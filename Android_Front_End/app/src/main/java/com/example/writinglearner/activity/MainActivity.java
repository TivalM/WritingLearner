package com.example.writinglearner.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;

import com.example.writinglearner.R;
import com.example.writinglearner.fragment.ProfileFragment;
import com.example.writinglearner.fragment.WritingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

import okhttp3.MediaType;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationView navigationBar;
    private WritingFragment writingFragment;
    private ProfileFragment profileFragment;
    private String user_account;
    private String user_cookie;
    @SuppressLint("HandlerLeak")
    private Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    Log.i("handler", "第一个消息是：" + msg.obj);
                    break;

                case 2:
                    Log.i("handler", "第二个消息是：" + msg.obj);
                    break;
            }
        }
    };


    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(this.getSupportActionBar()).hide();
        Log.d("HelloWorldActivity", "onCreate execute");
        writingFragment = new WritingFragment();
        profileFragment = new ProfileFragment();
        intiNavigationBar();
        switchFragment(profileFragment = new ProfileFragment());
    }

    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_empty, fragment);
        transaction.commit();
    }

    public void intiNavigationBar() {
        navigationBar = findViewById(R.id.bottomNavigationView);
        navigationBar.setOnNavigationItemSelectedListener(this);//设置导航栏监听器
        navigationBar.setSelectedItemId(R.id.navigation_profile);//设置个人信息页为首页
        navigationBar.setItemIconTintList(null);//取消导航栏子项图片的颜色覆盖
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();//获取点击的位置以及对应的id
        switch (itemId) {
            case R.id.navigation_writing:
                switchFragment(writingFragment);//id为tab_one则第一项被点击，遂用HomeFragment替换空Fragment
                menuItem.setChecked(true);
                break;
//            case R.id.navigation_history:
//                replaceFragment(new );
//                menuItem.setChecked(true);
//                break;
            case R.id.navigation_profile:
                switchFragment(profileFragment);
                menuItem.setChecked(true);
                break;
        }
        return false;
    }

    public Handler getMainHandler() {
        return mainHandler;
    }
}
