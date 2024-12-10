package com.example.nutriPlanner.View;

import static com.example.nutriPlanner.Helpers.CalorieCalculator.calculateNutrients;
import static com.example.nutriPlanner.Helpers.CalorieCalculator.distributeCalories;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nutriPlanner.DataBase.DbBMI;
import com.example.nutriPlanner.DataBase.DbFoodIntake;
import com.example.nutriPlanner.DataBase.DbHelper;
import com.example.nutriPlanner.DataBase.DbUser;
import com.example.nutriPlanner.DataBase.DbWater;
import com.example.nutriPlanner.Helpers.CalorieCalculator;
import com.example.nutriPlanner.Model.BMIModel;
import com.example.nutriPlanner.Model.UserModel;
import com.example.nutriPlanner.Model.WaterModel;
import com.example.nutriPlanner.R;
import com.example.nutriPlanner.ProgressActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private ImageButton[] imageButtons;
    private LinearLayout BreakfastLayout, LunchLayout, DinnerLayout, SnacksLayout;
    private UserModel userModel;
    private BMIModel bmiModel;
    private WaterModel waterModel;
    private int caloriesPerDay;
    int[] nutrients, caloriesDistribution;
    private BottomNavigationView bottomNavigationView;
    private ProgressBar proteinsProgressBar, carbsProgressBar, fatsProgressBar;
    private TextView waterLabel, tvPreviousDay, tvToday, tvNextDay;
    private String formattedDate1, formattedDate2, StringToday;
    private SimpleDateFormat dateFormat1;
    private SimpleDateFormat dateFormat2;
    private Calendar calendar;
    private Calendar today;
    private ConstraintLayout Water;
    private TextView caloriesPerDayText, proteinsProgressText, carbsProgressText, fatsProgressText,
            tvBreakfastRecommendation, tvLunchRecommendation, tvDinnerRecommendation, tvSnacksRecommendation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DbHelper(getApplicationContext()).getWritableDatabase();

        if (!getUserData()){
            Intent intent = new Intent(this, Authorization.class);
            startActivity(intent);
        }
        calculateData();
        bind();

        loadWaterData();
        updateWaterDisplay();

        for (int i = 0; i < imageButtons.length; i++) {
            final int index = i;
            imageButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleImageButtonClick(index);
                }
            });
        }


        BreakfastLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MealActivity.class);
                intent.putExtra("date", formattedDate1);
                intent.putExtra("mealType", "ЗАВТРАК");
                startActivity(intent);
            }
        });
        LunchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MealActivity.class);
                intent.putExtra("date", formattedDate1);
                intent.putExtra("mealType", "ОБЕД");
                startActivity(intent);
            }
        });
        DinnerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MealActivity.class);
                intent.putExtra("date", formattedDate1);
                intent.putExtra("mealType", "УЖИН");
                startActivity(intent);
            }
        });
        SnacksLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MealActivity.class);
                intent.putExtra("date", formattedDate1);
                intent.putExtra("mealType", "ПЕРЕКУСЫ");
                startActivity(intent);
            }
        });

        tvNextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveForward();
            }
        });

        tvPreviousDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveBackward();
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_home) {
                return true;
            } else if (item.getItemId() == R.id.menu_progress) {
                Intent intent = new Intent(this, ProgressActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            } else if (item.getItemId() == R.id.menu_cart) {
                Intent intent = new Intent(this, CartActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            } else if (item.getItemId() == R.id.menu_profile) {
                Intent intent = new Intent(this, AccountActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            } else {
                return false;
            }
        });
    }

    private void bind(){
        tvPreviousDay = findViewById(R.id.tvPreviousDay);
        tvToday = findViewById(R.id.tvToday);
        tvNextDay = findViewById(R.id.tvNextDay);
        calendar = Calendar.getInstance();
        today = (Calendar) calendar.clone();
        dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat2 = new SimpleDateFormat("d MMMM yyyy", new Locale("ru"));
        StringToday = dateFormat1.format(calendar.getTime());
        updateDateDisplay();

        BreakfastLayout = findViewById(R.id.BreakfastLayout);
        LunchLayout = findViewById(R.id.LunchLayout);
        DinnerLayout = findViewById(R.id.DinnerLayout);
        SnacksLayout = findViewById(R.id.SnacksLayout);

        proteinsProgressBar = findViewById(R.id.proteinsProgressBar);
        carbsProgressBar = findViewById(R.id.carbsProgressBar);
        fatsProgressBar = findViewById(R.id.fatsProgressBar);

        caloriesPerDayText = findViewById(R.id.caloriesPerDayText);
        proteinsProgressText = findViewById(R.id.proteinsProgressText);
        carbsProgressText = findViewById(R.id.carbsProgressText);
        fatsProgressText = findViewById(R.id.fatsProgressText);

        tvBreakfastRecommendation = findViewById(R.id.tvBreakfastRecommendation);
        tvLunchRecommendation = findViewById(R.id.tvLunchRecommendation);
        tvDinnerRecommendation = findViewById(R.id.tvDinnerRecommendation);
        tvSnacksRecommendation = findViewById(R.id.tvSnacksRecommendation);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_home);
        Water = findViewById(R.id.Water);
        waterLabel = findViewById(R.id.waterLabel);
        imageButtons = new ImageButton[]{
                findViewById(R.id.imageButton1),
                findViewById(R.id.imageButton2),
                findViewById(R.id.imageButton3),
                findViewById(R.id.imageButton4),
                findViewById(R.id.imageButton5),
                findViewById(R.id.imageButton6),
                findViewById(R.id.imageButton7),
                findViewById(R.id.imageButton8)
        };
        setData();
    }

    private void setData() {
        if (!StringToday.equals(formattedDate1)) {
            Water.setVisibility(View.GONE);
        } else  Water.setVisibility(View.VISIBLE);

        int totalCalories = DbFoodIntake.getTotalCaloriesForDay(db, formattedDate1);
        double totalProteins = DbFoodIntake.getTotalProteinsForDay(db, formattedDate1);
        double totalCarbohydrates = DbFoodIntake.getTotalCarbohydratesForDay(db, formattedDate1);
        double totalFats = DbFoodIntake.getTotalFatsForDay(db, formattedDate1);

        proteinsProgressBar.setProgress((int) ((totalProteins / nutrients[0]) * 100));
        carbsProgressBar.setProgress((int) ((totalCarbohydrates / nutrients[1]) * 100));
        fatsProgressBar.setProgress((int) ((totalFats / nutrients[2]) * 100));

        if ((caloriesPerDay - totalCalories) >= 0){
            caloriesPerDayText.setText("" + (caloriesPerDay - totalCalories));
        } else   caloriesPerDayText.setText("0");

        proteinsProgressText.setText(totalProteins + " / " + nutrients[0] + "г");
        carbsProgressText.setText(totalCarbohydrates + " / " + nutrients[1] + "г");
        fatsProgressText.setText(totalFats + " / " + nutrients[2] + "г");

        int[] caloriesByMealType = DbFoodIntake.getCaloriesByMealTypeForDay(db, formattedDate1);
        int breakfastMin = Math.max(0, (caloriesDistribution[0] - caloriesByMealType[0] - 48));
        int breakfastMax = Math.max(0, (caloriesDistribution[0] - caloriesByMealType[0] + 53));
        if (breakfastMin == breakfastMax) {
            tvBreakfastRecommendation.setText("Рекомендуется " + breakfastMin + " ккал");
        } else {
            tvBreakfastRecommendation.setText("Рекомендуется " + breakfastMin + "-" + breakfastMax + " ккал");
        }

        int lunchMin = Math.max(0, (caloriesDistribution[1] - caloriesByMealType[1] - 53));
        int lunchMax = Math.max(0, (caloriesDistribution[1] - caloriesByMealType[1] + 50));
        if (lunchMin == lunchMax) {
            tvLunchRecommendation.setText("Рекомендуется " + lunchMin + " ккал");
        } else {
            tvLunchRecommendation.setText("Рекомендуется " + lunchMin + "-" + lunchMax + " ккал");
        }

        int dinnerMin = Math.max(0, (caloriesDistribution[2] - caloriesByMealType[2] - 52));
        int dinnerMax = Math.max(0, (caloriesDistribution[2] - caloriesByMealType[2] + 52));
        if (dinnerMin == dinnerMax) {
            tvDinnerRecommendation.setText("Рекомендуется " + dinnerMin + " ккал");
        } else {
            tvDinnerRecommendation.setText("Рекомендуется " + dinnerMin + "-" + dinnerMax + " ккал");
        }

        int snacksMin = Math.max(0, (caloriesDistribution[3] - caloriesByMealType[3] - 54));
        int snacksMax = Math.max(0, (caloriesDistribution[3] - caloriesByMealType[3] + 44));
        if (snacksMin == snacksMax) {
            tvSnacksRecommendation.setText("Рекомендуется " + snacksMin + " ккал");
        } else {
            tvSnacksRecommendation.setText("Рекомендуется " + snacksMin + "-" + snacksMax + " ккал");
        }

    }

    private void calculateData() {
        caloriesPerDay = CalorieCalculator.calculateCalories(22, userModel.getGender(), bmiModel.getHeight() , bmiModel.getCurrentWeight(),
                userModel.getGoal(), userModel.getActivityLevel(), userModel.getWeightFactor());
        nutrients = calculateNutrients(caloriesPerDay, userModel.getGoal(), userModel.getActivityLevel());
        caloriesDistribution = distributeCalories(caloriesPerDay);
    }

    private void moveForward() {
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        updateDateDisplay();
        setData();
    }

    private void moveBackward() {
        if (calendar.after(today)) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            updateDateDisplay();
            setData();
        }
    }

    private boolean getUserData(){
        Cursor cursorUser = DbUser.getAll(db);
        cursorUser.moveToFirst();
        if (cursorUser.moveToFirst()) {
            if (DbUser.getUsersCount(db) > 0) {
                userModel = new UserModel(cursorUser.getInt(0),
                        cursorUser.getInt(1),
                        cursorUser.getString(2),
                        cursorUser.getString(3),
                        cursorUser.getString(4),
                        cursorUser.getString(5),
                        cursorUser.getString(6),
                        cursorUser.getString(7),
                        cursorUser.getDouble(8),
                        cursorUser.getString(9),
                        cursorUser.getDouble(10),
                        cursorUser.getString(11),
                        cursorUser.getString(12));

                bmiModel = DbBMI.getLastBMI(db);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void updateDateDisplay() {
        formattedDate1 = dateFormat1.format(calendar.getTime());
        formattedDate2 = dateFormat2.format(calendar.getTime());
        tvToday.setText(formattedDate2);
    }

    // обработчик нажатий на кнопку для воды
    private void handleImageButtonClick(int index) {
        for (int i = 0; i <= index; i++) {
            imageButtons[i].setColorFilter(Color.parseColor("#FF018786"));
        }
        for (int i = index + 1; i < imageButtons.length; i++) {
            imageButtons[i].setColorFilter(Color.parseColor("#A6CED3"));
        }

        int activeButtonsCount = index + 1;
        float waterVolume = (float) activeButtonsCount * 0.25f;
        waterLabel.setText("Вода: " + waterVolume + "л");
        saveWaterData(waterVolume); // Сохранение данных в базу
    }

    private void loadWaterData() {
        waterModel = DbWater.getWaterDataByDate(db, formattedDate1);
        if (waterModel == null) {
            waterModel = new WaterModel(formattedDate1, 0);
        }
    }

    private void saveWaterData(float waterVolume) {
        waterModel.setCount(waterVolume);

        if (DbWater.getWaterDataByDate(db, formattedDate1) == null) {
            DbWater.add(db, waterModel);
        } else {
            DbWater.update(db, waterModel);
        }
    }

    private void updateWaterDisplay() {
        float waterVolume = waterModel.getCount();
        int activeButtonsCount = (int) (waterVolume / 0.25f);

        for (int i = 0; i < activeButtonsCount; i++) {
            imageButtons[i].setColorFilter(Color.parseColor("#FF018786"));
        }
        for (int i = activeButtonsCount; i < imageButtons.length; i++) {
            imageButtons[i].setColorFilter(Color.parseColor("#A6CED3"));
        }

        waterLabel.setText("Вода: " + waterVolume + "л");
    }

    public void showWaterInfoDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Важность питья воды")
                .setMessage("В среднем, человеку рекомендуется пить около 2 литров воды в день. Вода необходима " +
                        "для поддержания всех функций организма, включая регулирование температуры, транспортировку " +
                        "питательных веществ и выведение токсинов. Недостаток воды может привести к обезвоживанию, " +
                        "которое вызывает усталость, головные боли и ухудшение концентрации.")
                .setPositiveButton("ОК", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}