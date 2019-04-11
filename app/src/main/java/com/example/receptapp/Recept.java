package com.example.receptapp;

import android.support.annotation.DrawableRes;
import android.util.Log;

import java.util.ArrayList;

public class Recept {
    String title;
    String description;
    ArrayList<String> ingredients;
    String instructions;
    ArrayList<String>tags;
    boolean favorite;
    int image;

    public Recept(){};

    public Recept(String title, String description, ArrayList ingredients, String instructions, ArrayList tags, boolean favorite) {
        this.title = title;
        this.description = description;
        this.image = R.drawable.ic_restaurant_color_24dp;
        Log.d("!!!", "Recept: " + R.drawable.ic_restaurant_color_24dp);
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.tags = tags;
        this.favorite = favorite;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public int getImage() {
        return R.drawable.ic_restaurant_color_24dp;
    }

    public void setImage(int image) {
        this.image = R.drawable.ic_restaurant_color_24dp;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}

