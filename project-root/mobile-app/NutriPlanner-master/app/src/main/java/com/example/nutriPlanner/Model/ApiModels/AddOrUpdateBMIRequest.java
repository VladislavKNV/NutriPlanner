package com.example.nutriPlanner.Model.ApiModels;

public class AddOrUpdateBMIRequest {
    private int userId;
    private int height;
    private double currentWeight;
    private String measurementDate;

    public AddOrUpdateBMIRequest(int userId, int height, double currentWeight, String measurementDate) {
        this.userId = userId;
        this.height = height;
        this.currentWeight = currentWeight;
        this.measurementDate = measurementDate;
    }

}
