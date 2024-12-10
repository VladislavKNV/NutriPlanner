package com.example.nutriPlanner.Model.ApiModels;

public class RegisterRequest {
    private int roleId;
    private String name;
    private String email;
    private String password;
    private String gender;
    private String birthDate;
    private String goal;
    private double desiredWeight;
    private String activityLevel;
    private double weightFactor;
    private String registrationDate;
    private String lastLoginDate;
    private int height;
    private double currentWeight;
    private String measurementDate;

    public RegisterRequest(int roleId, String name, String email, String password, String gender, String birthDate,
                           String goal, double desiredWeight, String activityLevel, double weightFactor,
                           String registrationDate, String lastLoginDate, int height, double currentWeight,
                           String measurementDate) {
        this.roleId = roleId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.birthDate = birthDate;
        this.goal = goal;
        this.desiredWeight = desiredWeight;
        this.activityLevel = activityLevel;
        this.weightFactor = weightFactor;
        this.registrationDate = registrationDate;
        this.lastLoginDate = lastLoginDate;
        this.height = height;
        this.currentWeight = currentWeight;
        this.measurementDate = measurementDate;
    }
}