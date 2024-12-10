package com.example.nutriPlanner.DataBase;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nutriPlanner.Model.FoodModel;

public class DbFoods {
    private static final String FOODS_TABLE = "FOODS";

    public static long add(SQLiteDatabase db, FoodModel foodModel) {
        ContentValues values = new ContentValues();

        values.put("IDFOOD", foodModel.getId());
        values.put("FOOD_NAME", foodModel.getFoodName());
        values.put("DESCRIPTION", foodModel.getDescription());
        values.put("CATEGORY", foodModel.getCategory());
        values.put("FOOD_RATING", foodModel.getFoodRating());
        values.put("INGREDIENTS", foodModel.getIngredients());
        values.put("RECIPE", foodModel.getRecipe());
        values.put("CALORIES_PER_100G", foodModel.getCaloriesPer100g());
        values.put("CARBOHYDRATES", foodModel.getCarbohydrates());
        values.put("FATS", foodModel.getFats());
        values.put("PROTEINS", foodModel.getProteins());
        values.put("SERVING_SIZE", foodModel.getServingSize());

        return db.insert(FOODS_TABLE, null, values);
    }

    public static Cursor getAll(SQLiteDatabase db) {
        return db.rawQuery("select * from " + FOODS_TABLE + ";", null);
    }

    public static FoodModel getById(SQLiteDatabase db, long id) {
        FoodModel foodModel = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + FOODS_TABLE + " WHERE IDFOOD = ?;", new String[]{String.valueOf(id)});
        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex("IDFOOD");
            if (idIndex != -1) {
                foodModel = new FoodModel();
                foodModel.setId(cursor.getInt(idIndex));
                int foodNameIndex = cursor.getColumnIndex("FOOD_NAME");
                if (foodNameIndex != -1) {
                    foodModel.setFoodName(cursor.getString(foodNameIndex));
                }
                int descriptionIndex = cursor.getColumnIndex("DESCRIPTION");
                if (descriptionIndex != -1) {
                    foodModel.setDescription(cursor.getString(descriptionIndex));
                }
                int categoryIndex = cursor.getColumnIndex("CATEGORY");
                if (categoryIndex != -1) {
                    foodModel.setCategory(cursor.getString(categoryIndex));
                }
                int foodRatingIndex = cursor.getColumnIndex("FOOD_RATING");
                if (foodRatingIndex != -1) {
                    foodModel.setFoodRating(cursor.getInt(foodRatingIndex));
                }
                int ingredientsIndex = cursor.getColumnIndex("INGREDIENTS");
                if (ingredientsIndex != -1) {
                    foodModel.setIngredients(cursor.getString(ingredientsIndex));
                }
                int recipeIndex = cursor.getColumnIndex("RECIPE");
                if (recipeIndex != -1) {
                    foodModel.setRecipe(cursor.getString(recipeIndex));
                }
                int caloriesIndex = cursor.getColumnIndex("CALORIES_PER_100G");
                if (caloriesIndex != -1) {
                    foodModel.setCaloriesPer100g(cursor.getInt(caloriesIndex));
                }
                int carbohydratesIndex = cursor.getColumnIndex("CARBOHYDRATES");
                if (carbohydratesIndex != -1) {
                    foodModel.setCarbohydrates(cursor.getDouble(carbohydratesIndex));
                }
                int fatsIndex = cursor.getColumnIndex("FATS");
                if (fatsIndex != -1) {
                    foodModel.setFats(cursor.getDouble(fatsIndex));
                }
                int proteinsIndex = cursor.getColumnIndex("PROTEINS");
                if (proteinsIndex != -1) {
                    foodModel.setProteins(cursor.getDouble(proteinsIndex));
                }
                int servingSizeIndex = cursor.getColumnIndex("SERVING_SIZE");
                if (servingSizeIndex != -1) {
                    foodModel.setServingSize(cursor.getString(servingSizeIndex));
                }
            }
            cursor.close();
        }
        return foodModel;
    }

}