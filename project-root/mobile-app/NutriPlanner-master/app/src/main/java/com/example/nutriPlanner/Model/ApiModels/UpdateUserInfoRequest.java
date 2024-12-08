package com.example.nutriPlanner.Model.ApiModels;

public class UpdateUserInfoRequest {
    private int idUser;
    private String name;
    private String goal;
    private double desiredWeight;
    private String activityLevel;
    private double weightFactor;

    public UpdateUserInfoRequest(int idUser, String name, String goal, double desiredWeight, String activityLevel, double weightFactor) {
        this.idUser = idUser;
        this.name = name;
        this.goal = goal;
        this.desiredWeight = desiredWeight;
        this.activityLevel = activityLevel;
        this.weightFactor = weightFactor;
    }

}