package com.example.nutriPlanner.Model;

public class FoodModel {

    private int id;
    private String foodName;
    private String description;
    private String category;
    private int foodRating;
    private String ingredients;
    private String recipe;
    private int caloriesPer100g;
    private double carbohydrates;
    private double fats;
    private double proteins;
    private String servingSize;


    public FoodModel() {
        this.id = id;
        this.foodName = foodName;
        this.description = description;
        this.category = category;
        this.foodRating = foodRating;
        this.ingredients = ingredients;
        this.recipe = recipe;
        this.caloriesPer100g = caloriesPer100g;
        this.carbohydrates = carbohydrates;
        this.fats = fats;
        this.proteins = proteins;
        this.servingSize = servingSize;
    }

    public FoodModel(int id, String foodName, String category, int caloriesPer100g, String servingSize) {
        this.id = id;
        this.foodName = foodName;
        this.category = category;
        this.caloriesPer100g = caloriesPer100g;
        this.servingSize = servingSize;
    }

    public FoodModel(String foodName, String category, int caloriesPer100g, double carbohydrates,
                     double fats, double proteins, String servingSize, int foodRating) {
        this.foodName = foodName;
        this.category = category;
        this.caloriesPer100g = caloriesPer100g;
        this.carbohydrates = carbohydrates;
        this.fats = fats;
        this.proteins = proteins;
        this.servingSize = servingSize;
        this.foodRating = foodRating;
    }

    public FoodModel(String foodName, String description, String category, String ingredients, String recipe, int caloriesPer100g,
                     double carbohydrates, double fats, double proteins, String servingSize, int foodRating) {
        this.foodName = foodName;
        this.description = description;
        this.category = category;
        this.ingredients = ingredients;
        this.recipe = recipe;
        this.caloriesPer100g = caloriesPer100g;
        this.carbohydrates = carbohydrates;
        this.fats = fats;
        this.proteins = proteins;
        this.servingSize = servingSize;
        this.foodRating = foodRating;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getFoodName() {
        return foodName;
    }
    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public int getFoodRating() {
        return foodRating;
    }
    public void setFoodRating(int foodRating) {
        this.foodRating = foodRating;
    }

    public String getIngredients() {
        return ingredients;
    }
    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getRecipe() {
        return recipe;
    }
    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }

    public int getCaloriesPer100g() {
        return caloriesPer100g;
    }
    public void setCaloriesPer100g(int caloriesPer100g) {
        this.caloriesPer100g = caloriesPer100g;
    }

    public double getCarbohydrates() {
        return carbohydrates;
    }
    public void setCarbohydrates(double carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public double getFats() {
        return fats;
    }
    public void setFats(double fats) {
        this.fats = fats;
    }

    public double getProteins() {
        return proteins;
    }
    public void setProteins(double proteins) {
        this.proteins = proteins;
    }

    public String getServingSize() {
        return servingSize;
    }
    public void setServingSize(String servingSize) {
        this.servingSize = servingSize;
    }

}
