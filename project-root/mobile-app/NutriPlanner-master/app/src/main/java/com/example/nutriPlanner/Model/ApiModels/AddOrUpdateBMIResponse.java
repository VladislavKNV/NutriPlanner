package com.example.nutriPlanner.Model.ApiModels;

public class AddOrUpdateBMIResponse {
    private String message;
    private int id;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
