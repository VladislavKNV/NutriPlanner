package com.example.nutriPlanner.DataBase;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nutriPlanner.Model.UserModel;

public class DbUser {
    private static final String USERS_TABLE = "USERS";

    public static long addAll(SQLiteDatabase db, UserModel userModel) {
        ContentValues values = new ContentValues();

        values.put("IDUSER", userModel.getId());
        values.put("ROLEID", userModel.getRoleId());
        values.put("NAME", userModel.getName());
        values.put("EMAIL", userModel.getEmail());
        values.put("PASSWORD", userModel.getPassword());
        values.put("GENDER", userModel.getGender());
        values.put("BIRTHDAY", userModel.getBirthDate());
        values.put("GOAL", userModel.getGoal());
        values.put("ACTIVITY_LEVEL", userModel.getActivityLevel());
        values.put("DESIRED_WEIGHT", userModel.getDesiredWeight());
        values.put("WEIGHT_FACTOR", userModel.getWeightFactor());
        values.put("REGISTRATION_DATE", userModel.getRegistrationDate());
        values.put("LAST_LOGIN_DATE", userModel.getLastLoginDate());

        return db.insert(USERS_TABLE, null, values);
    }

    public static Cursor getAll(SQLiteDatabase db) {
        return db.rawQuery("select * from " + USERS_TABLE + ";", null);
    }

    public static int getUserId(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT IDUSER FROM " + USERS_TABLE, null);
        int userId = -1;

        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getInt(0);
            cursor.close();
        }

        return userId;
    }


    public static int getUsersCount(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + USERS_TABLE, null);
        int count = 0;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }

        return count;
    }

    public static int updateUserInfo(SQLiteDatabase db, UserModel userModel) {
        ContentValues values = new ContentValues();
        values.put("NAME", userModel.getName());
        values.put("GOAL", userModel.getGoal());
        values.put("DESIRED_WEIGHT", userModel.getDesiredWeight());
        values.put("ACTIVITY_LEVEL", userModel.getActivityLevel());
        values.put("WEIGHT_FACTOR", userModel.getWeightFactor());

        String whereClause = "IDUSER = ?";
        String[] whereArgs = {String.valueOf(userModel.getId())};

        return db.update(USERS_TABLE, values, whereClause, whereArgs);
    }

    public static boolean updatePassword(SQLiteDatabase db, int userId, String newPassword) {
        ContentValues values = new ContentValues();
        values.put("PASSWORD", newPassword);

        String selection = "IDUSER = ?";
        String[] selectionArgs = { String.valueOf(userId) };

        int count = db.update(
                USERS_TABLE, values, selection, selectionArgs);
        return count > 0;
    }

    public static long updateLastLoginDate(SQLiteDatabase db, int id, String lastLoginDate) {
        ContentValues values = new ContentValues();

        values.put("LAST_LOGIN_DATE", lastLoginDate);

        return db.update(USERS_TABLE, values, "IDUSER = ?", new String[] { String.valueOf(id) });
    }

    public static long deleteUser(SQLiteDatabase db, int id) {
        return db.delete(USERS_TABLE, "IDUSER = ?", new String[] {String.valueOf(id)});
    }
}
