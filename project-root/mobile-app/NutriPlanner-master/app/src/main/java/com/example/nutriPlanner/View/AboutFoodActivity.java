package com.example.nutriPlanner.View;


import static com.example.nutriPlanner.DataBase.DbUser.getUserId;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.nutriPlanner.DataBase.DbFavoriteFood;
import com.example.nutriPlanner.DataBase.DbFoodIntake;
import com.example.nutriPlanner.DataBase.DbFoods;
import com.example.nutriPlanner.DataBase.DbHelper;
import com.example.nutriPlanner.Helpers.ApiService;
import com.example.nutriPlanner.Helpers.GetDateData;
import com.example.nutriPlanner.Helpers.RetrofitInstance;
import com.example.nutriPlanner.Model.ApiModels.AddFavoriteFoodRequest;
import com.example.nutriPlanner.Model.ApiModels.AddFavoriteFoodResponse;
import com.example.nutriPlanner.Model.ApiModels.AddFoodIntakeRequest;
import com.example.nutriPlanner.Model.ApiModels.AddFoodIntakeResponse;
import com.example.nutriPlanner.Model.FavoriteFoodModel;
import com.example.nutriPlanner.Model.FoodIntakeModel;
import com.example.nutriPlanner.Model.FoodModel;
import com.example.nutriPlanner.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AboutFoodActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private ApiService apiService;
    private FoodModel foodModel;
    private ConstraintLayout constraintLayoutAF3;
    private ImageButton favoriteButtonAF;
    private TextView tvMealTypeAF, tvFoodNameAF;
    private RatingBar ratingBar;
    private TextView tvsServingSize, tvCalories;
    private ProgressBar proteinsProgressBarAF, carbsProgressBarAF, fatsProgressBarAF;
    private TextView proteinsProgressTextAF, carbsProgressTextAF, fatsProgressTextAF;
    private TextView tvDescription, tvIngredients, tvRecipe;
    private String mealType;
    private String date, dateMeal;
    int idFood;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_food);
        bind();
        apiService = RetrofitInstance.getRetrofitInstance().create(ApiService.class);
        Intent intent = getIntent();
        dateMeal = getIntent().getStringExtra("date");
        idFood = (int) intent.getSerializableExtra("idFood");
        mealType = (String) intent.getSerializableExtra("mealType");
        foodModel = DbFoods.getById(db, idFood);

        setData();
    }

    private void bind(){
        db = new DbHelper(getApplicationContext()).getReadableDatabase();

        constraintLayoutAF3 = findViewById(R.id.constraintLayoutAF3);
        constraintLayoutAF3.setVisibility(View.GONE);

        tvMealTypeAF = findViewById(R.id.tvMealTypeAF);
        tvFoodNameAF = findViewById(R.id.tvFoodNameAF);
        favoriteButtonAF = findViewById(R.id.FavoriteButtonAF);

        ratingBar = findViewById(R.id.ratingBar);
        tvsServingSize = findViewById(R.id.tvsServingSize);
        tvCalories = findViewById(R.id.tvCalories);
        proteinsProgressBarAF = findViewById(R.id.proteinsProgressBarAF);
        carbsProgressBarAF = findViewById(R.id.carbsProgressBarAF);
        fatsProgressBarAF = findViewById(R.id.fatsProgressBarAF);
        proteinsProgressTextAF = findViewById(R.id.proteinsProgressTextAF);
        carbsProgressTextAF = findViewById(R.id.carbsProgressTextAF);
        fatsProgressTextAF = findViewById(R.id.fatsProgressTextAF);

        tvDescription = findViewById(R.id.tvDescription);
        tvIngredients = findViewById(R.id.tvIngredients);
        tvRecipe = findViewById(R.id.tvRecipe);
    }

    private void setData() {
        tvFoodNameAF.setText(String.valueOf(foodModel.getFoodName()));
        tvMealTypeAF.setText(String.valueOf(mealType));

        ratingBar.setRating((float) foodModel.getFoodRating());
        tvsServingSize.setText(String.valueOf(foodModel.getServingSize()));
        tvCalories.setText(String.valueOf(foodModel.getCaloriesPer100g() + " ккал"));

        double total = foodModel.getProteins() + foodModel.getCarbohydrates() + foodModel.getFats();
        double proteinsPercentage = Math.round((foodModel.getProteins() / total) * 100);
        double carbsPercentage = Math.round((foodModel.getCarbohydrates() / total) * 100);
        double fatsPercentage = Math.round((foodModel.getFats() / total) * 100);

        proteinsProgressBarAF.setProgress((int) proteinsPercentage);
        carbsProgressBarAF.setProgress((int) carbsPercentage);
        fatsProgressBarAF.setProgress((int) fatsPercentage);

        proteinsProgressTextAF.setText(String.valueOf(foodModel.getProteins() + "г | " + proteinsPercentage + "%"));
        carbsProgressTextAF.setText(String.valueOf(foodModel.getCarbohydrates() + "г | " + carbsPercentage + "%"));
        fatsProgressTextAF.setText(String.valueOf(foodModel.getFats() + "г | " + fatsPercentage + "%"));

        if (foodModel.getDescription() != null) {
            constraintLayoutAF3.setVisibility(View.VISIBLE);
            tvDescription.setText(String.valueOf(foodModel.getDescription()));
            tvIngredients.setText(String.valueOf(foodModel.getIngredients().replace(";", "\n")));
            tvRecipe.setText(String.valueOf(foodModel.getRecipe()));
        }
        getDate();
        setFavoriteButtonAF();
    }

    private void getDate() {
        GetDateData downloadDataTask = new GetDateData();
        downloadDataTask.execute();
        GetDateData.DownloadDataListener listener = new GetDateData.DownloadDataListener() {
            @Override
            public void onDataDownloaded(String result) {
                if (result != null) {
                    date = result.substring(0, 10);
                } else {
                    Log.e("AboutFoodActivity", "Error. getDate.");
                }
            }
        };
        downloadDataTask.setDownloadDataListener(listener);
    }

    public void addButton(View view) {
        int userId = getUserId(db);
        AddFoodIntakeRequest request = new AddFoodIntakeRequest(userId, foodModel.getId(), mealType, date);

        apiService.addFoodIntake(request).enqueue(new Callback<AddFoodIntakeResponse>() {
            @Override
            public void onResponse(Call<AddFoodIntakeResponse> call, Response<AddFoodIntakeResponse> response) {
                if (response.isSuccessful()) {
                    //  ID из ответа сервера
                    int serverId = response.body().getIdfoodintake();

                    //  обновить локальную базу данных
                    if (addFoodIntakeDb(serverId)) {
                        Intent intent = new Intent(AboutFoodActivity.this, MealActivity.class);
                        intent.putExtra("date", dateMeal);
                        intent.putExtra("mealType", mealType);
                        startActivity(intent);
                    } else {
                        Log.e("AboutFoodActivity", "Failed to add to server");
                    }
                } else {
                    Log.e("AboutFoodActivity", "Failed to add to server");
                }
            }

            @Override
            public void onFailure(Call<AddFoodIntakeResponse> call, Throwable t) {
                Log.e("AboutFoodActivity", "Network error: " + t.getMessage(), t);
            }
        });
    }

    private boolean addFoodIntakeDb(int serverId) {
        try {
            int userId = getUserId(db);

            FoodIntakeModel foodIntakeModel = new FoodIntakeModel(serverId, userId, foodModel.getId(), mealType, date);
            if (DbFoodIntake.add(db, foodIntakeModel) != -1) {
                return true;
            } else {
                Log.e("FoodIntakeActivity", "Failed to add food intake to local database");
                Toast.makeText(this, "Ошибка добавления", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (Exception e) {
            Log.e("FoodIntakeActivity", "Error adding food intake data: " + e.getMessage(), e);
            Toast.makeText(this, "Ошибка добавления данных", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void favoriteButton(View view) {
        int foodId = foodModel.getId();
        int userId = getUserId(db);

        if (DbFavoriteFood.isFavoriteFood(db, foodId)) {
            deleteFavoriteFoodOnServer(DbFavoriteFood.getFavoriteFoodIdByFoodId(db, foodModel.getId()));
        } else {
            addFavoriteFoodOnServer(userId, foodId);
        }
    }


    private void addFavoriteFoodOnServer(int userId, int foodId) {
        AddFavoriteFoodRequest request = new AddFavoriteFoodRequest(userId, foodId);

        apiService.addFavoriteFood(request).enqueue(new Callback<AddFavoriteFoodResponse>() {
            @Override
            public void onResponse(Call<AddFavoriteFoodResponse> call, Response<AddFavoriteFoodResponse> response) {
                if (response.isSuccessful()) {
                    // Успешно добавлено на сервере, обновить локальную базу данных
                    AddFavoriteFoodResponse responseBody = response.body();
                    if (responseBody != null) {
                        int favoriteFoodId = responseBody.getIdfavoritefood();
                        try {
                            //сохранение в локальной базе данных
                            FavoriteFoodModel favoriteFoodModel = new FavoriteFoodModel(favoriteFoodId, userId, foodId);
                            if (DbFavoriteFood.add(db, favoriteFoodModel) != -1) {
                                setFavoriteButtonAF();
                            } else {
                                Toast.makeText(AboutFoodActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
                                Log.e("AboutFoodActivity", "Error adding to local database");
                            }
                        } catch (Exception e) {
                            Toast.makeText(AboutFoodActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
                            Log.e("AboutFoodActivity", "Error adding to local database: " + e.getMessage(), e);
                        }
                    }
                } else {
                    Log.e("AboutFoodActivity", "Error adding on server: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<AddFavoriteFoodResponse> call, Throwable t) {
                Log.e("AboutFoodActivity", "Network error: " + t.getMessage(), t);
            }
        });
    }


    private void deleteFavoriteFoodOnServer(int favoriteFoodId) {
        apiService.deleteFavoriteFood(favoriteFoodId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Успешно удалено на сервере, обновить локальную базу данных
                    try {
                        if (DbFavoriteFood.deleteFavoriteFood(db, favoriteFoodId) > 0) {
                            setFavoriteButtonAF();
                        }
                    } catch (Exception e) {
                        Toast.makeText(AboutFoodActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
                        Log.e("AboutFoodActivity", "Error deleting from local database: " + e.getMessage(), e);
                    }
                } else {
                    Toast.makeText(AboutFoodActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
                    Log.e("AboutFoodActivity", "Error deleting on server: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("AboutFoodActivity", "Network error: " + t.getMessage(), t);
            }
        });
    }

    private void setFavoriteButtonAF() {
        if (DbFavoriteFood.isFavoriteFood(db, foodModel.getId()) == false) {
            favoriteButtonAF.setColorFilter(ContextCompat.getColor(this, R.color.gray));
        } else {
            favoriteButtonAF.setColorFilter(ContextCompat.getColor(this, R.color.main_color));
        }
    }

    public void backButtonAF(View view){
        Intent intent = new Intent(this, MealActivity.class);
        intent.putExtra("date", dateMeal);
        intent.putExtra("mealType", mealType);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MealActivity.class);
        intent.putExtra("date", dateMeal);
        intent.putExtra("mealType", mealType);
        startActivity(intent);
    }
}