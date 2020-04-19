package com.example.writinglearner.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.writinglearner.R;

public class LogFragment extends Fragment {
    private Button bt_login;
    private Button bt_register;
    private Button bt_confirm;
    private ImageView imageView_head;
    private EditText et_account;
    private EditText et_password;
    private EditText et_nickname;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log, container, false);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bt_login = (Button) getActivity().findViewById(R.id.bt_login);
        bt_register = (Button) getActivity().findViewById(R.id.bt_register);
        bt_confirm = (Button) getActivity().findViewById(R.id.bt_confirm);
        imageView_head = (ImageView) getActivity().findViewById(R.id.imageView_head);
        et_account = (EditText) getActivity().findViewById(R.id.editText_account);
        et_password = (EditText) getActivity().findViewById(R.id.editText_password);
        et_nickname = (EditText) getActivity().findViewById(R.id.editText_nickname);
        bindButton();
    }

    private void bindButton() {

    }
}
