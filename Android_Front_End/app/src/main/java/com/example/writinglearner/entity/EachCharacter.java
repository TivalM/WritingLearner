package com.example.writinglearner.entity;

import androidx.annotation.NonNull;

public class EachCharacter {
    private int char_id;
    private String char_itself;
    private String learning_state = "NL";

    public EachCharacter(int id, String itself, String learning_state) {
        this.char_id = id;

        this.char_itself = itself;
//        if (learning_state.equals("LR"))
//            this.learning_state = "LR";
//        else if (learning_state.equals("NL"))
//            this.learning_state = "NL";
//        else if (learning_state.equals("FD"))
//            this.learning_state = "FD";
//        else
        this.learning_state = learning_state;
    }

    public int getId() {
        return char_id;
    }

    public String getItself() {
        return char_itself;
    }

    public String getLearning_state() {
        return learning_state;
    }

    public void changeStateTo(String learning_state) {
        this.learning_state = learning_state;
    }

    @NonNull
    @Override
    public String toString() {
        return "id: " + char_id + " itself: " + char_itself + " learning_state: " + learning_state;
    }
}
