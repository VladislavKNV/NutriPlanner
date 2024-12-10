package com.example.nutriPlanner.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    private static final int SCHEMA = 1;
    public static final String DATABASE_NAME = "NutriPlanner";
    private static final String ROLES_TABLE = "ROLES";
    private static final String USERS_TABLE = "USERS";
    private static final String BMI_TABLE = "BMI";
    private static final String FOODS_TABLE = "FOODS";
    private static final String FAVORITE_FOOD_TABLE = "FAVORITE_FOOD";
    private static final String FOOD_INTAKE_TABLE = "FOOD_INTAKE";
    private static final String WATER_TABLE = "WATER";
    private static final String CART_TABLE = "CART";


    private static DbHelper instance = null;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    public static DbHelper getInstance(Context context) {
        if(instance == null) instance = new DbHelper(context);
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + ROLES_TABLE + " (                         "
                + "IDROLE integer primary key not null,                        "
                + "ROLENAME text                                             );"
        );
        db.execSQL("create table " + USERS_TABLE + " (                         "
                + "IDUSER integer primary key not null,                        "
                + "ROLEID integer,                                             "
                + "NAME text,                                                  "
                + "EMAIL text,                                                 "
                + "PASSWORD text,                                              "
                + "GENDER text,                                                "
                + "BIRTHDAY text,                                              "  //format: YYYY-MM-DD
                + "GOAL text,                                                  "
                + "DESIRED_WEIGHT text,                                        "
                + "ACTIVITY_LEVEL text,                                        "
                + "WEIGHT_FACTOR double,                                       "
                + "REGISTRATION_DATE text,                                     "  //format: YYYY-MM-DDTHH:MM:SS
                + "LAST_LOGIN_DATE text,                                       "  //format: YYYY-MM-DDTHH:MM:SS
                + "foreign key(ROLEID) references " + ROLES_TABLE + "(IDROLE)  "
                + " on delete cascade                                        );"
        );
        db.execSQL("create table " + BMI_TABLE + " (                           "
                + "IDBMI integer primary key not null,                         "
                + "USERID integer,                                             "
                + "HEIGHT integer,                                             "
                + "CURRENT_WEIGHT double,                                      "
                + "MEASUREMENT_DATE text,                                      "  //format: YYYY-MM-DD
                + "foreign key(USERID) references " + USERS_TABLE + "(IDUSER)  "
                + " on delete cascade                                        );"
        );
        db.execSQL("create table " + FOODS_TABLE + " (                         "
                + "IDFOOD integer primary key not null,                        "
                + "FOOD_NAME text,                                             "
                + "DESCRIPTION text,                                           "
                + "CATEGORY text,                                              "
                + "FOOD_RATING integer,                                        "
                + "INGREDIENTS text,                                           "
                + "RECIPE text,                                                "
                + "CALORIES_PER_100G integer,                                  "
                + "CARBOHYDRATES double,                                       "
                + "FATS double,                                                "
                + "PROTEINS double,                                            "
                + "SERVING_SIZE text                                         );"
        );
        db.execSQL("create table " + FAVORITE_FOOD_TABLE + " (                 "
                + "IDFAVORITEFOOD integer primary key not null,                "
                + "IDUSER integer,                                             "
                + "IDFOOD integer,                                             "
                + "foreign key(IDUSER) references " + USERS_TABLE + "(IDUSER)  "
                + " on delete cascade,                                         "
                + "foreign key(IDFOOD) references " + FOODS_TABLE + "(IDFOOD)  "
                + " on delete cascade                                        );"
        );
        db.execSQL("create table " + FOOD_INTAKE_TABLE + " (                   "
                + "IDFOODINTAKE integer primary key not null,                  "
                + "IDUSER integer,                                             "
                + "IDFOOD integer,                                             "
                + "MEAL_TYPE text,                                             "
                + "DATE text,                                                  "  //format: YYYY-MM-DD
                + "foreign key(IDUSER) references " + USERS_TABLE + "(IDUSER)  "
                + " on delete cascade,                                         "
                + "foreign key(IDFOOD) references " + FOODS_TABLE + "(IDFOOD)  "
                + " on delete cascade                                        );"
        );
        db.execSQL("create table " + WATER_TABLE + " (                         "
                + "IDWATER integer primary key not null,                       "
                + "DATE text,                                                  "
                + "COUNT float                                               );"
        );
        db.execSQL("create table " + CART_TABLE + " ("
                + "IDCART integer primary key not null, "
                + "DATE text, "
                + "INGREDIENTS text, "
                + "IS_COMPLETE integer default 0"
                + ");"
        );

        // добавление данных в таблицу ROLES_TABLE
        ContentValues adminValues = new ContentValues();
        adminValues.put("IDROLE", 1);
        adminValues.put("ROLENAME", "Administrator");
        db.insert(ROLES_TABLE, null, adminValues);

        ContentValues userValues = new ContentValues();
        userValues.put("IDROLE", 2);
        userValues.put("ROLENAME", "User");
        db.insert(ROLES_TABLE, null, userValues);

    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists " + USERS_TABLE);
        db.execSQL("drop table if exists " + ROLES_TABLE);
        db.execSQL("drop table if exists " + BMI_TABLE);
        db.execSQL("drop table if exists " + FOODS_TABLE);
        db.execSQL("drop table if exists " + FAVORITE_FOOD_TABLE);
        db.execSQL("drop table if exists " + FOOD_INTAKE_TABLE);
        onCreate(db);
    }
}
