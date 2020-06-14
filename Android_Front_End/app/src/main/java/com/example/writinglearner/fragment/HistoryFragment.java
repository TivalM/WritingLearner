package com.example.writinglearner.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.writinglearner.R;
import com.example.writinglearner.activity.MainActivity;
import com.example.writinglearner.entity.EachCharacterAdapter;
import com.example.writinglearner.entity.EachCharacter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HistoryFragment extends Fragment {
    private Activity mainActivity;
    private RecyclerView recyclerView;
    private EachCharacterAdapter adapter;
    private EditText text_search;
    private ImageButton bt_search;
    //应当移动到mainActivity方便更新
//    private List<EachCharacter> characters = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainActivity = getActivity();
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView = getActivity().findViewById(R.id.recycler_view);
        text_search = getActivity().findViewById(R.id.text_search);
        bt_search = getActivity().findViewById(R.id.bt_search);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 4);
        recyclerView.setLayoutManager(layoutManager);
    }

    public void initCharas() {
        List<EachCharacter> charset = ((MainActivity) mainActivity).getCharset();
        adapter = new EachCharacterAdapter(charset);
        recyclerView.setAdapter(adapter);
        if (!((MainActivity) mainActivity).isGlobalClickable())
            ((MainActivity) mainActivity).changeGlobalClickableSate();
        bindButton();
    }

    public void freshSpecificCharacter(int id) {
        adapter.notifyItemChanged(id);
    }

    private void bindButton() {
        bt_search.setOnClickListener(v -> {
            String search_condition = text_search.getText().toString();
            List<EachCharacter> charset = ((MainActivity) mainActivity).getCharset();
            List<EachCharacter> filteredSet = new ArrayList<>();
            if (search_condition.equals("")) {
                //全部显示
                adapter.setFilter(charset);
            } else if (isInteger(search_condition) && Integer.parseInt(search_condition) > 0 && Integer.parseInt(search_condition) < 600) {
                //按id查找
                filteredSet.add(charset.get(Integer.parseInt(search_condition) - 1));
                adapter.setFilter(filteredSet);
            } else if (search_condition.length() == 1 && (19968 <= search_condition.charAt(0) && search_condition.charAt(0) < 40869)) {
                //是单个汉字
                for (int i = 0; i < charset.size(); i++)
                    if (charset.get(i).getItself().equals(search_condition))
                        filteredSet.add(charset.get(i));
                if (filteredSet.size() > 0)
                    adapter.setFilter(filteredSet);
                else Toast.makeText(getActivity(), "未找到对应字", Toast.LENGTH_SHORT).show();
            } else if (search_condition.equals("LR") || search_condition.equals("FD") || search_condition.equals("NL")) {
                filteredSet = charset.stream()
                        .filter(eachChar -> eachChar.getLearning_state().equals(search_condition))
                        .collect(Collectors.toList());
                adapter.setFilter(filteredSet);
            } else
                Toast.makeText(getActivity(), "条件无效", Toast.LENGTH_SHORT).show();
        });
    }

    public static boolean isInteger(String input) {
        Matcher mer = Pattern.compile("^[+-]?[0-9]+$").matcher(input);
        return mer.find();
    }
}