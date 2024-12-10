package com.example.nutriPlanner.View;

import static com.example.nutriPlanner.DataBase.DbUser.getUserId;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutriPlanner.DataBase.DbFavoriteFood;
import com.example.nutriPlanner.DataBase.DbFoodIntake;
import com.example.nutriPlanner.DataBase.DbFoods;
import com.example.nutriPlanner.DataBase.DbHelper;
import com.example.nutriPlanner.DataBase.DbUser;
import com.example.nutriPlanner.Helpers.ApiService;
import com.example.nutriPlanner.Helpers.RetrofitInstance;
import com.example.nutriPlanner.Model.ApiModels.AddFoodIntakeRequest;
import com.example.nutriPlanner.Model.ApiModels.AddFoodIntakeResponse;
import com.example.nutriPlanner.Model.FavoriteFoodModel;
import com.example.nutriPlanner.Model.FoodIntakeModel;
import com.example.nutriPlanner.Model.FoodModel;
import com.example.nutriPlanner.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MealActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ListView listViewFoodCards;
    private CardListAdapter cardListAdapter;
    private List<FoodModel> allFoodsList;
    private List<FoodModel> currentFoodsList;
    private List<FoodModel> favoriteFoodsList;
    private List<FoodModel> foodIntakeFoodsList;
    private List<FavoriteFoodModel> favoriteList;
    private List<FoodIntakeModel> foodIntakeList;
    List<FoodIntakeCountModel> foodIntakeCountList;
    private ApiService apiService;
    private SQLiteDatabase db;
    private EditText txtSearch;
    private TextView tvMealType;
    private String date;
    private String mealType;
    private String searchText = "";
    private int userIdg;
    private boolean isVisibility = false;
    private int currentMenu = R.id.menu_meal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);

        apiService = RetrofitInstance.getRetrofitInstance().create(ApiService.class);
        date = getIntent().getStringExtra("date");
        mealType = getIntent().getStringExtra("mealType");
        bind();
        loadFoods();
        loadFavoriteList();
        loadFoodIntakeList();
        setupListViewAdapter();
        setupBottomNavigationView();
        setupSearchListener();
        bottomNavigationView.setSelectedItemId(R.id.menu_search);
    }

    private void bind() {
        db = new DbHelper(getApplicationContext()).getReadableDatabase();
        allFoodsList = new ArrayList<>();
        currentFoodsList = new ArrayList<>();
        favoriteFoodsList = new ArrayList<>();
        foodIntakeFoodsList = new ArrayList<>();
        foodIntakeList = new ArrayList<>();
        foodIntakeCountList = new ArrayList<>();
        favoriteList = new ArrayList<>();
        bottomNavigationView = findViewById(R.id.bottom_navigation_meal);
        listViewFoodCards = findViewById(R.id.listViewFoodCards);
        txtSearch = findViewById(R.id.txtSearch);
        tvMealType = findViewById(R.id.tvMealType);
        tvMealType.setText(mealType);
    }

    private void loadFoods() {
        Cursor query = DbFoods.getAll(db);
        allFoodsList.clear();
        while (query.moveToNext()) {
            int id = query.getInt(0);
            String name = query.getString(1);
            String category = query.getString(3);
            int calories = query.getInt(7);
            String servingSize = query.getString(11);
            allFoodsList.add(new FoodModel(id, name, category, calories, servingSize));
        }
        query.close();
        currentFoodsList.addAll(allFoodsList);
    }

    private void loadFavoriteList() {
        Cursor query = DbFavoriteFood.getAllFavoriteFoods(db);
        favoriteList.clear();
        while (query.moveToNext()) {
            int id = query.getInt(0);
            int userId = query.getInt(1);
            int foodId = query.getInt(2);
            favoriteList.add(new FavoriteFoodModel(id, userId, foodId));
        }
        query.close();
    }

    private void loadFoodIntakeList() {
        Cursor query = DbFoodIntake.getFoodIntakesByMealTypeAndDate(db, mealType, date);
        Map<Integer, FoodIntakeCountModel> foodIntakeMap = new HashMap<>();
        foodIntakeList.clear();
        foodIntakeCountList.clear();

        while (query.moveToNext()) {
            int id = query.getInt(0);
            int userId = query.getInt(1);
            int foodId = query.getInt(2);
            String mealType = query.getString(3);
            String date = query.getString(4);

            FoodIntakeModel foodIntake = new FoodIntakeModel(id, userId, foodId, mealType, date);
            if (foodIntakeMap.containsKey(foodId)) {
                foodIntakeMap.get(foodId).incrementCount();
            } else {
                foodIntakeMap.put(foodId, new FoodIntakeCountModel(foodIntake, 1));
            }
        }
        query.close();

        for (FoodIntakeCountModel countModel : foodIntakeMap.values()) {
            foodIntakeList.add(countModel.getFoodIntake());
            foodIntakeCountList.add(countModel);
        }
    }

    private void setupListViewAdapter() {
        cardListAdapter = new CardListAdapter(this, sortFoodListByMealType(currentFoodsList, mealType));
        listViewFoodCards.setAdapter(cardListAdapter);
    }

    private List<FoodModel> sortFoodListByMealType(List<FoodModel> foodList, String mealType) {
        String mealTd = "null";
        if (mealType.equals("ЗАВТРАК")){mealTd = "Breakfast";}
        if (mealType.equals("ОБЕД")){mealTd = "Lunch";}
        if (mealType.equals("УЖИН")){mealTd = "Dinner";}
        if (mealType.equals("ПЕРЕКУСЫ")){mealTd = "Snack";}
        String finalMealTd = mealTd;
        Collections.sort(foodList, new Comparator<FoodModel>() {
            @Override
            public int compare(FoodModel o1, FoodModel o2) {
                // Сначала сортировка по совпадению с указанным mealType
                if (o1.getCategory().equals(finalMealTd) && !o2.getCategory().equals(finalMealTd)) {
                    return -1;
                } else if (!o1.getCategory().equals(finalMealTd) && o2.getCategory().equals(finalMealTd)) {
                    return 1;
                }

                // Затем сортировка по остальным mealType в заданном порядке
                List<String> order = Arrays.asList("Breakfast", "Lunch", "Dinner", "Snack");
                return Integer.compare(order.indexOf(o1.getCategory()), order.indexOf(o2.getCategory()));
            }
        });
        return foodList;
    }


    private void setupBottomNavigationView() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            currentMenu = item.getItemId();
            if (currentMenu == R.id.menu_meal) {
                isVisibility = true;
                updateFoodIntakeList();
            } else if (currentMenu == R.id.menu_search) {
                isVisibility = false;
                updateCurrentFoodsList(allFoodsList);
            }
            else if (currentMenu == R.id.menu_favorite) {
                isVisibility = false;
                updateFavoriteFoodsList();
            }
            filterFoodsList();
            return true;
        });
    }

    private void setupSearchListener() {
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                searchText = s.toString().toLowerCase().trim();
                filterFoodsList();
            }
        });
    }

    private void updateCurrentFoodsList(List<FoodModel> foods) {
        currentFoodsList.clear();
        currentFoodsList.addAll(sortFoodListByMealType(foods, mealType));
    }

    private void updateFavoriteFoodsList() {
        favoriteFoodsList.clear();
        for (FavoriteFoodModel favorite : favoriteList) {
            for (FoodModel food : allFoodsList) {
                if (food.getId() == favorite.getFoodId()) {
                    favoriteFoodsList.add(food);
                    break;
                }
            }
        }
        updateCurrentFoodsList(sortFoodListByMealType(favoriteFoodsList, mealType));
    }

    private void updateFoodIntakeList() {
        foodIntakeFoodsList.clear();
        for (FoodIntakeModel foodIntakeModel : foodIntakeList) {
            for (FoodModel food : allFoodsList) {
                if (food.getId() == foodIntakeModel.getFoodId()) {
                    foodIntakeFoodsList.add(food);
                    break;
                }
            }
        }
        updateCurrentFoodsList(foodIntakeFoodsList);
    }

    private void filterFoodsList() {
        List<FoodModel> filteredFoods = new ArrayList<>();
        for (FoodModel food : currentFoodsList) {
            if (food.getFoodName().toLowerCase().contains(searchText)) {
                filteredFoods.add(food);
            }
        }
        cardListAdapter.updateFoods( sortFoodListByMealType(filteredFoods, mealType));
    }

    private class CardListAdapter extends BaseAdapter {
        private Context context;
        private List<FoodModel> foodList;

        CardListAdapter(Context context, List<FoodModel> foods) {
            this.context = context;
            this.foodList = new ArrayList<>(foods);
        }

        @Override
        public int getCount() {
            return foodList.size();
        }

        @Override
        public Object getItem(int position) {
            return foodList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        void updateFoods(List<FoodModel> foods) {
            this.foodList.clear();
            this.foodList.addAll(foods);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.foods_card_view, null);
            TextView name = view.findViewById(R.id.txtCardFoodName);
            TextView calories = view.findViewById(R.id.txtCardFoodCalories);
            TextView servingSize = view.findViewById(R.id.txtCardFoodServingSize);
            LinearLayout ln = view.findViewById(R.id.con);
            TextView txCount = view.findViewById(R.id.txCount);
            ImageButton buttonAdd = view.findViewById(R.id.buttonAdd);
            ImageButton buttonDel = view.findViewById(R.id.buttonDel);

            FoodModel food = foodList.get(position);
            name.setText(food.getFoodName());
            calories.setText(food.getCaloriesPer100g() + " ккал");
            servingSize.setText(food.getServingSize());

            if (isVisibility == true){
                ln.setVisibility(View.VISIBLE);
                txCount.setVisibility(View.VISIBLE);
                for (FoodIntakeCountModel countModel : foodIntakeCountList) {
                    if (countModel.getFoodIntake().getFoodId() == food.getId()) {
                        txCount.bringToFront();
                        txCount.setText("x" + countModel.getCount());
                        break;
                    }
                }
                userIdg = DbUser.getUserId(db);
                buttonAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AddFoodIntakeRequest request = new AddFoodIntakeRequest(userIdg, food.getId(), mealType, date);

                        apiService.addFoodIntake(request).enqueue(new Callback<AddFoodIntakeResponse>() {
                            @Override
                            public void onResponse(Call<AddFoodIntakeResponse> call, Response<AddFoodIntakeResponse> response) {
                                if (response.isSuccessful()) {
                                    // Получить ID из ответа сервера
                                    int serverId = response.body().getIdfoodintake();

                                    // Успешно добавлено на сервере, обновить локальную базу данных
                                    try {
                                        int userId = getUserId(db);
                                        FoodIntakeModel foodIntakeModel = new FoodIntakeModel(serverId, userId, food.getId(), mealType, date);
                                        if (DbFoodIntake.add(db, foodIntakeModel) != -1) {
                                            loadFoodIntakeList();
                                            updateFoodIntakeList();
                                            notifyDataSetChanged(); // Обновление списка
                                        } else {

                                            Toast.makeText(MealActivity.this, "Ошибка добавления", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        Log.e("MealActivity", "Error adding food intake to local database", e);
                                        Toast.makeText(MealActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Log.e("MealActivity", "Server error: " + response.code() + " - " + response.message());
                                    Toast.makeText(MealActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<AddFoodIntakeResponse> call, Throwable t) {
                                Log.e("MealActivity", "Network error: " + t.getMessage(), t);
                            }
                        });

                    }
                });

                buttonDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (FoodIntakeCountModel countModel : foodIntakeCountList) {
                            if (countModel.getFoodIntake().getFoodId() == food.getId()) {
                                if (countModel.getCount() > 0) {
                                    countModel.decrementCount();

                                    // Удалить запись из базы данных через API
                                    int id = DbFoodIntake.getLastFoodIntakeId(db, mealType, food.getId(), date);
                                    apiService.deleteFoodIntake(id).enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            if (response.isSuccessful()) {
                                                foodIntakeCountList.remove(countModel);
                                                try {
                                                    if (DbFoodIntake.deleteFoodIntakeById(db, id) > 0) {
                                                        loadFoodIntakeList();
                                                        updateFoodIntakeList();
                                                        if (countModel.getCount() > 1) {
                                                            notifyDataSetChanged(); // Обновление списка
                                                        } else filterFoodsList();

                                                    }
                                                } catch (Exception e) {
                                                    Toast.makeText(MealActivity.this, "Ошибка удаления из локальной базы данных", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(context, "Ошибка при удалении записи", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {
                                            Toast.makeText(context, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                return;
                            }
                        }
                    }
                });
            }

            view.setOnClickListener(v -> {
                Intent intent = new Intent(MealActivity.this, AboutFoodActivity.class);
                intent.putExtra("idFood", food.getId());
                intent.putExtra("date", date);
                intent.putExtra("mealType", mealType);
                startActivity(intent);
            });

            return view;
        }
    }

    private class FoodIntakeCountModel {
        private FoodIntakeModel foodIntake;
        private int count;

        public FoodIntakeCountModel(FoodIntakeModel foodIntake, int count) {
            this.foodIntake = foodIntake;
            this.count = count;
        }

        public FoodIntakeModel getFoodIntake() {
            return foodIntake;
        }

        public int getCount() {
            return count;
        }

        public void incrementCount() {
            this.count++;
        }
        public void decrementCount() {
            this.count--;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    public void backButton(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
    }
}
