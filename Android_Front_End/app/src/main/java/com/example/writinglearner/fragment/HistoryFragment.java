package com.example.writinglearner.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class HistoryFragment extends Fragment {
    private Activity mainActivity;
    private RecyclerView recyclerView;
    private EachCharacterAdapter adapter;
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
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 4);
        recyclerView.setLayoutManager(layoutManager);
    }

    public void initCharas() {
        List<EachCharacter> charset = ((MainActivity) mainActivity).getCharset();
        adapter = new EachCharacterAdapter(charset);
        recyclerView.setAdapter(adapter);
        if (!((MainActivity) mainActivity).isGlobalClickable())
            ((MainActivity) mainActivity).changeGlobalClickableSate();
    }

}
