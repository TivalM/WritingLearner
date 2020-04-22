package com.example.writinglearner.entity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.writinglearner.R;
import com.example.writinglearner.activity.MainActivity;

import java.util.List;

public class EachCharacterAdapter extends RecyclerView.Adapter<EachCharacterAdapter.ViewHolder> {

    private Context mContext;
    private List<EachCharacter> eachCharacters;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView text_id;
        TextView text_state;
        TextView text_itself;

        public EachCharacter getCharacterContained() {
            return new EachCharacter(
                    Integer.parseInt(text_id.getText().toString()),
                    text_itself.getText().toString(),
                    text_state.getText().toString());
        }

        public ViewHolder(@NonNull View view) {
            super(view);
            cardView = (CardView) view;
            text_id = view.findViewById(R.id.text_id);
            text_itself = view.findViewById(R.id.text_chara);
            text_state = view.findViewById(R.id.text_state);
        }
    }

    public EachCharacterAdapter(List<EachCharacter> charaList) {
        eachCharacters = charaList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null)
            mContext = parent.getContext();
        //mContext 即 MainActivity
        View view = LayoutInflater.from(mContext).inflate(R.layout.chara_item,
                parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EachCharacter c = holder.getCharacterContained();
                ((MainActivity) mContext).notifyLearnSpecificChar(c);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EachCharacter eachCharacter = eachCharacters.get(position);
        holder.text_id.setText(String.valueOf(eachCharacter.getId()));
        holder.text_itself.setText(eachCharacter.getItself());
        holder.text_state.setText(eachCharacter.getLearning_state());
    }

    @Override
    public int getItemCount() {
        return eachCharacters.size();
    }
}
