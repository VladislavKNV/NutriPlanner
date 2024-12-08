package com.example.nutriPlanner.Helpers;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.nutriPlanner.DataBase.DbBMI;
import com.example.nutriPlanner.DataBase.DbHelper;
import com.example.nutriPlanner.Model.ApiModels.AddOrUpdateBMIRequest;
import com.example.nutriPlanner.Model.ApiModels.AddOrUpdateBMIResponse;
import com.example.nutriPlanner.Model.BMIModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BMIUpdater {

    private final SQLiteDatabase db;
    private String date;
    private BMIModel bmiModel;
    private final ApiService apiService;

    public BMIUpdater(Context context) {
        getDate();
        this.apiService = RetrofitInstance.getRetrofitInstance().create(ApiService.class);
        this.db = new DbHelper(context).getReadableDatabase();
        bmiModel = DbBMI.getLastBMI(db);
    }
    private void getDate() {
        GetDateData downloadDataTask = new GetDateData();
        downloadDataTask.execute();
        GetDateData.DownloadDataListener listener = new GetDateData.DownloadDataListener() {
            @Override
            public void onDataDownloaded(String result) {
                if (result != null) {
                    date = result.substring(0, 10);
                }
            }
        };
        downloadDataTask.setDownloadDataListener(listener);
    }
    public void addOrUpdateBMIOnServer(int height, double currentWeight, Context context, Runnable onSuccess, Runnable onFailure) {
        AddOrUpdateBMIRequest request = new AddOrUpdateBMIRequest(bmiModel.getUserId(), height, currentWeight, date);
        apiService.addOrUpdateBMI(request).enqueue(new Callback<AddOrUpdateBMIResponse>() {
            @Override
            public void onResponse(Call<AddOrUpdateBMIResponse> call, Response<AddOrUpdateBMIResponse> response) {
                if (response.isSuccessful()) {
                    AddOrUpdateBMIResponse bmiResponse = response.body();
                    int bmiRecordId = bmiResponse.getId();

                    try {
                        BMIModel bmiModel1 = new BMIModel(bmiRecordId, bmiModel.getUserId(), height, currentWeight, date);
                        if (DbBMI.addOrUpdate(db, bmiModel1) != -1) {
                            Log.i("AccountActivity", "BMI successfully added/updated in local database.");
                            onSuccess.run(); // Notify caller of success
                        } else {
                            onFailure.run();
                            Log.e("BMIUpdate", "Error adding BMI to local database.");
                        }
                    } catch (Exception e) {
                        Log.e("BMIUpdate", "Exception while adding BMI to local database: " + e.getMessage(), e);
                        onFailure.run();
                    }
                } else {
                    onFailure.run();
                    Log.e("BMIUpdate", "Error adding/updating BMI on server. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AddOrUpdateBMIResponse> call, Throwable t) {
                Log.e("BMIUpdate", "Network error: " + t.getMessage(), t);
                onFailure.run();
            }
        });
    }

    public List<Double> getListBmiWeight() {
        Map<String, Double> bmiRecords = DbBMI.getAllBmiRecords(db);
        List<Double> weights = new ArrayList<>();

        for (Map.Entry<String, Double> entry : bmiRecords.entrySet()) {
            Double weight = entry.getValue();
            weights.add(weight);
        }
        return weights;
    }

    public List<String> getListBmiDate() {
        Map<String, Double> bmiRecords = DbBMI.getAllBmiRecords(db);
        List<String> dates = new ArrayList<>();

        // Обработка записей BMI
        for (Map.Entry<String, Double> entry : bmiRecords.entrySet()) {
            String date = entry.getKey();

            // Обрезка даты
            String formattedDate = date.substring(8, 10) + "-" + date.substring(5, 7);
            dates.add(formattedDate);
        }
        return dates;
    }

    public int getLastgetHeight(){
        BMIModel bmiModel1 = DbBMI.getLastBMI(db);
        return bmiModel1.getHeight();
    }
}
