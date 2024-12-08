package com.example.nutriPlanner.View;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.nutriPlanner.DataBase.DbBMI;
import com.example.nutriPlanner.DataBase.DbHelper;
import com.example.nutriPlanner.DataBase.DbUser;
import com.example.nutriPlanner.Helpers.ApiService;
import com.example.nutriPlanner.Helpers.GetDateData;
import com.example.nutriPlanner.Helpers.PasswordEncryptor;
import com.example.nutriPlanner.Helpers.RetrofitInstance;
import com.example.nutriPlanner.Model.ApiModels.AddOrUpdateBMIRequest;
import com.example.nutriPlanner.Model.ApiModels.AddOrUpdateBMIResponse;
import com.example.nutriPlanner.Model.ApiModels.UpdatePasswordRequest;
import com.example.nutriPlanner.Model.ApiModels.UpdateUserInfoRequest;
import com.example.nutriPlanner.Model.BMIModel;
import com.example.nutriPlanner.Model.UserModel;
import com.example.nutriPlanner.R;
import com.example.nutriPlanner.ProgressActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountActivity extends AppCompatActivity implements GetDateData.DownloadDataListener {

    private SQLiteDatabase db;
    private ApiService apiService;
    private UserModel userModel;
    private BMIModel bmiModel;
    private String date;
    private BottomNavigationView bottomNavigationView;
    private ConstraintLayout clGoal, clActivityLevel, clHeight, clWeight, clDesiredWeight, clWeightFactor,
            clSurveyAgain, clChangePassword, clLogOut;
    private TextView tvUserName, tvAge, tvGender, tvEmail, tvGoal, tvActivityLevel, tvHeight,
            tvWeight, tvDesiredWeight, tvWeightFactor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        apiService = RetrofitInstance.getRetrofitInstance().create(ApiService.class);
        getDate();
        bind();
        getUserData();
        setDataUser();
        View.OnClickListener constraintLayoutClickListenerEditText = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = (String) v.getTag();
                showUpdateDialogEditText(AccountActivity.this, tag);
            }
        };
        View.OnClickListener constraintLayoutClickListenerRadioButtons = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = (String) v.getTag();
                showUpdateDialogRadioGroup(AccountActivity.this, tag);
            }
        };
        View.OnClickListener constraintLayoutClickListenerSurveyAgain = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SurveyActivity.class);
                intent.putExtra("isReg", false);
                intent.putExtra("idUser", userModel.getId());
                intent.putExtra("name", userModel.getName());
                intent.putExtra("email", userModel.getEmail());
                intent.putExtra("password", userModel.getPassword());
                startActivity(intent);
            }
        };

        View.OnClickListener constraintLayoutClickListenerNewPassword = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogOut();
            }
        };
        View.OnClickListener constraintLayoutClickListenerLogOut = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogOut();
            }
        };
        // Присваиваем слушателя каждому ConstraintLayout
        tvUserName.setOnClickListener(constraintLayoutClickListenerEditText);
        clGoal.setOnClickListener(constraintLayoutClickListenerRadioButtons);
        clActivityLevel.setOnClickListener(constraintLayoutClickListenerRadioButtons);
        clWeightFactor.setOnClickListener(constraintLayoutClickListenerRadioButtons);
        clHeight.setOnClickListener(constraintLayoutClickListenerEditText);
        clWeight.setOnClickListener(constraintLayoutClickListenerEditText);
        clDesiredWeight.setOnClickListener(constraintLayoutClickListenerEditText);
        clSurveyAgain.setOnClickListener(constraintLayoutClickListenerSurveyAgain);
        clChangePassword.setOnClickListener(constraintLayoutClickListenerEditText);
        clLogOut.setOnClickListener(constraintLayoutClickListenerLogOut);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_home) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
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
                return true;
            } else {
                return false;
            }
        });

    }

    private void bind(){
        db = new DbHelper(getApplicationContext()).getReadableDatabase();
        clGoal = findViewById(R.id.clGoal);
        clActivityLevel = findViewById(R.id.clActivityLevel);
        clHeight = findViewById(R.id.clHeight);
        clWeight = findViewById(R.id.clWeight);
        clDesiredWeight = findViewById(R.id.clDesiredWeight);
        clWeightFactor = findViewById(R.id.clWeightFactor);
        clSurveyAgain = findViewById(R.id.clSurveyAgain);
        clChangePassword = findViewById(R.id.clChangePassword);
        clLogOut = findViewById(R.id.clLogOut);
        tvUserName = findViewById(R.id.tvUserName);
        tvAge = findViewById(R.id.tvAge);
        tvGender = findViewById(R.id.tvGender);
        tvEmail = findViewById(R.id.tvEmail);
        tvGoal = findViewById(R.id.tvGoal);
        tvActivityLevel = findViewById(R.id.tvActivityLevel);
        tvHeight = findViewById(R.id.tvHeight);
        tvWeight = findViewById(R.id.tvWeight);
        tvDesiredWeight = findViewById(R.id.tvDesiredWeight);
        tvWeightFactor = findViewById(R.id.tvWeightFactor);

        tvUserName.setTag("имя");
        clGoal.setTag("цель");
        clActivityLevel.setTag("уровень активности");
        clHeight.setTag("рост");
        clWeight.setTag("вес");
        clDesiredWeight.setTag("желаемый вес");
        clWeightFactor.setTag("коэффициент веса");
        clChangePassword.setTag("пароль");

        bottomNavigationView = findViewById(R.id.bottom_navigationAF);
        bottomNavigationView.setSelectedItemId(R.id.menu_profile);
    }

    private void getDate() {
        GetDateData downloadDataTask = new GetDateData();
        downloadDataTask.execute();
        downloadDataTask.setDownloadDataListener(this);
    }

    private void setDataUser(){
        tvUserName.setText(userModel.getName());
        tvEmail.setText("Почта: " + userModel.getEmail());
        if (userModel.getGender().equals("Man")) {
            tvGender.setText("Пол: мужской");
        } else tvGender.setText("Пол: женский");
        switch (userModel.getGoal()) {
            case "WEIGHT_LOSS":
                tvGoal.setText("сбросить вес");
                break;
            case "MAINTENANCE":
                tvGoal.setText("поддерживать вес");
                break;
            case "WEIGHT_GAIN":
                tvGoal.setText("набрать вес");
                break;
        }
        switch (userModel.getActivityLevel()) {
            case "SEDENTARY":
                tvActivityLevel.setText("очень низкая");
                break;
            case "LIGHT":
                tvActivityLevel.setText("низкая");
                break;
            case "MODERATE":
                tvActivityLevel.setText("средняя");
                break;
            case "ACTIVE":
                tvActivityLevel.setText("высокая");
                break;
            case "VERY_ACTIVE":
                tvActivityLevel.setText("очень высокая");
                break;
        }
        tvHeight.setText(bmiModel.getHeight() + " см");
        tvWeight.setText(bmiModel.getCurrentWeight() + " кг");
        tvDesiredWeight.setText(userModel.getDesiredWeight() + " кг");
        tvWeightFactor.setText(userModel.getWeightFactor() + " кг");

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat1.format(calendar.getTime());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate birthDate = LocalDate.parse(userModel.getBirthDate(), formatter);
        LocalDate currentDate = LocalDate.parse(formattedDate, formatter);

        int age = Period.between(birthDate, currentDate).getYears();
        tvAge.setText("Возраст: " + age);
    }

    private void getUserData(){
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
            }
        }
    }

    private void showUpdateDialogEditText(Context context, String tag) {
        // Создание пользовательского макета
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.popup_input_layout, null);
        TextInputLayout textInputLayout = dialogView.findViewById(R.id.edit_text_update1);
        TextInputEditText edit_text = dialogView.findViewById(R.id.edit_text_update);
        TextInputLayout textInputLayout1 = dialogView.findViewById(R.id.edit_text_update01);
        TextInputEditText edit_text11 = dialogView.findViewById(R.id.edit_text_update11);
        TextInputLayout textInputLayout2 = dialogView.findViewById(R.id.edit_text_update2);
        TextInputEditText edit_text21 = dialogView.findViewById(R.id.edit_text_update21);
        TextInputLayout textInputLayout3 = dialogView.findViewById(R.id.edit_text_update3);
        TextInputEditText edit_text31 = dialogView.findViewById(R.id.edit_text_update31);

        if (tag.equals("пароль")){
            textInputLayout.setVisibility(View.GONE);
            textInputLayout1.setVisibility(View.VISIBLE);
            textInputLayout2.setVisibility(View.VISIBLE);
            textInputLayout3.setVisibility(View.VISIBLE);
        }
        if (tag.equals("рост")) { edit_text.setText(bmiModel.getHeight() + "");}
        if (tag.equals("вес")) {edit_text.setText(bmiModel.getCurrentWeight() + "");}
        if (tag.equals("желаемый вес")) {edit_text.setText(userModel.getDesiredWeight() + "");}
        if (tag.equals("имя")) {edit_text.setText(userModel.getName());}

        textInputLayout.setHint("Введите " + tag);
        // Создание диалогового окна
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView)
                .setPositiveButton("Изменить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inputText = edit_text.getText().toString().trim();

                        if (inputText.isEmpty() && !tag.equals("пароль") ) {
                            Toast.makeText(context, "Поле не должно быть пустым", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        try {
                            switch (tag) {
                                case ("рост"):
                                    int height = Integer.parseInt(inputText);
                                    if (height < 50 || height > 250) {
                                        Toast.makeText(context, "Введите корректный рост (50-250 см)", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    if (date != null) {
                                        addOrUpdateBMIOnServer(bmiModel.getUserId(), height, bmiModel.getCurrentWeight(), date);
                                    } else {
                                        Toast.makeText(context, "Ошибка даты", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case ("вес"):
                                    double weight = Double.parseDouble(inputText);
                                    if (weight < 20 || weight > 300) {

                                        return;
                                    }
                                    if (date != null) {
                                        if (checkGoalAchievement(userModel.getGoal(), weight, userModel.getDesiredWeight())){
                                            addOrUpdateBMIOnServer(bmiModel.getUserId(), bmiModel.getHeight(), weight, date);
                                        } else {
                                            Toast.makeText(context, "Вес не соответствует цели", Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                        Toast.makeText(context, "Ошибка даты", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case ("желаемый вес"):
                                    double desiredWeight = Double.parseDouble(inputText);
                                    if (desiredWeight < 15 || desiredWeight > 300) {
                                        Toast.makeText(context, "Введите корректный желаемый вес (15-300 кг)", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    if (checkGoalAchievement(userModel.getGoal(), bmiModel.getCurrentWeight(), desiredWeight)){
                                        updateUserInfoOnServer(userModel.getId(), userModel.getName(), userModel.getGoal(), desiredWeight, userModel.getActivityLevel(), userModel.getWeightFactor());
                                    } else {
                                        Toast.makeText(context, "Вес не соответствует цели", Toast.LENGTH_SHORT).show();
                                    }
                                    break;

                                case ("имя"):
                                    if (inputText.isEmpty()) {
                                        if (inputText.trim().length() < 3 || inputText.trim().length() > 50) {
                                            Toast.makeText(context, "Не менее 3 символов и н еболее 50", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        Toast.makeText(context, "Имя не должно быть пустым", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    updateUserInfoOnServer(userModel.getId(), inputText, userModel.getGoal(), userModel.getDesiredWeight(), userModel.getActivityLevel(), userModel.getWeightFactor());
                                    break;
                                case ("пароль"):
                                    String inputText1 = edit_text11.getText().toString().trim();
                                    String inputText2 = edit_text21.getText().toString().trim();
                                    String inputText3 = edit_text31.getText().toString().trim();

                                    if (inputText1.isEmpty() && inputText2.isEmpty() && inputText3.isEmpty()) {
                                        Toast.makeText(context, "Пароль не должен быть пустым", Toast.LENGTH_SHORT).show();
                                        return;
                                    } else {
                                        PasswordEncryptor passwordHashes = new PasswordEncryptor();
                                        if (passwordHashes.encryptPassword(inputText1).equals(userModel.getPassword())){
                                            if (isPasswordValid(inputText2, inputText3)){

                                                String newPassword = passwordHashes.encryptPassword(inputText2);
                                                updatePasswordOnServer(userModel.getId(), userModel.getPassword(), newPassword);
                                            }
                                        }
                                    }

                                    break;
                                default:
                                    Toast.makeText(context, "Неизвестный тег: " + tag, Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        } catch (NumberFormatException e) {
                            Log.e("AccountActivity", "Invalid numeric value entered: " + e.getMessage(), e);
                        } catch (Exception e) {
                            Log.e("AccountActivity", "An error occurred: " + e.getMessage(), e);
                        }
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // Отображение диалогового окна
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void showUpdateDialogRadioGroup(Context context, String tag) {
        // Создание пользовательского макета
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.popup_radio_layout, null);
        RadioGroup radioGroup = dialogView.findViewById(R.id.radio_group);

        RadioButton radio1 = dialogView.findViewById(R.id.radio1);
        RadioButton radio2 = dialogView.findViewById(R.id.radio2);
        RadioButton radio3 = dialogView.findViewById(R.id.radio3);
        RadioButton radio4 = dialogView.findViewById(R.id.radio4);
        RadioButton radio5 = dialogView.findViewById(R.id.radio5);

        if (tag == "цель") {
            dialogView.findViewById(R.id.radio4).setVisibility(View.GONE);
            dialogView.findViewById(R.id.radio5).setVisibility(View.GONE);
            radio1.setText("Сбросить вес");
            radio2.setText("Поддерживать вес");
            radio3.setText("Набрать вес");
            switch (userModel.getGoal()){
                case "WEIGHT_LOSS":
                    radio1.setChecked(true);
                    break;
                case "MAINTENANCE":
                    radio2.setChecked(true);
                    break;
                case "WEIGHT_GAIN":
                    radio3.setChecked(true);
                    break;
            }
        } else if (tag == "коэффициент веса"){
            radio1.setText("0.3");
            radio2.setText("0.4");
            radio3.setText("0.5");
            radio4.setText("0.6");
            radio5.setText("0.7");
            double activityLevel = userModel.getWeightFactor();

            if (activityLevel == 0.3) {
                radio1.setChecked(true);
            } else if (activityLevel == 0.4) {
                radio2.setChecked(true);
            } else if (activityLevel == 0.5) {
                radio3.setChecked(true);
            } else if (activityLevel == 0.6) {
                radio4.setChecked(true);
            } else if (activityLevel == 0.7) {
                radio5.setChecked(true);
            }
        } else if (tag == "уровень активности") {
            switch (userModel.getActivityLevel()) {
                case "SEDENTARY":
                    radio1.setChecked(true);
                    break;
                case "LIGHT":
                    radio2.setChecked(true);
                    break;
                case "MODERATE":
                    radio3.setChecked(true);
                    break;
                case "ACTIVE":
                    radio4.setChecked(true);
                    break;
                case "VERY_ACTIVE":
                    radio5.setChecked(true);
                    break;
            }
        }

        // Создание диалогового окна
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView)
                .setTitle("Выберите  " + tag)
                .setPositiveButton("Выбрать", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int selectedId = radioGroup.getCheckedRadioButtonId();
                        if (selectedId != -1) {
                            RadioButton selectedRadioButton = dialogView.findViewById(selectedId);
                            String selectedText = selectedRadioButton.getText().toString();
                            if (selectedText.isEmpty()) {
                                Toast.makeText(context, "Сделайте выбор", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            switch (tag) {
                                case("уровень активности"):
                                    String actLevel = null;
                                    switch (selectedText) {
                                        case "Очень низкий уровень":
                                            actLevel = "SEDENTARY";
                                            break;
                                        case "Низкий уровень":
                                            actLevel = "LIGHT";
                                            break;
                                        case "Средний уровень":
                                            actLevel = "MODERATE";
                                            break;
                                        case "Высокий уровень":
                                            actLevel = "ACTIVE";
                                            break;
                                        case "Очень высокий уровень":
                                            actLevel = "VERY_ACTIVE";
                                            break;
                                    }
                                    updateUserInfoOnServer(userModel.getId(), userModel.getName(), userModel.getGoal(), userModel.getDesiredWeight(), actLevel, userModel.getWeightFactor());
                                    break;
                                case("цель"):
                                    String newGoal = null;
                                    switch (selectedText) {
                                        case "Сбросить вес":
                                            newGoal = "WEIGHT_LOSS";
                                            break;
                                        case "Поддерживать вес":
                                            newGoal = "MAINTENANCE";
                                            break;
                                        case "Набрать вес":
                                            newGoal = "WEIGHT_GAIN";
                                            break;
                                    }
                                    if (!newGoal.equals("MAINTENANCE")) {
                                        if (Math.abs(userModel.getWeightFactor() - 0.0) < 1e-9) {
                                            updateUserInfoOnServer(userModel.getId(), userModel.getName(), newGoal, userModel.getDesiredWeight(), userModel.getActivityLevel(), 0.3);
                                            Toast.makeText(context, "Скорректируйте коэффициент веса. По умолчанию было выставлено 0.3 ", Toast.LENGTH_SHORT).show();
                                        } else updateUserInfoOnServer(userModel.getId(), userModel.getName(), newGoal, userModel.getDesiredWeight(), userModel.getActivityLevel(), userModel.getWeightFactor());
                                    } else updateUserInfoOnServer(userModel.getId(), userModel.getName(), newGoal, userModel.getDesiredWeight(), userModel.getActivityLevel(), 0);
                                    break;
                                case("коэффициент веса"):
                                    Double newWeightFactor = Double.parseDouble(selectedText);
                                    updateUserInfoOnServer(userModel.getId(), userModel.getName(), userModel.getGoal(), userModel.getDesiredWeight(), userModel.getActivityLevel(), newWeightFactor);
                                    break;
                            }
                        }
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // Отображение диалогового окна
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addOrUpdateBMIOnServer(int userId, int height, double currentWeight, String measurementDate) {
        // Создать объект запроса для добавления или обновления BMI
        AddOrUpdateBMIRequest request = new AddOrUpdateBMIRequest(userId, height, currentWeight, measurementDate);

        // Отправить запрос на сервер
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
                            getUserData();
                            setDataUser();
                            Log.i("AccountActivity", "BMI successfully added/updated in local database.");
                        } else {
                            Toast.makeText(AccountActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
                            Log.e("AccountActivity", "Error adding BMI to local database.");
                        }
                    } catch (Exception e) {
                        Toast.makeText(AccountActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
                        Log.e("AccountActivity", "Exception occurred while adding BMI to local database: " + e.getMessage(), e);
                    }
                } else {
                    Toast.makeText(AccountActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
                    Log.e("AccountActivity", "Error adding/updating BMI on server. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AddOrUpdateBMIResponse> call, Throwable t) {
                Log.e("AccountActivity", "Network error: " + t.getMessage(), t);
            }
        });
    }

    private void updateUserInfoOnServer(int userId, String name, String goal, double desiredWeight, String activityLevel, double weightFactor) {
        UpdateUserInfoRequest request = new UpdateUserInfoRequest(userId, name, goal, desiredWeight, activityLevel, weightFactor);

        apiService.updateUserInfo(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Успешное обновление на сервере
                    try {
                        // объект модели пользователя для обновления в локальной базе данных
                        UserModel userModel = new UserModel(userId, name, goal, desiredWeight, activityLevel, weightFactor);

                        if (DbUser.updateUserInfo(db, userModel) > 0) {
                            getUserData();
                            setDataUser();
                            Log.i("AccountActivity", "User information successfully updated in local database.");
                            Toast.makeText(AccountActivity.this, "Информация успешно обновлена", Toast.LENGTH_SHORT).show();

                        } else {
                            Log.e("AccountActivity", "Error updating user information in local database.");
                            Toast.makeText(AccountActivity.this, "Ошибка обновления", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("AccountActivity", "Exception occurred while updating user information in local database: " + e.getMessage(), e);
                    }
                } else {
                    Log.e("AccountActivity", "Error updating user information on server. Response code: " + response.code());
                    Toast.makeText(AccountActivity.this, "Ошибка обновления", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("AccountActivity", "Network error: " + t.getMessage(), t);
            }
        });
    }


    private void updatePasswordOnServer(int userId, String oldPassword, String newPassword) {
        UpdatePasswordRequest request = new UpdatePasswordRequest(userId, oldPassword, newPassword);

        apiService.updatePassword(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Успешно обновлено на сервере
                    try {
                        if (DbUser.updatePassword(db, userId, newPassword)) {
                            Toast.makeText(AccountActivity.this, "Пароль успешно обновлен", Toast.LENGTH_SHORT).show();
                            Log.i("AccountActivity", "Password successfully updated in local database.");
                        } else {
                            Log.e("AccountActivity", "Error updating password in local database.");
                            Toast.makeText(AccountActivity.this, "Ошибка обновления пароля", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("AccountActivity", "Exception occurred while updating password in local database: " + e.getMessage(), e);
                    }
                } else {
                    Toast.makeText(AccountActivity.this, "Ошибка обновления пароля на сервере", Toast.LENGTH_SHORT).show();
                    Log.e("AccountActivity", "Error updating password on server. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("AccountActivity", "Network error: " + t.getMessage(), t);
            }
        });
    }

    private void LogOut() {
        try {
            // Удаление базы данных
            if (deleteDatabase(DbHelper.DATABASE_NAME)) {
                Log.i("AccountActivity", "Database successfully deleted.");
                Intent intent = new Intent(this, Authorization.class);
                startActivity(intent);
            } else {
                Log.e("Logout", "Error deleting database.");
            }
        } catch (Exception e) {
            Log.e("Logout", "Error occurred while deleting database: " + e.getMessage(), e);
        }
    }

    public boolean isPasswordValid (String password1, String password2) {
        if (password1.trim().length() < 6 || password1.trim().length() > 50) {
            Toast.makeText(this, "Не менее " + 6 + " символов и не более " + 50, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password1.equals(password2)) {
            return true;
        } else {
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            Log.w("AccountActivity", "Passwords do not match.");
        }
        return false;
    }

    public boolean checkGoalAchievement(String goal, double currentWeight, double desiredWeight){
        switch (goal) {
            case "WEIGHT_LOSS":
                return currentWeight > desiredWeight;
            case "MAINTENANCE":
                return currentWeight == desiredWeight;
            case "WEIGHT_GAIN":
                return currentWeight < desiredWeight;
        }
        return false;
    }

    @Override
    public void onDataDownloaded(String result) {
        if (result != null) {
            date = result.substring(0, 10);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
