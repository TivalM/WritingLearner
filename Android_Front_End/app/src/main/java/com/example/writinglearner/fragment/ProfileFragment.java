package com.example.writinglearner.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.writinglearner.HttpUtil;
import com.example.writinglearner.R;
import com.example.writinglearner.activity.MainActivity;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.writinglearner.activity.MainActivity.JSON;
import static org.apache.commons.lang3.StringEscapeUtils.unescapeJava;

public class ProfileFragment extends Fragment {
    private Button bt_login;
    private Button bt_register;
    private Button bt_confirm;
    private Button bt_logout;
    private ImageView imageView_head;
    private TextView hint_account;
    private TextView hint_password;
    private TextView hint_nickname;
    private TextView text_profile;
    private TextView text_account;
    private TextView text_nickname;
    private EditText et_account;
    private EditText et_password;
    private EditText et_nickname;
    private ProgressBar loading_bar;
    private String cookie_stored;
    //    private String account;
//    private String password;
//    private String nickname;
    private Activity mainActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainActivity = getActivity();
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        cookie_stored = "";
        bt_login = getActivity().findViewById(R.id.bt_login);
        bt_register = getActivity().findViewById(R.id.bt_register);
        bt_confirm = getActivity().findViewById(R.id.bt_confirm);
        bt_logout = getActivity().findViewById(R.id.bt_logout);
        loading_bar = getActivity().findViewById(R.id.loading_bar);
        imageView_head = getActivity().findViewById(R.id.imageView_head);
        et_account = getActivity().findViewById(R.id.editText_account);
        et_password = getActivity().findViewById(R.id.editText_password);
        et_nickname = getActivity().findViewById(R.id.editText_nickname);
        hint_account = getActivity().findViewById(R.id.text_account);
        hint_password = getActivity().findViewById(R.id.text_password);
        hint_nickname = getActivity().findViewById(R.id.text_nickname);
        text_account = getActivity().findViewById(R.id.text_login_account);
        text_nickname = getActivity().findViewById(R.id.text_login_nickname);
        text_profile = getActivity().findViewById(R.id.text_profile);
        inti_when_start();
        bindButton();
    }

    private void inti_when_start() {
        //初始时呈现登陆界面
        bt_login.setVisibility(View.GONE);
        bt_logout.setVisibility(View.GONE);
        hint_nickname.setVisibility(View.GONE);
        et_nickname.setVisibility(View.GONE);
        loading_bar.setVisibility(View.INVISIBLE);
    }

    private void bindButton() {
        bt_login.setOnClickListener(v -> {
            //切换到登陆模式，登陆按钮消失，昵称框消失，登陆按钮出现
            bt_register.setVisibility(View.VISIBLE);
            bt_login.setVisibility(View.GONE);
            hint_nickname.setVisibility(View.GONE);
            et_nickname.setVisibility(View.GONE);
        });
        bt_register.setOnClickListener(v -> {
            //切换到注册模式，注册按钮消失，昵称框出现，登陆按钮出现
            bt_register.setVisibility(View.GONE);
            bt_login.setVisibility(View.VISIBLE);
            hint_nickname.setVisibility(View.VISIBLE);
            et_nickname.setVisibility(View.VISIBLE);
        });
        bt_confirm.setOnClickListener(v -> {
            if (bt_login.getVisibility() == View.GONE) {
                //登陆模式，执行登陆操作
                String account = et_account.getText().toString();
                String password = et_password.getText().toString();
                if (!account.equals("") && !password.equals("")) {
                    login(account, password);

                    //TODO页面切换和更新
                } else
                    Toast.makeText(getActivity(), "请输入必要信息", Toast.LENGTH_SHORT).show();
            } else if (bt_register.getVisibility() == View.GONE) {
                //注册模式，执行注册操作
                String account = et_account.getText().toString();
                String password = et_password.getText().toString();
                String nickname = et_nickname.getText().toString();
                if (!account.equals("") && !password.equals("") && !nickname.equals("")) {
                    try {
                        loading_bar.setVisibility(View.VISIBLE);
                        text_profile.setText("正在分配账户，注册成功后将自动登录");
                        if (((MainActivity) mainActivity).isGlobalClickable())
                            ((MainActivity) mainActivity).changeGlobalClickableSate();
                        register(account, password, nickname);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                    Toast.makeText(getActivity(), "请输入必要信息", Toast.LENGTH_SHORT).show();
            }
        });
        bt_logout.setOnClickListener(v -> {
            //回到注册模式
            String account = text_account.getText().toString();
            if (!account.equals("") && !cookie_stored.equals("")) {
                logout(account, cookie_stored);
            }
        });
    }

    private void logout(String account, String cookie) {
        Map<String, String> headers = new HashMap<>();
        headers.put("account", account);
        headers.put("cookie", cookie);

        HttpUtil.sendGetRequest("users/logout/", headers, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                text_profile.setText("注销失败");
                Log.d("http2", "Logout Failure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                assert response.body() != null;
                String res_json = response.body().string();
                Log.d("http3", res_json);
                JsonObject jsonObject = (JsonObject) new JsonParser().parse(res_json).getAsJsonObject();
                int state = jsonObject.get("state").getAsInt();
                String desc = jsonObject.get("description").getAsString();
                Handler handler = ((MainActivity) mainActivity).getMainHandler();
                if (state == 0) {
                    //更新Ui
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            flushEditText();
                            text_profile.setText("");
                            text_account.setVisibility(View.GONE);
                            text_nickname.setVisibility(View.GONE);
                            et_account.setVisibility(View.VISIBLE);
                            hint_password.setVisibility(View.VISIBLE);
                            hint_nickname.setVisibility(View.GONE);
                            et_password.setVisibility(View.VISIBLE);
                            bt_confirm.setVisibility(View.VISIBLE);
                            bt_register.setVisibility(View.VISIBLE);
                            bt_logout.setVisibility(View.GONE);
                        }
                    });
                    //重置系统
                    ((MainActivity) mainActivity).logout_system();
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            flushEditText();
                            text_profile.setText(desc);
                        }
                    });
                }
            }
        });
    }

    private void register(String account, String password, String nickname) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("account", account);
        jsonObject.put("password", password);
        jsonObject.put("name", nickname);
        String json = jsonObject.toString();
        RequestBody body = RequestBody.create(JSON, json);
        HttpUtil.sendPostRequest("users/register/", body, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                text_profile.setText("注册失败");
                Log.d("http3", "Register Failure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                assert response.body() != null;
                String res_json = response.body().string();
                Log.d("http3", res_json);
                JsonObject jsonObject = (JsonObject) new JsonParser().parse(res_json).getAsJsonObject();
                int state = jsonObject.get("state").getAsInt();
                String desc = jsonObject.get("description").getAsString();
                Handler handler = ((MainActivity) mainActivity).getMainHandler();
                if (state == 0) {
//                  String welcome = "注册成功，使用用户 " + nickname + " 登陆";
                    //更新Ui
                    handler.post(() -> {
                        loading_bar.setVisibility(View.INVISIBLE);
                        if (!((MainActivity) mainActivity).isGlobalClickable())
                            ((MainActivity) mainActivity).changeGlobalClickableSate();
                        et_nickname.setVisibility(View.GONE);
                        text_nickname.setText(nickname);
//                        text_profile.setText(welcome);
                    });
                    login(account, password);
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            flushEditText();
                            text_profile.setText(desc);
                            loading_bar.setVisibility(View.INVISIBLE);
                            if (!((MainActivity) mainActivity).isGlobalClickable())
                                ((MainActivity) mainActivity).changeGlobalClickableSate();
                        }
                    });
                }
            }
        });
    }

    private void login(String account, String password) {
        Map<String, String> headers = new HashMap<>();
        Log.d("http3", account + password);
        headers.put("account", account);
        headers.put("password", password);

        HttpUtil.sendGetRequest("users/login/", headers, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                text_profile.setText("登陆失败");
                Log.d("http2", "Login Failure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                assert response.body() != null;
                String res_json = response.body().string();
                JsonObject jsonObject = new JsonParser().parse(res_json).getAsJsonObject();
                int state = jsonObject.get("state").getAsInt();
                String desc = jsonObject.get("description").getAsString();
                Log.d("http3", res_json);
                Handler handler = ((MainActivity) mainActivity).getMainHandler();
                if (state == 0) {
                    String welcome = "欢迎 " + desc;
                    //更新Ui
                    handler.post(() -> {
                        flushEditText();
                        text_profile.setText(welcome);
                        text_account.setText(account);
                        text_nickname.setText(desc);
                        text_account.setVisibility(View.VISIBLE);
                        text_nickname.setVisibility(View.VISIBLE);
                        et_account.setVisibility(View.GONE);
                        hint_password.setVisibility(View.GONE);
                        hint_nickname.setVisibility(View.VISIBLE);
                        et_password.setVisibility(View.GONE);
                        bt_login.setVisibility(View.GONE);
                        bt_confirm.setVisibility(View.GONE);
                        bt_register.setVisibility(View.GONE);
                        bt_logout.setVisibility(View.VISIBLE);
                    });
                    //向主线程传递Cookie
                    cookie_stored = jsonObject.get("cookie").getAsString();
                    Message cookie_msg = new Message();
                    cookie_msg.obj = cookie_stored;
                    cookie_msg.what = 1;
                    handler.sendMessage(cookie_msg);
                    Message account_msg = new Message();
                    account_msg.obj = account;
                    account_msg.what = 2;
                    handler.sendMessage(account_msg);
                    //登录后更新历史
                    if (((MainActivity) mainActivity).isGlobalClickable())
                        ((MainActivity) mainActivity).changeGlobalClickableSate();
                    Log.d("history", account + cookie_stored);
                    if (!account.equals("") && !cookie_stored.equals("")) {
                        initHistory(account, cookie_stored);
                    }
                } else {
                    handler.post(() -> {
                        text_profile.setText(desc);
                    });
                }
            }
        });
    }

    private void initHistory(String account, String cookie) {
        //获取Charset
        Log.d("history", "history getting");
        Map<String, String> headers = new HashMap<>();
        Log.d("history", account + cookie);
        headers.put("account", account);
        headers.put("cookie", cookie);
        HttpUtil.sendGetRequest("users/get_charset", headers, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                text_profile.setText("获取历史失败");
                Log.d("history", "history Failure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                assert response.body() != null;
                Log.d("history", "history get");
                String res_json = unescapeJava(response.body().string());
                Log.d("history", res_json);
//                String res_json = response.body().toString();
                JsonObject result = new JsonParser().parse(res_json).getAsJsonObject();
                int state = result.get("state").getAsInt();
                if (state == 0) {
                    JsonArray charsetJson = result.getAsJsonArray("data");
                    ((MainActivity) mainActivity).initCharset(charsetJson);
                }

            }
        });

        //获取用户历史
        HttpUtil.sendGetRequest("users/get_history", headers, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                text_profile.setText("获取个人历史失败");
                Log.d("history", "personal history Failure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                assert response.body() != null;
                Log.d("history", "personal history get");
                String res_json = unescapeJava(response.body().string());
                Log.d("history", res_json);
                JsonObject result = new JsonParser().parse(res_json).getAsJsonObject();
                int state = result.get("state").getAsInt();
                if (state == 0) {
                    JsonArray historyJson = result.getAsJsonArray("data");
                    try {
                        ((MainActivity) mainActivity).updatePersonalHistory(historyJson);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }

    private void flushEditText() {
        et_nickname.setText("");
        et_account.setText("");
        et_password.setText("");
        text_account.setText("");
        text_nickname.setText("");
        text_profile.setText("");
    }
}
