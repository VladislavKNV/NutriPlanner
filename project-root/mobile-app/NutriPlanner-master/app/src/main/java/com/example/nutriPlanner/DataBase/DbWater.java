package com.example.nutriPlanner.DataBase;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nutriPlanner.Model.WaterModel;

public class DbWater {
    private static final String WATER_TABLE = "WATER";

    public static long add(SQLiteDatabase db, WaterModel waterModel) {
        ContentValues values = new ContentValues();

        values.put("DATE", waterModel.getDate());
        values.put("COUNT", waterModel.getCount());

        return db.insert(WATER_TABLE, null, values);
    }

    public static int update(SQLiteDatabase db, WaterModel waterModel) {
        ContentValues values = new ContentValues();
        values.put("COUNT", waterModel.getCount());

        String whereClause = "DATE = ?";
        String[] whereArgs = { waterModel.getDate() };

        return db.update(WATER_TABLE, values, whereClause, whereArgs);
    }

    public static WaterModel getWaterDataByDate(SQLiteDatabase db, String date) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + WATER_TABLE + " WHERE DATE = ?", new String[]{date});
        WaterModel waterData = null;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex("IDWATER");
                int dateIdIndex = cursor.getColumnIndex("DATE");
                int countIndex = cursor.getColumnIndex("COUNT");

                int id = cursor.getInt(idIndex);
                String dateStr = cursor.getString(dateIdIndex);
                float count = cursor.getFloat(countIndex);

                waterData = new WaterModel(id, dateStr, count);
            }
            cursor.close();
        }

        return waterData;
    }
}