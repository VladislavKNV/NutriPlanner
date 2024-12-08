package com.example.nutriPlanner.Model;

public class BMIModel {

    private int id;
    private int userId;
    private int height;
    private double currentWeight;
    private String measurementDate;

    public BMIModel(int userId, int height, double currentWeight, String measurementDate) {
        this.id = id;
        this.userId = userId;
        this.height = height;
        this.currentWeight = currentWeight;
        this.measurementDate = measurementDate;
    }

    public BMIModel(int id, int userId, int height, double currentWeight, String measurementDate) {
        this.id = id;
        this.userId = userId;
        this.height = height;
        this.currentWeight = currentWeight;
        this.measurementDate = measurementDate;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public int getUserId() {return userId;}
    public void setUserId(int userId) {this.userId = userId;}

    public int getHeight() {return height;}
    public void setHeight(int height) {this.height = height;}

    public double getCurrentWeight() {return currentWeight;}
    public void setCurrentWeight(double currentWeight) {this.currentWeight = currentWeight;}

    public String getMeasurementDate() {return measurementDate;}
    public void setMeasurementDate(String measurementDate) {this.measurementDate = measurementDate;}
}

