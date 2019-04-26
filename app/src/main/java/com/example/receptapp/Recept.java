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
    String imageLink;
    String recipeID;
    String creator;

    public Recept(){};

    public String getRecepeID() {
        return recipeID;
    }

    public void setRecepeID(String recipeID) {
        this.recipeID = recipeID;
    }

    public Recept(String title, String description, ArrayList ingredients, String instructions, ArrayList tags, String recipeID, String imageLink, String creator) {
        this.title = title;
        this.description = description;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.tags = tags;
        this.recipeID = recipeID;
        this.imageLink = imageLink;
        this.creator = creator;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
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

}

