package com.example.nutriPlanner.View;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutriPlanner.DataBase.DbHelper;
import com.example.nutriPlanner.DataBase.DbUser;
import com.example.nutriPlanner.Helpers.ApiService;
import com.example.nutriPlanner.Helpers.GetDateData;
import com.example.nutriPlanner.Helpers.NetworkChangeListener;
import com.example.nutriPlanner.Helpers.RetrofitInstance;
import com.example.nutriPlanner.Model.ApiModels.UpdateLastLoginRequest;
import com.example.nutriPlanner.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppStart extends AppCompatActivity implements GetDateData.DownloadDataListener {
    private SQLiteDatabase db;
    private NetworkChangeListener networkChangeListener;
    private String date;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_start);
        apiService = RetrofitInstance.getRetrofitInstance().create(ApiService.class);

        networkChangeListener = new NetworkChangeListener();
        db = new DbHelper(getApplicationContext()).getReadableDatabase();

        if (DbUser.getUsersCount(db) > 0) {
                getDate();
        } else {
            Intent intent = new Intent(this, Authorization.class);
            startActivity(intent);
            finish();
        }
    }

    private void getDate() {
        GetDateData downloadDataTask = new GetDateData();
        downloadDataTask.execute();
        downloadDataTask.setDownloadDataListener(this);
    }

    private void updateLastLoginOnServer(int userId, String date) {
        apiService.updateLastLogin(new UpdateLastLoginRequest(userId, date)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.i("AppStart", "Last login date successfully updated on server for user ID: " + userId);
                    updateLastLoginInLocalDatabase(userId, date);
                } else {
                    Log.w("AppStart", "Failed to update last login date on server. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("AppStart", "Network error while updating last login date on server. Error: " + t.getMessage(), t);
            }
        });
    }


    private void updateLastLoginInLocalDatabase(int userId, String date) {
        try {
            // Обновление последней даты входа в локальной базе
            if (DbUser.updateLastLoginDate(db, userId, date) > 0) {
                Log.i("AppStart", "Last login date updated for user ID: " + userId);
                Intent intent = new Intent(AppStart.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Log.w("AppStart", "Update failed: No records updated for user ID: " + userId);
            }
        } catch (Exception e) {
            Log.e("AppStart", "Error updating last login date for user ID: " + userId, e);
        }
    }

    @Override
    public void onDataDownloaded(String result) {
        if (result != null) {
            date = result;
            int userId = DbUser.getUserId(db);
            Log.d("AppStart", "Data downloaded successfully for user ID: " + userId);

            try {
                // Обновление последней даты входа на сервере
                updateLastLoginOnServer(userId, date);
                Log.i("AppStart", "Last login date updated successfully for user ID: " + userId);
            } catch (Exception e) {
                Log.e("AppStart", "Failed to update last login date on the server. Error: " + e.getMessage(), e);
            }
        } else {
            Log.w("AppStart", "Downloaded data is null. Unable to update last login date.");
        }
    }


}
