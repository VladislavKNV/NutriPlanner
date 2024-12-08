package com.example.nutriPlanner.Model;

public class UserModel {
    private int id;                     //0
    private int roleId;                 //1
    private String name;                //2
    private String email;               //3
    private String password;            //4
    private String gender;              //5
    private String birthDate;           //6
    private String goal;                //7
    private double desiredWeight;       //8
    private String activityLevel;       //9
    private double weightFactor;        //10
    private String registrationDate;    //11
    private String lastLoginDate;       //12

    public UserModel(int roleId, String name, String email, String password, String gender, String birthDate, String goal, double desiredWeight, String activityLevel, double weightFactor, String registrationDate, String lastLoginDate) {
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
    }

    public UserModel(int id, int roleId, String name, String email, String password, String gender, String birthDate, String goal, double desiredWeight, String activityLevel, double weightFactor, String registrationDate, String lastLoginDate) {
        this.id = id;
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
    }

    public UserModel(int id, String name, String goal, double desiredWeight, String activityLevel, double weightFactor) {
        this.id = id;
        this.name = name;
        this.goal = goal;
        this.desiredWeight = desiredWeight;
        this.activityLevel = activityLevel;
        this.weightFactor = weightFactor;
    }
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public int getRoleId() {
        return roleId;
    }
    public void setRoleId(int roleId) {this.roleId = roleId;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}

    public String getGender() {return gender;}
    public void setGender(String gender) {this.gender = gender;}

    public String getBirthDate() {return birthDate;}
    public void setBirthDate(String birthDate) {this.birthDate = birthDate;}

    public String getGoal() {return goal;}
    public void setGoal(String goal) {this.goal = goal;}

    public double getDesiredWeight() {return desiredWeight;}
    public void setDesiredWeight(double desiredWeight) {this.desiredWeight = desiredWeight;}

    public String getActivityLevel() {return activityLevel;}
    public void setActivityLevel(String activityLevel) {this.activityLevel = activityLevel;}

    public double getWeightFactor() {return weightFactor;}
    public void setWeightFactor(double weightFactor) {this.weightFactor = weightFactor;}

    public String getRegistrationDate() {return registrationDate;}
    public void setRegistrationDate(String registrationDate) {this.registrationDate = registrationDate;}

    public String getLastLoginDate() {return lastLoginDate;}
    public void setLastLoginDate(String lastLoginDate) {this.lastLoginDate = lastLoginDate;}
}


