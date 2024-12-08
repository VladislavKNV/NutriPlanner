package com.example.nutriPlanner.Model;

public class FavoriteFoodModel {

    private int id;
    private int userId;
    private int foodId;

    public FavoriteFoodModel(int userId, int foodId) {
        this.userId = userId;
        this.foodId = foodId;
    }

    public FavoriteFoodModel(int id, int userId, int foodId) {
        this.id = id;
        this.userId = userId;
        this.foodId = foodId;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getFoodId() { return foodId; }
    public void setFoodId(int foodId) { this.foodId = foodId; }
}
