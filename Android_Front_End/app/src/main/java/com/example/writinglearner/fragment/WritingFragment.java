package com.example.writinglearner.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.icu.math.BigDecimal;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.writinglearner.HttpUtil;
import com.example.writinglearner.MyWritingPad;
import com.example.writinglearner.R;
import com.example.writinglearner.activity.MainActivity;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.writinglearner.activity.MainActivity.JSON;
import static org.apache.commons.lang3.StringEscapeUtils.unescapeJava;

public class WritingFragment extends Fragment {
    private MyWritingPad writingPad;
    private ImageButton bt_finish;
    private ImageButton bt_clear;
    private TextView text_info;
    private TextView text_target;
    private TextView text_parse;
    private Activity mainActivity;
    private String jsonResponse;
    private WebView webView;
    int learning_char_id; //id = charset下标 + 1
    int writing_state;
    public static final int imageSize = 128;
    OkHttpClient client = new OkHttpClient();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_writing, container, false);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity = getActivity();

        writingPad = getActivity().findViewById(R.id.writing_pad);
        bt_finish = getActivity().findViewById(R.id.button_finish);
        bt_clear = getActivity().findViewById(R.id.button_clear);
        text_target = getActivity().findViewById(R.id.text_target);
        text_info = getActivity().findViewById(R.id.text_info);
        text_parse = getActivity().findViewById(R.id.text_prase);
        webView = getActivity().findViewById(R.id.webView);
        WebView.setWebContentsDebuggingEnabled(true);
        setPad();
        bindButton();
        initWritingPanel();
        initWebView();
    }

    //    @SuppressLint("JavascriptInterface")
    private void initWebView() {

        //支持App内部javascript交互
        webView.getSettings().setJavaScriptEnabled(true);
        //设置不可缩放
        webView.getSettings().setSupportZoom(false);
        //设置是否出现缩放工具
        webView.getSettings().setBuiltInZoomControls(false);
        webView.loadUrl("file:///android_asset/character.html");

    }

    private void initWritingPanel() {
        jsonResponse = "";
        learning_char_id = -1; //id = charset下标 + 1
        text_target.setText("");
        writing_state = 0;
        bt_clear.setClickable(true);
        bt_finish.setClickable(false);
    }

    public void learnSpecificChar(int id, String characterItself) {
        ((MainActivity) mainActivity).updateHistoryWhenWriting("LR");
        text_info.setVisibility(View.VISIBLE);
        text_target.setText(characterItself);
        text_target.setVisibility(View.INVISIBLE);
        learning_char_id = id;
        jsonResponse = "";
        writing_state = -1;
        bt_clear.setClickable(true);
        bt_finish.setClickable(true);
        text_parse.setText("请观察写法");
        text_info.setText("");
        webView.setVisibility(View.VISIBLE);
        webView.loadUrl("javascript:learnSpecificChar(\"" + characterItself + "\")");
    }

    private void flushStateToNextParse() {
        jsonResponse = "";
        writingPad.clear();
    }

    private void bindButton() {

        bt_finish.setOnClickListener(v -> {
            boolean isCorrect = false;
            switch (writing_state) {
                case -1:
                    //播放动画
                    writing_state = 0;
                    text_parse.setText("第一步：描红");
                    text_target.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.INVISIBLE);
                    break;
                case 0:
                    //描红
                    try {
                        isCorrect = CharacterRecognize();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (isCorrect) {
                        //进入下一阶段：重复练习
                        writing_state = 1;
                        text_parse.setText("第二步：重复练习");
                    }
                    break;
                case 1:
                    //重复练习
                    try {
                        isCorrect = CharacterRecognize();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (isCorrect) {
                        //进入下一阶段：无提示练习
                        writing_state = 2;
                        text_target.setVisibility(View.INVISIBLE);
                        text_parse.setText("第三步：无提示练习");
                    }
                    break;
                case 2:
                    //描红
                    try {
                        isCorrect = CharacterRecognize();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (isCorrect) {
                        //正确，提交记录
                        text_parse.setText("成功！您已完成该字符的练习，请选择其它字符");
                        ((MainActivity) mainActivity).updateHistoryWhenWriting("FD");
                    }
                    break;
            }
            flushStateToNextParse();
        });

        bt_clear.setOnClickListener(v -> {
            writingPad.clear();
            writingPad.clean_paths();
            text_info.setText("");
        });
    }

    private boolean CharacterRecognize() throws InterruptedException {
        //获取标的图片
        Bitmap targetBitmap = targetTextViewToImage(text_target);
        // 获取手写图片
        Bitmap writingBitmap = writingPad.getSignatureBitmap();

        writingBitmap = Bitmap.createScaledBitmap(writingBitmap, imageSize,
                imageSize, true);
        targetBitmap = Bitmap.createScaledBitmap(targetBitmap, imageSize,
                imageSize, true);
        // 获取笔画信息
        List<List<PointF>> paths = writingPad.getPaths();

        ByteArrayOutputStream output1 = new ByteArrayOutputStream();
        ByteArrayOutputStream output2 = new ByteArrayOutputStream();
        writingBitmap.compress(Bitmap.CompressFormat.PNG, 100, output1);
        byte[] writingImageBytes = output1.toByteArray();
        targetBitmap.compress(Bitmap.CompressFormat.PNG, 100, output2);
        byte[] targetImageBytes = output2.toByteArray();

        try {
            postImg(writingImageBytes, targetImageBytes);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        int count = 0;
        while (jsonResponse.equals("") && count < 300) {
            Thread.sleep(10);
            count++;
        }
        if (count == 300 || count > 3000)
            return false;
        JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonResponse).getAsJsonObject();
        String character = jsonObject.get("char").getAsString();
        double similarity = jsonObject.get("similarity").getAsDouble();
        BigDecimal bg = new BigDecimal(similarity);
        double similarity_pretty = bg.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (character.equals(text_target.getText().toString())) {
            String info = "您书写的 “" + character + "” 字正确，相似度为 " + similarity_pretty;
            text_info.setText(info);
            return true;
        } else {
            String info = "书写错误，您书写的好像是 “" + character + "” 字";
            text_info.setText(info);
            return false;
        }

//        try {
//            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "data.my");
//
//            FileOutputStream fos = new FileOutputStream(file);
//            fos.write(writingImageBytes);
//            fos.flush();
//            fos.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private Bitmap targetTextViewToImage(TextView textView) {

        textView.setDrawingCacheEnabled(true);
        textView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        textView.setDrawingCacheBackgroundColor(Color.WHITE);

        int w = textView.getWidth();
        int h = textView.getHeight();
        Bitmap targetBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(targetBitmap);
        c.drawColor(Color.WHITE);

        textView.setTextColor(Color.BLACK);
//        textView.layout(0, 0, w, h);
        textView.draw(c);
        textView.setTextColor(Color.parseColor("#ED9E9E"));
        return targetBitmap;
    }

    private void setPad() {
        writingPad.setMaxWidth(12);
        writingPad.setMinWidth(8);
        writingPad.setVelocityFilterWeight(0.6f);

        writingPad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {

            }

            @Override
            public void onSigned() {
                bt_clear.setEnabled(true);
                bt_finish.setEnabled(true);
            }

            @Override
            public void onClear() {
                bt_clear.setEnabled(false);
                bt_finish.setEnabled(false);
            }
        });
    }

    private void postImg(byte[] imgWrittenData, byte[] targetImageBytes) throws JSONException, IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("img_written", Base64.encodeToString(imgWrittenData, Base64.DEFAULT));
        jsonObject.put("img_target", Base64.encodeToString(targetImageBytes, Base64.DEFAULT));
        jsonObject.put("strokes", "");
        String json = jsonObject.toString();
        RequestBody body = RequestBody.create(JSON, json);
        HttpUtil.sendPostRequest("writingLearner/recognize_char/", body, new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("http2", "Post Image Fail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                assert response.body() != null;
                String res_json = response.body().string();
                Log.d("http3", res_json);
                jsonResponse = unescapeJava(res_json);
            }
        });
    }

}
