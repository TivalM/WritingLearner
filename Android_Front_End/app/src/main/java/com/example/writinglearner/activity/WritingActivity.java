package com.example.writinglearner.activity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.writinglearner.MyWritingPad;
import com.example.writinglearner.R;
import com.github.gcacace.signaturepad.views.SignaturePad;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

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


    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException, FileNotFoundException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }


    private void bindButton() {

        bt_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取图片
                Bitmap writingBitmap = writingPad.getSignatureBitmap();
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
}
