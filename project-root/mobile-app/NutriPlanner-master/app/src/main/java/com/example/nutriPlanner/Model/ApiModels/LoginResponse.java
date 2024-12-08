package com.example.nutriPlanner.Model.ApiModels;

import com.example.nutriPlanner.Model.BMIModel;
import com.example.nutriPlanner.Model.FavoriteFoodModel;
import com.example.nutriPlanner.Model.FoodIntakeModel;
import com.example.nutriPlanner.Model.FoodModel;
import com.example.nutriPlanner.Model.UserModel;

import java.util.List;

public class LoginResponse {
    private UserModel user;
    private List<BMIModel> bmi;
    private List<FavoriteFoodModel> favorite_food;
    private List<FoodIntakeModel> food_intake;
    private List<FoodModel> foods;

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public List<BMIModel> getBmi() {
        return bmi;
    }

    public void setBmi(List<BMIModel> bmi) {
        this.bmi = bmi;
    }

    public List<FavoriteFoodModel> getFavorite_food() {
        return favorite_food;
    }

    public void setFavorite_food(List<FavoriteFoodModel> favorite_food) {
        this.favorite_food = favorite_food;
    }

    public List<FoodIntakeModel> getFood_intake() {
        return food_intake;
    }

    public void setFood_intake(List<FoodIntakeModel> food_intake) {
        this.food_intake = food_intake;
    }

    public List<FoodModel> getFoods() {return foods;}

    public void setFoods(List<FoodModel> foods) {
        this.foods = foods;
    }
}
