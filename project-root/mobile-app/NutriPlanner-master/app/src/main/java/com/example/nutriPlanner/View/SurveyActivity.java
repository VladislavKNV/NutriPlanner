package com.example.nutriPlanner.View;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;

import com.example.nutriPlanner.DataBase.DbBMI;
import com.example.nutriPlanner.DataBase.DbHelper;
import com.example.nutriPlanner.DataBase.DbUser;
import com.example.nutriPlanner.Helpers.ApiService;
import com.example.nutriPlanner.Helpers.GetDateData;
import com.example.nutriPlanner.Helpers.NetworkChangeListener;
import com.example.nutriPlanner.Helpers.RetrofitInstance;
import com.example.nutriPlanner.Model.ApiModels.AddOrUpdateBMIRequest;
import com.example.nutriPlanner.Model.ApiModels.AddOrUpdateBMIResponse;
import com.example.nutriPlanner.Model.ApiModels.RegisterRequest;
import com.example.nutriPlanner.Model.ApiModels.RegisterResponse;
import com.example.nutriPlanner.Model.ApiModels.UpdateUserInfoRequest;
import com.example.nutriPlanner.Model.BMIModel;
import com.example.nutriPlanner.Model.UserModel;
import com.example.nutriPlanner.R;
import com.example.nutriPlanner.View.Survey.ActivityLevelFragment;
import com.example.nutriPlanner.View.Survey.BirthdayFragment;
import com.example.nutriPlanner.View.Survey.GenderFragment;
import com.example.nutriPlanner.View.Survey.GoalFragment;
import com.example.nutriPlanner.View.Survey.HeightFragment;
import com.example.nutriPlanner.View.Survey.OnSurveyInteractionListener;
import com.example.nutriPlanner.View.Survey.WeightFactorFragment;
import com.example.nutriPlanner.View.Survey.WeightFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SurveyActivity extends AppCompatActivity implements OnSurveyInteractionListener {

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    private static final int PERMISSION_REQUEST_CODE = 123;
    private FragmentManager fragmentManager;
    private ApiService apiService;
    private SQLiteDatabase db;
    private ProgressBar progressBar;
    private Fragment[] fragments;
    private ImageButton prevButton;
    private int currentIndex = 0;
    public boolean correctData = true, isReg = true;
    public String goal;
    private int height, idUs;
    private double desiredWeight, currentWeight, weightFactor;
    private String gender, birthDate, activityLevel, date;
    private String name, email, password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        if (checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, PERMISSION_REQUEST_CODE);
        }
        getDate();
        Intent intent = getIntent();
        name = (String) intent.getSerializableExtra("name");
        email = (String) intent.getSerializableExtra("email");
        password = (String) intent.getSerializableExtra("password");
        isReg = (boolean) intent.getSerializableExtra("isReg");
        idUs = (int) intent.getSerializableExtra("idUser");
        apiService = RetrofitInstance.getRetrofitInstance().create(ApiService.class);
        bind();
        fragmentManager = getSupportFragmentManager();
        fragments = new Fragment[]{new GoalFragment(), new GenderFragment(), new BirthdayFragment(), new HeightFragment(),
                new WeightFragment(), new ActivityLevelFragment(), new WeightFactorFragment()};

        progressBar.setMax(fragments.length * 100);
        replaceFragment(fragments[currentIndex]);
        progressBar.setProgress((currentIndex + 1) * 100);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPreviousFragment();
            }
        });
    }


    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        super.onStart();
    }


    private void bind(){
        prevButton = findViewById(R.id.imageButton);
        progressBar = findViewById(R.id.progressBar);
        db = new DbHelper(getApplicationContext()).getReadableDatabase();
    }

    public void setCorrectData(boolean correctData)
    {
        this.correctData = correctData;
    }

    public void onNextButtonClick(View view) {
        if (correctData == true)
        {
            if (fragments[currentIndex] instanceof GoalFragment) {
                ((GoalFragment) fragments[currentIndex]).notifyGoalSelected();
            }
            if (fragments[currentIndex] instanceof GenderFragment) {
                ((GenderFragment) fragments[currentIndex]).notifyGenderSelected();
            }
            if (fragments[currentIndex] instanceof BirthdayFragment) {
                ((BirthdayFragment) fragments[currentIndex]).notifyDateSelected();
            }
            if (fragments[currentIndex] instanceof HeightFragment) {
                ((HeightFragment) fragments[currentIndex]).notifyHeightSelected();
            }
            if (fragments[currentIndex] instanceof WeightFragment) {
                ((WeightFragment) fragments[currentIndex]).notifyWeightSelected();
            }
            if (fragments[currentIndex] instanceof ActivityLevelFragment) {
                ((ActivityLevelFragment) fragments[currentIndex]).notifyALevelSelected();
            }
            if (fragments[currentIndex] instanceof WeightFactorFragment) {
                ((WeightFactorFragment) fragments[currentIndex]).notifyWFactorSelected();
            }

            currentIndex++;
            if (currentIndex < fragments.length) {
                replaceFragment(fragments[currentIndex]);
                updateProgressBar();

            } else {
                String date2 = date.substring(0, 10);
                if (isReg == true){
                    RegisterRequest request = new RegisterRequest(2, name, email, password, gender, birthDate, goal, desiredWeight, activityLevel, weightFactor, date, date, height, currentWeight, date2);
                    registerUser(request);
                } else {
                    updateUserInfoOnServer(idUs , name, goal, desiredWeight, activityLevel, weightFactor);
                    addOrUpdateBMIOnServer(idUs, height, currentWeight, date2);
                }

            }
        }else {
            Toast.makeText(this, "Предоставьте данные", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPreviousFragment() {
        if (currentIndex > 0) {
            currentIndex--;
            replaceFragment(fragments[currentIndex]);
            updateProgressBar();
        } else {
            Intent intent;
            if(isReg) {
                intent = new Intent(this, Registration.class);
            } else {
                intent = new Intent(this, AccountActivity.class);
            }
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }

    private void replaceFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void updateProgressBar() {
        progressBar.setProgress((currentIndex + 1) * 100);
    }

    @Override
    public void onGoalSelected(String selectedGoal) {
        goal = selectedGoal;

        if (selectedGoal.equals("MAINTENANCE")) {
            weightFactor = 0;
            Fragment[] newFragments = new Fragment[fragments.length - 1];
            int newIndex = 0;
            for (int i = 0; i < fragments.length; i++) {
                if (!(fragments[i] instanceof WeightFactorFragment)) {
                    newFragments[newIndex] = fragments[i];
                    newIndex++;
                }
            }
            fragments = newFragments;
            progressBar.setMax(fragments.length * 100);
            replaceFragment(fragments[currentIndex]);
        }
    }

    @Override
    public void onGenderSelected(String gender) {
        this.gender = gender;
    }

    @Override
    public void onDateSelected(int day, int month, int year) {
        String formattedDay = (day < 10) ? "0" + day : String.valueOf(day);
        String formattedMonth = (month < 10) ? "0" + month : String.valueOf(month);

        birthDate = year + "-" + formattedMonth + "-" + formattedDay;
    }

    @Override
    public void onHeightSelected(int height) {
        this.height = height;
    }

    @Override
    public void onWeightSelected(double currentWeight, double desiredWeight) {
        this.currentWeight = currentWeight;
        this.desiredWeight = desiredWeight;
    }

    @Override
    public void onALevelSelected(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    @Override
    public void onWFactorSelected(double weightFactor) {
        this.weightFactor = weightFactor;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    private void getDate() {
        GetDateData downloadDataTask = new GetDateData();
        downloadDataTask.execute();
        GetDateData.DownloadDataListener listener = new GetDateData.DownloadDataListener() {
            @Override
            public void onDataDownloaded(String result) {
                if (result != null) {
                    date = result;
                    Log.d("SurveyActivity", "Дата: " + result);
                } else {
                    Toast.makeText(SurveyActivity.this, "Произошла ошибка!", Toast.LENGTH_SHORT).show();
                }
            }
        };
        downloadDataTask.setDownloadDataListener(listener);
    }

    private void registerUser(RegisterRequest request) {
        apiService.register(request).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse registerResponse = response.body();
                    Toast.makeText(SurveyActivity.this, "Регистрация прошла успешно", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SurveyActivity.this, Authorization.class);
                        startActivity(intent);
                } else {
                    // Ошибка регистрации на сервере
                    Toast.makeText(SurveyActivity.this, "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                Toast.makeText(SurveyActivity.this, "Попробуйте снова", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SurveyActivity.this, Registration.class);
                startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(SurveyActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addOrUpdateBMIOnServer(int userId, int height, double currentWeight, String measurementDate) {
        AddOrUpdateBMIRequest request = new AddOrUpdateBMIRequest(userId, height, currentWeight, measurementDate);

        apiService.addOrUpdateBMI(request).enqueue(new Callback<AddOrUpdateBMIResponse>() {
            @Override
            public void onResponse(Call<AddOrUpdateBMIResponse> call, Response<AddOrUpdateBMIResponse> response) {
                if (response.isSuccessful()) {
                    AddOrUpdateBMIResponse bmiResponse = response.body();
                    int bmiRecordId = bmiResponse.getId();
                    // Обновите локальную базу данных с использованием возвращенного ID
                    try {
                        BMIModel bmiModel = new BMIModel(bmiRecordId, userId, height, currentWeight, measurementDate);
                        if (DbBMI.addOrUpdate(db, bmiModel) != -1) {
                            Toast.makeText(SurveyActivity.this, "BMI успешно добавлен/обновлен", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SurveyActivity.this, "Ошибка добавления в локальную базу данных", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(SurveyActivity.this, "Ошибка добавления в локальную базу данных", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SurveyActivity.this, "Ошибка добавления/обновления на сервере", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AddOrUpdateBMIResponse> call, Throwable t) {
                Toast.makeText(SurveyActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserInfoOnServer(int userId, String name, String goal, double desiredWeight, String activityLevel, double weightFactor) {
        UpdateUserInfoRequest request = new UpdateUserInfoRequest(userId, name, goal, desiredWeight, activityLevel, weightFactor);

        apiService.updateUserInfo(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Успешно обновлено на сервере
                    try {
                        UserModel userModel = new UserModel(userId, name, goal, desiredWeight, activityLevel, weightFactor);

                        if (DbUser.updateUserInfo(db, userModel) > 0) {
                            Toast.makeText(SurveyActivity.this, "Информация о пользователе успешно обновлена", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SurveyActivity.this, AccountActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(SurveyActivity.this, "Ошибка обновления в локальной базе данных", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(SurveyActivity.this, "Ошибка обновления в локальной базе данных", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(SurveyActivity.this, "Информация о пользователе успешно обновлена", Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(SurveyActivity.this, "Ошибка обновления на сервере", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Ошибка сети
                Toast.makeText(SurveyActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (currentIndex > 0) {
            showPreviousFragment();
        } else {
            super.onBackPressed();
        }
    }

}
