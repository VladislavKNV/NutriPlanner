package com.example.nutriPlanner.Model.ApiModels;

public class AddFavoriteFoodRequest {
    private int userId;
    private int foodId;

    public AddFavoriteFoodRequest(int userId, int foodId) {
        this.userId = userId;
        this.foodId = foodId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }
}

