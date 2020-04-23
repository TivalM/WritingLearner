package com.example.writinglearner.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.example.writinglearner.HttpUtil;
import com.example.writinglearner.R;
import com.example.writinglearner.entity.EachCharacter;
import com.example.writinglearner.fragment.HistoryFragment;
import com.example.writinglearner.fragment.ProfileFragment;
import com.example.writinglearner.fragment.WritingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView navigationBar;
    FragmentManager fragmentManager;
    private WritingFragment writingFragment;
    private ProfileFragment profileFragment;
    private HistoryFragment historyFragment;
    private Fragment activeFragment;


    private boolean isGlobalClickable;


    private String user_account;
    private String nickname;
    private String user_cookie;

    private EachCharacter learningChar;
    private List<EachCharacter> charset;

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
        isGlobalClickable = true;
        writingFragment = new WritingFragment();
        profileFragment = new ProfileFragment();
        historyFragment = new HistoryFragment();
        fragmentManager = getSupportFragmentManager();
        activeFragment = profileFragment;
        charset = new ArrayList<>(600);
        intiNavigationBar();
    }

    public void intiNavigationBar() {
        navigationBar = findViewById(R.id.bottomNavigationView);
        navigationBar.setOnNavigationItemSelectedListener(mNavigationListener);//设置导航栏监听器
        navigationBar.setSelectedItemId(R.id.navigation_profile);//设置个人信息页为首页
        navigationBar.setItemIconTintList(null);//取消导航栏子项图片的颜色覆盖
        fragmentManager.beginTransaction().add(R.id.fragment_empty, profileFragment, "1").commit();
        fragmentManager.beginTransaction().add(R.id.fragment_empty, historyFragment, "2").hide(historyFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_empty, writingFragment, "3").hide(writingFragment).commit();
    }

    public void initCharset(JsonArray json) {
        charset.clear();
        Gson gson = new Gson();
        json.forEach((dt) ->
        {
            if (dt.isJsonObject()) {
                EachCharacter entry = gson.fromJson(dt, EachCharacter.class);
                charset.add(entry);
            }
        });
        Log.d("history", charset.get(0).toString());
        if (!charset.isEmpty())
            historyFragment.initCharas();
    }

    public void logout_system() {
        isGlobalClickable = true;
        user_account = "";
        user_cookie = "";
        charset = new ArrayList<>(600);

    }

    public void notifyLearnSpecificChar(EachCharacter c) {
        //通知WritingFragment更新
        if (!c.getItself().equals("")) {
            learningChar = c;
            writingFragment.learnSpecificChar(learningChar.getId(), learningChar.getItself());
            navigationBar.setSelectedItemId(R.id.navigation_writing);
        }
    }

    public void updateHistory(String state) {
        charset.get(learningChar.getId()).changeStateTo(state);
        Map<String, String> headers = new HashMap<>();
        Log.d("history", "update history");
        headers.put("char_id", String.valueOf(learningChar.getId()));
        if (state.equals("Not Learned"))
            headers.put("state", "NL");
        else if(state.equals("Learning"))
            headers.put("state", "LR");
        else if(state.equals("finished"))
            headers.put("state", "LR");
        HttpUtil.sendGetRequest("users/change_learning_state/", );
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mNavigationListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.navigation_profile:
                            fragmentManager.beginTransaction().hide(activeFragment).show(profileFragment).commit();
                            activeFragment = profileFragment;
                            return true;

                        case R.id.navigation_history:
                            fragmentManager.beginTransaction().hide(activeFragment).show(historyFragment).commit();
                            activeFragment = historyFragment;
                            return true;

                        case R.id.navigation_writing:
                            fragmentManager.beginTransaction().hide(activeFragment).show(writingFragment).commit();
                            activeFragment = writingFragment;
                            return true;
                    }
                    return false;
                }
            };

    public Handler getMainHandler() {
        return mainHandler;
    }

    public List<EachCharacter> getCharset() {
        return charset;
    }

    public String getUser_account() {
        return user_account;
    }

    public String getNickname() {
        return nickname;
    }

    public String getUser_cookie() {
        return user_cookie;
    }

    public EachCharacter getLearningChar() {
        return learningChar;
    }

    public void changeGlobalClickableSate() {
        isGlobalClickable = !isGlobalClickable;
    }

    public boolean isGlobalClickable() {
        return isGlobalClickable;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (isGlobalClickable)
            super.dispatchTouchEvent(event);
        return isGlobalClickable;
    }
}
