package com.example.nutriPlanner.Model.ApiModels;

public class AddFoodIntakeRequest {
    private int userId;
    private int foodId;
    private String mealType;
    private String date;

    public AddFoodIntakeRequest(int userId, int foodId, String mealType, String date) {
        this.userId = userId;
        this.foodId = foodId;
        this.mealType = mealType;
        this.date = date;
    }

}
