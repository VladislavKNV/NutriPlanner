package com.example.nutriPlanner.DataBase;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nutriPlanner.Model.BMIModel;

import java.util.LinkedHashMap;
import java.util.Map;


public class DbBMI {
    private static final String BMI_TABLE = "BMI";

    public static long add(SQLiteDatabase db, BMIModel bmiModel) {
        ContentValues values = new ContentValues();

        values.put("IDBMI", bmiModel.getId());
        values.put("USERID", bmiModel.getUserId());
        values.put("HEIGHT", bmiModel.getHeight());
        values.put("CURRENT_WEIGHT", bmiModel.getCurrentWeight());
        values.put("MEASUREMENT_DATE", bmiModel.getMeasurementDate());

        return db.insert(BMI_TABLE, null, values);
    }

    public static BMIModel getLastBMI(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + BMI_TABLE + " ORDER BY MEASUREMENT_DATE DESC LIMIT 1", null);
        BMIModel lastBMI = null;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex("IDBMI");
                int userIdIndex = cursor.getColumnIndex("USERID");
                int heightIndex = cursor.getColumnIndex("HEIGHT");
                int currentWeightIndex = cursor.getColumnIndex("CURRENT_WEIGHT");
                int measurementDateIndex = cursor.getColumnIndex("MEASUREMENT_DATE");

                int id = cursor.getInt(idIndex);
                int userId = cursor.getInt(userIdIndex);
                int height = cursor.getInt(heightIndex);
                double currentWeight = cursor.getDouble(currentWeightIndex);
                String measurementDate = cursor.getString(measurementDateIndex);

                lastBMI = new BMIModel(id, userId, height, currentWeight, measurementDate);
            }
            cursor.close();
        }

        return lastBMI;
    }


    public static long addOrUpdate(SQLiteDatabase db, BMIModel bmiModel) {
        ContentValues values = new ContentValues();
        values.put("IDBMI", bmiModel.getId());
        values.put("USERID", bmiModel.getUserId());
        values.put("HEIGHT", bmiModel.getHeight());
        values.put("CURRENT_WEIGHT", bmiModel.getCurrentWeight());
        values.put("MEASUREMENT_DATE", bmiModel.getMeasurementDate());

        String whereClause = "IDBMI = ?";
        String[] whereArgs = { String.valueOf(bmiModel.getId()) };

        int rowsUpdated = db.update(BMI_TABLE, values, whereClause, whereArgs);

        if (rowsUpdated == 0) {
            return db.insert(BMI_TABLE, null, values);
        } else {
            return rowsUpdated;
        }
    }

    public static Map<String, Double> getAllBmiRecords(SQLiteDatabase db) {
        Map<String, Double> bmiMap = new LinkedHashMap<>();

        Cursor cursor = db.rawQuery("SELECT MEASUREMENT_DATE, CURRENT_WEIGHT FROM " + BMI_TABLE, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int measurementDateIndex = cursor.getColumnIndex("MEASUREMENT_DATE");
                int currentWeightIndex = cursor.getColumnIndex("CURRENT_WEIGHT");

                do {
                    String measurementDate = cursor.getString(measurementDateIndex);
                    double currentWeight = cursor.getDouble(currentWeightIndex);
                    bmiMap.put(measurementDate, currentWeight);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return bmiMap;
    }
}
