package com.example.writinglearner.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.writinglearner.MyWritingPad;
import com.example.writinglearner.R;
import com.github.gcacace.signaturepad.views.SignaturePad;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class WritingActivity extends AppCompatActivity {

    private MyWritingPad writingPad;
    private ImageButton bt_finish;
    private ImageButton bt_clear;
    private ImageView view_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);

        writingPad = (MyWritingPad) findViewById(R.id.writing_pad);
        bt_finish = (ImageButton) findViewById(R.id.button_finish);
        bt_clear = (ImageButton) findViewById(R.id.button_clear);
        view_test = (ImageView) findViewById(R.id.imageView_test);

        // TextView character = (TextView) this.findViewById(R.id.textView2);
        // character.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/simkai.ttf"));
        setPad();
        bindButton();

    }

    private void bindButton() {

        bt_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取图片
                String[] PERMISSIONS_STORAGE = {
                        "android.permission.READ_EXTERNAL_STORAGE",
                        "android.permission.WRITE_EXTERNAL_STORAGE"};
                Bitmap writingBitmap = writingPad.getSignatureBitmap();
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                writingBitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
                byte[] image_bytes = output.toByteArray();
                //PostMan测试
                InputStream input = postImg("http://192.168.31.245:8000/writingLearner/recognize_char/", image_bytes);
                Toast.makeText(WritingActivity.this, input.toString(),
                        Toast.LENGTH_SHORT).show();
                try {
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "data.my");

                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(image_bytes);
                    fos.flush();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // 获取笔画信息
                List<List<PointF>> paths = writingPad.getPaths();
                // 绘图验证
                Bitmap baseBitmap = Bitmap.createBitmap(view_test.getWidth(), view_test.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(baseBitmap);
                canvas.drawColor(Color.WHITE);
                Paint p = new Paint();
                p.setColor(Color.BLACK);
                for (int j = 0; j < paths.size(); j++)
                    for (int i = 0; i < paths.get(j).size() - 1; i++) {
                        canvas.drawLine(paths.get(j).get(i).x, paths.get(j).get(i).y, paths.get(j).get(i + 1).x, paths.get(j).get(i + 1).y, p);
                    }
                view_test.setImageBitmap(baseBitmap);
                view_test.setVisibility(View.VISIBLE);
                //传输图片
                // 传输笔画信息
                Toast.makeText(WritingActivity.this, "书写完成",
                        Toast.LENGTH_SHORT).show();
            }
        });
        bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writingPad.clear();
                writingPad.clean_paths();
                view_test.setVisibility(View.GONE);
                Toast.makeText(WritingActivity.this, "清理完成",
                        Toast.LENGTH_SHORT).show();
            }
        });
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

    private void initCharacter() {

    }


    public static InputStream postImg(String url, byte[] PostData) {
        URL u = null;
        HttpURLConnection con = null;
        InputStream inputStream = null;
        //尝试发送请求
        try {
            u = new URL(url);
            con = (HttpURLConnection) u.openConnection();
            con.setRequestMethod("POST");

            con.setDoOutput(true);
            con.setDoInput(true);
            con.setUseCaches(false);
//            con.setRequestProperty(“Content - Type”, “binary / octet - stream”);
            OutputStream outStream = con.getOutputStream();
            outStream.write(PostData);
            outStream.flush();
            outStream.close();
            //读取返回内容
            inputStream = con.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inputStream;
    }
}