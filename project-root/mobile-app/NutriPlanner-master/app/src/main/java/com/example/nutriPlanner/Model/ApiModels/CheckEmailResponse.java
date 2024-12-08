package com.example.nutriPlanner.Model.ApiModels;

public class CheckEmailResponse {
    private String message;

    public CheckEmailResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
