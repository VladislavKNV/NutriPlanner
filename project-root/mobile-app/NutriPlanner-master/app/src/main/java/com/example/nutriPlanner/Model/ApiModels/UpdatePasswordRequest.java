package com.example.nutriPlanner.Model.ApiModels;

public class UpdatePasswordRequest {
    private int idUser;
    private String oldPassword;
    private String newPassword;

    public UpdatePasswordRequest(int idUser, String oldPassword, String newPassword) {
        this.idUser = idUser;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    // Getters and Setters
    public int getIduser() {
        return idUser;
    }

    public void setIduser(int iduser) {
        this.idUser = iduser;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
