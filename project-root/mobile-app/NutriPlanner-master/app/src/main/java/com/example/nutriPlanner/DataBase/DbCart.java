package com.example.nutriPlanner.DataBase;

import android.database.Cursor;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.nutriPlanner.Model.CartModel;

public class DbCart { //не реализовано

    private static final String CART_TABLE = "CART";

    public static long add(SQLiteDatabase db, CartModel cartModel) {
        ContentValues values = new ContentValues();

        values.put("DATE", cartModel.getDate());
        values.put("INGREDIENTS", cartModel.getIngredients());
        values.put("IS_COMPLETE", cartModel.isComplete() ? 1 : 0);

        return db.insert(CART_TABLE, null, values);
    }

    public static int update(SQLiteDatabase db, CartModel cartModel) {
        ContentValues values = new ContentValues();
        values.put("DATE", cartModel.getDate());
        values.put("INGREDIENTS", cartModel.getIngredients());
        values.put("IS_COMPLETE", cartModel.isComplete() ? 1 : 0);

        String whereClause = "IDCART = ?";
        String[] whereArgs = { String.valueOf(cartModel.getId()) };

        return db.update(CART_TABLE, values, whereClause, whereArgs);
    }

    public static CartModel getCartById(SQLiteDatabase db, int id) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + CART_TABLE + " WHERE IDCART = ?", new String[]{String.valueOf(id)});
        CartModel cartModel = null;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex("IDCART");
                int dateIndex = cursor.getColumnIndex("DATE");
                int ingredientsIndex = cursor.getColumnIndex("INGREDIENTS");
                int isCompleteIndex = cursor.getColumnIndex("IS_COMPLETE");

                int cartId = cursor.getInt(idIndex);
                String date = cursor.getString(dateIndex);
                String ingredients = cursor.getString(ingredientsIndex);
                boolean isComplete = cursor.getInt(isCompleteIndex) == 1;

                cartModel = new CartModel(cartId, date, ingredients, isComplete);
            }
            cursor.close();
        }

        return cartModel;
    }
}


