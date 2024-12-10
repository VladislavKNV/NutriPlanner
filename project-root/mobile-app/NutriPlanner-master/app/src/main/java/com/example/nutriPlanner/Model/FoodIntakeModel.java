package com.example.nutriPlanner.Model;

public class FoodIntakeModel {
    private int id;
    private int userId;
    private int foodId;
    private String mealType;
    private String date;

    public FoodIntakeModel(int userId, int foodId, String mealType, String date) {
        this.userId = userId;
        this.foodId = foodId;
        this.mealType = mealType;
        this.date = date;
    }

    public FoodIntakeModel(int id, int userId, int foodId, String mealType, String date) {
        this.id = id;
        this.userId = userId;
        this.foodId = foodId;
        this.mealType = mealType;
        this.date = date;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getFoodId() { return foodId; }
    public void setFoodId(int foodId) { this.foodId = foodId; }

    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
