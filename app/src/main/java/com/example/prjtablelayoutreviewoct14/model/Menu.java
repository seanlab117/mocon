package com.example.prjtablelayoutreviewoct14.model;

import android.graphics.Color;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Menu implements Serializable {

    private int id;
    private String description;
    private int textColor;
    private int backgroundColor;

    public Menu(int id, String description, int textColor, int backgroundColor) {
        this.id = id;
        this.description = description;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
    }

    public Menu(int id, String description) {
        this.id = id;
        this.description = description;
        this.textColor = Color.BLACK;
        this.backgroundColor = Color.WHITE;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @NonNull
    @Override
    public String toString() {
        return this.description;
    }
}
