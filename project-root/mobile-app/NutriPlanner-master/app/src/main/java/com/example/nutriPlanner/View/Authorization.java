package com.example.nutriPlanner.View;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutriPlanner.DataBase.DbBMI;
import com.example.nutriPlanner.DataBase.DbFavoriteFood;
import com.example.nutriPlanner.DataBase.DbFoodIntake;
import com.example.nutriPlanner.DataBase.DbFoods;
import com.example.nutriPlanner.DataBase.DbHelper;
import com.example.nutriPlanner.DataBase.DbUser;
import com.example.nutriPlanner.Helpers.ApiService;
import com.example.nutriPlanner.Helpers.GetDateData;
import com.example.nutriPlanner.Helpers.NetworkChangeListener;
import com.example.nutriPlanner.Helpers.PasswordEncryptor;
import com.example.nutriPlanner.Helpers.RetrofitInstance;
import com.example.nutriPlanner.Model.ApiModels.CheckEmailRequest;
import com.example.nutriPlanner.Model.ApiModels.CheckEmailResponse;
import com.example.nutriPlanner.Model.ApiModels.ForgotPasswordRequest;
import com.example.nutriPlanner.Model.ApiModels.LoginRequest;
import com.example.nutriPlanner.Model.ApiModels.LoginResponse;
import com.example.nutriPlanner.Model.ApiModels.UpdateLastLoginRequest;
import com.example.nutriPlanner.Model.BMIModel;
import com.example.nutriPlanner.Model.FavoriteFoodModel;
import com.example.nutriPlanner.Model.FoodIntakeModel;
import com.example.nutriPlanner.Model.FoodModel;
import com.example.nutriPlanner.Model.UserModel;
import com.example.nutriPlanner.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Authorization extends AppCompatActivity implements GetDateData.DownloadDataListener {

    private NetworkChangeListener networkChangeListener;
    private TextInputEditText edtAuthorizationEmail;
    private TextInputLayout edtAuthorizationEmail1;
    private TextInputEditText edtAuthorizationPassword;
    private TextInputLayout edtAuthorizationPassword1;
    private TextView tvForgotPassword;
    private ApiService apiService;
    private String date;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);
        getDate();
        bind();

        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        apiService = RetrofitInstance.getRetrofitInstance().create(ApiService.class);
        View.OnClickListener forgotPasswordClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtAuthorizationEmail.getText().toString();
                forgotPassword(email);
            }
        };
        tvForgotPassword.setOnClickListener(forgotPasswordClickListener);
    }

    private void getDate() {
        GetDateData downloadDataTask = new GetDateData();
        downloadDataTask.execute();
        downloadDataTask.setDownloadDataListener(this);
    }

    private void bind() {
        networkChangeListener = new NetworkChangeListener();
        edtAuthorizationEmail1 = findViewById(R.id.edtAuthorizationEmail1);
        edtAuthorizationEmail = findViewById(R.id.edtAuthorizationEmail);
        edtAuthorizationPassword = findViewById(R.id.edtAuthorizationPassword);
        edtAuthorizationPassword1 = findViewById(R.id.edtAuthorizationPassword1);
        db = new DbHelper(getApplicationContext()).getReadableDatabase();
    }

    public void GoToRegister(View view) {
        Intent intent = new Intent(this, Registration.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void Login(View view) {
        String email = edtAuthorizationEmail.getText().toString().trim();
        String password = edtAuthorizationPassword.getText().toString().trim();

        if (!isValid(email, password)) {
            return;
        }

        PasswordEncryptor passwordHashes = new PasswordEncryptor();
        loginDb(email, passwordHashes.encryptPassword(password));
    }

    private boolean isValid(String email, String password)
    {
        boolean valid = true;
        if (TextUtils.isEmpty(email)) {
            Log.w("Validation", "Email is empty.");
            edtAuthorizationEmail1.setError("Введите электронную почту");
            valid = false;
        }

        // Проверка на корректный формат электронной почты
        if (!isEmailValid(email)) {
            Log.w("Validation", "Invalid email format: " + email);
            edtAuthorizationEmail1.setError("Введите корректную электронную почту");
            valid = false;
        }

        if (TextUtils.isEmpty(password)) {
            Log.w("Validation", "Password is empty.");
            Toast.makeText(Authorization.this, "Введите пароль", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }

    private boolean isEmailValid(String email) {
        // регулярное выражение для проверки email
        String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void loginDb(String email, String password) {
        LoginRequest loginRequest = new LoginRequest(email, password);
        apiService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    saveUserInfo(loginResponse);

                    Log.i("Authorization", "Login successful for email: " + email);
                    Intent intent = new Intent(Authorization.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(Authorization.this, "Неверные данные", Toast.LENGTH_SHORT).show();
                    Log.w("Authorization", "Invalid login data. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("Authorization", "Network error: " + t.getMessage());
            }
        });
    }

    private void saveUserInfo(LoginResponse loginResponse) {
        UserModel userModel = loginResponse.getUser();
        List<BMIModel> bmiList = loginResponse.getBmi();
        List<FavoriteFoodModel> favoriteFoodList = loginResponse.getFavorite_food();
        List<FoodIntakeModel> foodIntakeList = loginResponse.getFood_intake();
        List<FoodModel> foodList = loginResponse.getFoods();

        try {
            if (DbUser.addAll(db, userModel) != -1) {
                Log.i("Authorization", "User info added successfully for user ID: " + userModel.getId());

                for (FoodModel foodModel : foodList) {
                    if (DbFoods.add(db, foodModel) == -1) {
                        Log.e("Authorization", "Error adding food: " + foodModel.getFoodName());
                        break;
                    }
                }
                for (BMIModel bmiModel : bmiList) {
                    if (DbBMI.add(db, bmiModel) == -1) {
                        Log.e("Authorization", "Error adding BMI record.");
                        break;
                    }
                }
                for (FavoriteFoodModel favoriteFoodModel : favoriteFoodList) {
                    if (DbFavoriteFood.add(db, favoriteFoodModel) == -1) {
                        Log.e("Authorization", "Error adding favorite food: " + favoriteFoodModel.getFoodId());
                        break;
                    }
                }
                for (FoodIntakeModel foodIntakeModel : foodIntakeList) {
                    if (DbFoodIntake.add(db, foodIntakeModel) == -1) {
                        Log.e("Authorization", "Error adding food intake record.");
                        break;
                    }
                }
                updateLastLoginOnServer(userModel.getId(), date);
            } else {
                Log.e("Authorization", "Error adding user info.");
            }
        } catch (Exception e) {
            Log.e("Authorization", "Data addition error: " + e.getMessage());
        }
    }

    private void forgotPassword(String email) {
        CheckEmailRequest request = new CheckEmailRequest(email);
        Call<CheckEmailResponse> call = apiService.checkEmail(request);

        call.enqueue(new Callback<CheckEmailResponse>() {
            @Override
            public void onResponse(Call<CheckEmailResponse> call, Response<CheckEmailResponse> response) {
                if (response.isSuccessful()) {
                    CheckEmailResponse checkEmailResponse = response.body();
                    if (checkEmailResponse != null) {
                        Log.w("ForgotPassword", "Invalid email: " + email);
                    }
                } else {
                    ForgotPasswordRequest request = new ForgotPasswordRequest(email);
                    apiService.forgotPassword(request).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Log.i("ForgotPassword", "Password reset email sent successfully to: " + email);
                                Toast.makeText(Authorization.this, "Новый пароль отправлен на почту", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e("ForgotPassword", "Failed to send password reset email. Response code: " + response.code());
                                Toast.makeText(Authorization.this, "Не удалось отправить электронное письмо для сброса пароля", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.e("ForgotPassword", "Network error while sending password reset email for: " + email, t);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<CheckEmailResponse> call, Throwable t) {
                Log.e("Authorization", "Network error while checking email: " + email, t);
            }
        });
    }


    private void updateLastLoginOnServer(int userId, String date) {
        apiService.updateLastLogin(new UpdateLastLoginRequest(userId, date)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.i("Authorization", "Last login date successfully updated on server for user ID: " + userId);
                    updateLastLoginInLocalDatabase(userId, date);
                } else {
                    Log.w("Authorization", "Failed to update last login date on server. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Authorization", "Network error while updating last login date on server. Error: " + t.getMessage(), t);
            }
        });
    }

    private void updateLastLoginInLocalDatabase(int userId, String date) {
        try {
            // Обновление последней даты входа в локальной базе
            if (DbUser.updateLastLoginDate(db, userId, date) > 0) {
                Log.i("Authorization", "Last login date updated for user ID: " + userId);
                Intent intent = new Intent(Authorization.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Log.w("Authorization", "Update failed: No records updated for user ID: " + userId);
            }
        } catch (Exception e) {
            Log.e("Authorization", "Error updating last login date for user ID: " + userId, e);
        }
    }

    @Override
    public void onDataDownloaded(String result) {
        if (result != null) {
            date = result;
            int userId = DbUser.getUserId(db);
            Log.i("Authorization", "Data downloaded successfully. User ID: " + userId + ", Date: " + date);

            try {
                // Обновление последней даты входа на сервере
                updateLastLoginOnServer(userId, date);
            } catch (Exception e) {
                Log.e("Authorization", "Error updating last login date on server for user ID: " + userId, e);
            }
        } else {
            Log.w("Authorization", "Downloaded data is null. Unable to update last login date.");
        }
    }

}