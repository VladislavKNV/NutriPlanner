package com.example.nutriPlanner.View.Survey;

public interface OnSurveyInteractionListener {
    void onGoalSelected(String selectedGoal);
    void onGenderSelected(String selectedGender);
    void onDateSelected(int day, int month, int year);
    void onHeightSelected(int height);
    void onWeightSelected(double currentWeight, double desiredWeight);
    void onALevelSelected(String ALevel);
    void onWFactorSelected(double weightChangePerWeek);
}