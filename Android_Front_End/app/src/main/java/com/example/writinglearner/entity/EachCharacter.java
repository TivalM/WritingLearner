package com.example.writinglearner.entity;

import androidx.annotation.NonNull;

public class EachCharacter {
    private int id;
    private String itself;
    private String learning_state = "NL";

    public EachCharacter(int id, String itself, String learning_state) {
        this.id = id;
        this.itself = itself;
        this.learning_state = learning_state;
    }

    public int getId() {
        return id;
    }

    public String getItself() {
        return itself;
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
        return "id: " + id + " itself: " + itself + " learning_state: " + learning_state;
    }
}
