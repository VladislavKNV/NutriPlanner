package com.example.nutriPlanner.Model.ApiModels;

public class CheckEmailRequest {
    private String email;

    public CheckEmailRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}