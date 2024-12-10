package com.example.nutriPlanner.Model;

public class CartModel { //не реализовано
    private int id;
    private String date;
    private String ingredients;
    private boolean isComplete;

    public CartModel(String date, String ingredients, boolean isComplete) {
        this.date = date;
        this.ingredients = ingredients;
        this.isComplete = isComplete;
    }

    public CartModel(int id, String date, String ingredients, boolean isComplete) {
        this.id = id;
        this.date = date;
        this.ingredients = ingredients;
        this.isComplete = isComplete;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    public boolean isComplete() { return isComplete; }
    public void setComplete(boolean complete) { isComplete = complete; }
}

