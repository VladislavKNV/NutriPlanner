package com.example.nutriPlanner.DataBase;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nutriPlanner.Model.FoodIntakeModel;

public class DbFoodIntake {

    private static final String FOOD_INTAKE_TABLE = "FOOD_INTAKE";
    public static long add(SQLiteDatabase db, FoodIntakeModel foodIntakeModel) {
        ContentValues values = new ContentValues();

        values.put("IDFOODINTAKE", foodIntakeModel.getId());
        values.put("IDUSER", foodIntakeModel.getUserId());
        values.put("IDFOOD", foodIntakeModel.getFoodId());
        values.put("MEAL_TYPE", foodIntakeModel.getMealType());
        values.put("DATE", foodIntakeModel.getDate());

        return db.insert(FOOD_INTAKE_TABLE, null, values);
    }

    public static int getTotalCaloriesForDay(SQLiteDatabase db, String date) {
        double totalCalories = 0;

        String query = "SELECT SUM(FOODS.CALORIES_PER_100G) " +
                "FROM FOOD_INTAKE " +
                "JOIN FOODS ON FOOD_INTAKE.IDFOOD = FOODS.IDFOOD " +
                "WHERE DATE = ?";

        Cursor cursor = db.rawQuery(query, new String[]{date});

        if (cursor != null && cursor.moveToFirst()) {
            totalCalories = cursor.getDouble(0);
            cursor.close();
        }

        return (int) totalCalories;
    }

    public static double getTotalProteinsForDay(SQLiteDatabase db, String date) {
        double totalProteins = 0;

        String query = "SELECT SUM(FOODS.PROTEINS) " +
                "FROM FOOD_INTAKE " +
                "JOIN FOODS ON FOOD_INTAKE.IDFOOD = FOODS.IDFOOD " +
                "WHERE DATE = ?";

        Cursor cursor = db.rawQuery(query, new String[]{date});

        if (cursor != null && cursor.moveToFirst()) {
            totalProteins = cursor.getDouble(0);
            cursor.close();
        }

        return Math.round(totalProteins * 10.0) / 10.0;
    }

    public static double getTotalCarbohydratesForDay(SQLiteDatabase db, String date) {
        double totalCarbohydrates = 0;

        String query = "SELECT SUM(FOODS.CARBOHYDRATES) " +
                "FROM FOOD_INTAKE " +
                "JOIN FOODS ON FOOD_INTAKE.IDFOOD = FOODS.IDFOOD " +
                "WHERE DATE = ?";

        Cursor cursor = db.rawQuery(query, new String[]{date});

        if (cursor != null && cursor.moveToFirst()) {
            totalCarbohydrates = cursor.getDouble(0);
            cursor.close();
        }

        return Math.round(totalCarbohydrates * 10.0) / 10.0;
    }

    public static double getTotalFatsForDay(SQLiteDatabase db, String date) {
        double totalFats = 0;

        String query = "SELECT SUM(FOODS.FATS) " +
                "FROM FOOD_INTAKE " +
                "JOIN FOODS ON FOOD_INTAKE.IDFOOD = FOODS.IDFOOD " +
                "WHERE DATE = ?";

        Cursor cursor = db.rawQuery(query, new String[]{date});

        if (cursor != null && cursor.moveToFirst()) {
            totalFats = cursor.getDouble(0);
            cursor.close();
        }

        return Math.round(totalFats * 10.0) / 10.0;
    }


    public static Cursor getFoodIntakesByMealTypeAndDate(SQLiteDatabase db, String mealType, String date) {
        String query = "SELECT * FROM " + FOOD_INTAKE_TABLE + " WHERE MEAL_TYPE = ? AND DATE = ?";
        return db.rawQuery(query, new String[]{mealType, date});
    }

    public static int[] getCaloriesByMealTypeForDay(SQLiteDatabase db, String date) {
        int[] caloriesByMealType = new int[4]; // 0: Breakfast, 1: Lunch, 2: Dinner, 3: Snack

        String query = "SELECT MEAL_TYPE, SUM(FOODS.CALORIES_PER_100G) as TOTAL_CALORIES " +
                "FROM FOOD_INTAKE " +
                "JOIN FOODS ON FOOD_INTAKE.IDFOOD = FOODS.IDFOOD " +
                "WHERE DATE = ? " +
                "GROUP BY MEAL_TYPE";

        Cursor cursor = db.rawQuery(query, new String[]{date});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String mealType = null;
                int totalCalories = 0;
                int mealTypeIndex = cursor.getColumnIndex("MEAL_TYPE");
                if (mealTypeIndex != -1) {
                    mealType = cursor.getString(mealTypeIndex);
                }
                int totalCaloriesIndex = cursor.getColumnIndex("TOTAL_CALORIES");
                if (totalCaloriesIndex != -1) {
                    totalCalories = cursor.getInt(totalCaloriesIndex);
                }

                switch (mealType) {
                    case "ЗАВТРАК":
                        caloriesByMealType[0] = totalCalories;
                        break;
                    case "ОБЕД":
                        caloriesByMealType[1] = totalCalories;
                        break;
                    case "УЖИН":
                        caloriesByMealType[2] = totalCalories;
                        break;
                    case "ПЕРЕКУСЫ":
                        caloriesByMealType[3] = totalCalories;
                        break;
                }
            }
            cursor.close();
        }

        return caloriesByMealType;
    }

    public static int getLastFoodIntakeId(SQLiteDatabase db, String mealType, int foodId, String date) {
        String query = "SELECT IDFOODINTAKE FROM " + FOOD_INTAKE_TABLE +
                " WHERE MEAL_TYPE = ? AND IDFOOD = ? AND DATE = ?" +
                " ORDER BY IDFOODINTAKE DESC LIMIT 1";
        Cursor cursor = db.rawQuery(query, new String[]{mealType, String.valueOf(foodId), date});

        int lastId = -1; // Default value if no record is found

        if (cursor != null && cursor.moveToFirst()) {
            lastId = cursor.getInt(0);
            cursor.close();
        }

        return lastId;
    }

    public static long deleteFoodIntakeById(SQLiteDatabase db, int id) {
        return db.delete(FOOD_INTAKE_TABLE, "IDFOODINTAKE = ?", new String[] {String.valueOf(id)});
    }
}
