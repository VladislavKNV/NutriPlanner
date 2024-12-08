package com.example.nutriPlanner.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.nutriPlanner.DataBase.DbHelper;
import com.example.nutriPlanner.Helpers.ApiService;
import com.example.nutriPlanner.Helpers.NetworkChangeListener;
import com.example.nutriPlanner.Helpers.PasswordEncryptor;
import com.example.nutriPlanner.Helpers.RetrofitInstance;
import com.example.nutriPlanner.Model.ApiModels.CheckEmailRequest;
import com.example.nutriPlanner.Model.ApiModels.CheckEmailResponse;
import com.example.nutriPlanner.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Registration extends AppCompatActivity {

    private TextInputLayout edtRegistrationName1;
    private TextInputEditText edtRegistrationName;
    private TextInputLayout edtRegistrationEmail1;
    private TextInputEditText edtRegistrationEmail;
    private TextInputLayout edtRegistrationPassword11;
    private TextInputEditText edtRegistrationPassword1;
    private TextInputLayout edtRegistrationPassword12;
    private TextInputEditText edtRegistrationPassword2;
    private NetworkChangeListener networkChangeListener;
    private ApiService apiService;
    private SQLiteDatabase db;

    private int MIN_LENGTH_NAME = 3;
    private int MIN_LENGTH_PASSWORD = 6;
    private int MAX_LENGTH = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        bind();

    }

    private void bind() {
        networkChangeListener = new NetworkChangeListener();
        apiService = RetrofitInstance.getRetrofitInstance().create(ApiService.class);
        edtRegistrationName1 = findViewById(R.id.edtRegistrationName1);
        edtRegistrationName = findViewById(R.id.edtRegistrationName);
        edtRegistrationEmail1 = findViewById(R.id.edtRegistrationEmail1);
        edtRegistrationEmail = findViewById(R.id.edtRegistrationEmail);
        edtRegistrationPassword11 = findViewById(R.id.edtRegistrationPassword11);
        edtRegistrationPassword1 = findViewById(R.id.edtRegistrationPassword1);
        edtRegistrationPassword12 = findViewById(R.id.edtRegistrationPassword12);
        edtRegistrationPassword2 = findViewById(R.id.edtRegistrationPassword2);
        db = new DbHelper(getApplicationContext()).getReadableDatabase();
    }

    public void Reg(View view) {
        String name = edtRegistrationName.getText().toString();
        String email = edtRegistrationEmail.getText().toString();
        String password1 = edtRegistrationPassword1.getText().toString();
        String password2 = edtRegistrationPassword2.getText().toString();

        if (isValid(name, email, password1, password2)) {
            try {
                CheckEmailRequest request = new CheckEmailRequest(email);
                Call<CheckEmailResponse> call = apiService.checkEmail(request);

                call.enqueue(new Callback<CheckEmailResponse>() {
                    @Override
                    public void onResponse(Call<CheckEmailResponse> call, Response<CheckEmailResponse> response) {
                        if (response.isSuccessful()) {
                            CheckEmailResponse checkEmailResponse = response.body();
                            if (checkEmailResponse != null) {
                                PasswordEncryptor passwordHashes = new PasswordEncryptor();
                                Intent intent = new Intent(getApplicationContext(), SurveyActivity.class);
                                intent.putExtra("idUser", -1);
                                intent.putExtra("isReg", true);
                                intent.putExtra("name", name);
                                intent.putExtra("email", email);
                                intent.putExtra("password", passwordHashes.encryptPassword(password1));
                                startActivity(intent);
                            } else {
                                Log.e("Registration", "CheckEmailResponse is null");
                            }
                        } else {
                            edtRegistrationEmail1.setError("Данный email уже занят");
                        }
                    }

                    @Override
                    public void onFailure(Call<CheckEmailResponse> call, Throwable t) {
                        Log.e("Registration", "Network error", t);
                    }
                });

            } catch (Exception e) {
                Log.e("Registration", "Error during registration", e);
                Toast.makeText(this, "Проверьте введенные данные", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Некоторые поля заполнены неверно", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean isPasswordValid (String password1, String password2) {
        if (password1.trim().length() < MIN_LENGTH_PASSWORD || password1.trim().length() > MAX_LENGTH) {
            Toast.makeText(this, "Не менее " + MIN_LENGTH_PASSWORD + " символов и не более " + MAX_LENGTH, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password1.equals(password2)) {
            return true;
        } else {
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private boolean isNameValid (String name) {
        if (name.trim().length() < MIN_LENGTH_NAME || name.trim().length() > MAX_LENGTH) {
            edtRegistrationName1.setError("Не менее " + MIN_LENGTH_NAME + " символов и не более " + MAX_LENGTH);
            return false;
        }
        return true;
    }

    private boolean isEmailValid(String email) {
        // регулярное выражение для проверки email
        String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValid(String name, String email, String password1, String password2){
        Boolean valid = true;
        if (!isNameValid(name)){valid = false;}
        if (!isEmailValid(email)){
            edtRegistrationEmail1.setError("Некорректный email");
            valid = false;}
        if (!isPasswordValid(password1, password2)){valid = false;}
        return valid;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, Authorization.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}