package com.example.writinglearner.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.writinglearner.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("HelloWorldActivity", "onCreate execute");
        Button button_start = (Button) findViewById(R.id.button_start);

        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //气泡
                Toast.makeText(MainActivity.this, "准备开始练习书写",
                        Toast.LENGTH_SHORT).show();
                //切换界面
                Intent intent_to_write = new Intent(MainActivity.this, WritingActivity.class);
                startActivity(intent_to_write);
            }
        });
    }

    private void loadChars(String path) throws FileNotFoundException {
        InputStream stream = new FileInputStream("G:\\HandWriting\\HWDB1\\char_dict");
    }
}
