package com.example.nutriPlanner.DataBase;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nutriPlanner.Model.FavoriteFoodModel;

public class DbFavoriteFood {
    private static final String FAVORITE_FOOD_TABLE = "FAVORITE_FOOD";

    public static long add(SQLiteDatabase db, FavoriteFoodModel favoriteFoodModel) {
        ContentValues values = new ContentValues();
        values.put("IDFAVORITEFOOD", favoriteFoodModel.getId());
        values.put("IDUSER", favoriteFoodModel.getUserId());
        values.put("IDFOOD", favoriteFoodModel.getFoodId());

        return db.insert(FAVORITE_FOOD_TABLE, null, values);
    }

    public static Cursor getAllFavoriteFoods(SQLiteDatabase db) {
        return db.rawQuery("SELECT * FROM " + FAVORITE_FOOD_TABLE, null);
    }

    public static boolean isFavoriteFood(SQLiteDatabase db, int foodId) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + FAVORITE_FOOD_TABLE + " WHERE IDFOOD = ?", new String[]{String.valueOf(foodId)});
        boolean isFavorite = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        return isFavorite;
    }

    public static Integer getFavoriteFoodIdByFoodId(SQLiteDatabase db, int foodId) {
        Cursor cursor = db.rawQuery("SELECT IDFAVORITEFOOD FROM " + FAVORITE_FOOD_TABLE + " WHERE IDFOOD = ?", new String[]{String.valueOf(foodId)});
        Integer favoriteFoodId = null;
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex("IDFAVORITEFOOD");
                    if (columnIndex != -1) {
                        favoriteFoodId = cursor.getInt(columnIndex);
                    }
                }
            } finally {
                cursor.close();
            }
        }
        return favoriteFoodId;
    }

    public static long deleteFavoriteFood(SQLiteDatabase db, int id) {
        return db.delete(FAVORITE_FOOD_TABLE, "IDFAVORITEFOOD = ?", new String[] {String.valueOf(id)});
    }
}
