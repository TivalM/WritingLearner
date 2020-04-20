package com.example.writinglearner.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.writinglearner.HttpUtil;
import com.example.writinglearner.MyWritingPad;
import com.example.writinglearner.R;
import com.github.gcacace.signaturepad.views.SignaturePad;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import okhttp3.Call;
import okhttp3.MediaType;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static org.apache.commons.lang3.StringEscapeUtils.unescapeJava;

public class WritingActivity extends AppCompatActivity {

    private MyWritingPad writingPad;
    private ImageButton bt_finish;
    private ImageButton bt_clear;
    private ImageView view_test;
    private TextView text_info;
    private TextView text_target;
    Queue<String> chars = new LinkedList<>();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final int imageSize = 128;
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);

        writingPad = findViewById(R.id.writing_pad);
        bt_finish = findViewById(R.id.button_finish);
        bt_clear = findViewById(R.id.button_clear);
        view_test = findViewById(R.id.imageView_test);
        text_target = findViewById(R.id.text_target);
        text_info = findViewById(R.id.text_info);
        final int image_size = 64;
        // TextView character = (TextView) this.findViewById(R.id.textView2);
        // character.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/simkai.ttf"));
        setPad();
        bindButton();
        initCharacters();
        targetNext();
    }

    private void targetNext() {
        if (chars.isEmpty()) {
            initCharacters();
        }
        text_target.setText(chars.poll());
    }

    private void bindButton() {

        bt_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] PERMISSIONS_STORAGE = {
                        "android.permission.READ_EXTERNAL_STORAGE",
                        "android.permission.WRITE_EXTERNAL_STORAGE"};
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

                try {
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "data.my");

                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(writingImageBytes);
                    fos.flush();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 绘图验证
//                Bitmap baseBitmap = Bitmap.createBitmap(view_test.getWidth(), view_test.getHeight(), Bitmap.Config.ARGB_8888);
//                //Bitmap baseBitmap = targetBitmap;
//                Canvas canvas = new Canvas(baseBitmap);
//                canvas.drawColor(Color.WHITE);
//                Paint p = new Paint();
//                p.setColor(Color.BLACK);
//                for (int j = 0; j < paths.size(); j++)
//                    for (int i = 0; i < paths.get(j).size() - 1; i++) {
//                        canvas.drawLine(paths.get(j).get(i).x, paths.get(j).get(i).y, paths.get(j).get(i + 1).x, paths.get(j).get(i + 1).y, p);
//                    }
//                view_test.setImageBitmap(baseBitmap);
//                view_test.setVisibility(View.VISIBLE);

                targetNext();
            }
        });

        bt_clear.setOnClickListener(v -> {
                writingPad.clear();
            writingPad.clean_paths();
            view_test.setVisibility(View.GONE);
            text_info.setText("");
            Toast.makeText(WritingActivity.this, "清理完成",
                    Toast.LENGTH_SHORT).show();
        });
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

    private void initCharacters() {
        chars.add("且");
        chars.add("世");
        chars.add("交");
    }

    public void postImg(byte[] imgWrittenData, byte[] targetImageBytes) throws JSONException, IOException {
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
                text_info.setText(unescapeJava(res_json));
            }
        });
    }

}