package com.example.nutriPlanner.Model.ApiModels;

public class UpdateLastLoginRequest {
    private int idUser;
    private String lastLoginDate;

    public UpdateLastLoginRequest(int idUser, String lastLoginDate) {
        this.idUser = idUser;
        this.lastLoginDate = lastLoginDate;
    }

}