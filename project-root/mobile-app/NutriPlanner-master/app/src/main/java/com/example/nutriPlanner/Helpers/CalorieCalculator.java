package com.example.nutriPlanner.Helpers;

public class CalorieCalculator {
    // уравнения Харриса-Бенедикта:
    // Коэффициенты для уровней активности
    private static final double SEDENTARY_ACTIVITY = 1.2;
    private static final double LIGHT_ACTIVITY = 1.375;
    private static final double MODERATE_ACTIVITY = 1.55;
    private static final double ACTIVE_ACTIVITY = 1.725;
    private static final double VERY_ACTIVE_ACTIVITY = 1.9;


    public static int calculateCalories(int age, String gender, double height, double weight,String goal, String activityLevel, double weightFactor) {
        double bmr;
        if (gender == "Man") {
            bmr = 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age);
        } else {
            bmr = 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age);
        }

        if (goal == "WEIGHT_LOSS") {
            bmr -= weightFactor * 100;
        } else if (goal == "WEIGHT_GAIN") {
            bmr += weightFactor * 100;
        }

        bmr *= getActivityMultiplier(activityLevel);

        return (int) Math.round(bmr);
    }

    public static int[] distributeCalories(int totalCalories) {

        //процентное соотношение калорий для каждого приема пищи
        double breakfastPercentage = 0.25;
        double lunchPercentage = 0.35;
        double dinnerPercentage = 0.30;
        double snacksPercentage = 0.10;

        //pаспред калории на приемы пищи в соответствии с процентным соотношением
        int[] caloriesDistribution = new int[4];
        caloriesDistribution[0] = (int) Math.round(totalCalories * breakfastPercentage);
        caloriesDistribution[1] = (int) Math.round(totalCalories * lunchPercentage);
        caloriesDistribution[2] = (int) Math.round(totalCalories * dinnerPercentage);
        caloriesDistribution[3] = (int) Math.round(totalCalories * snacksPercentage);

        return caloriesDistribution;
    }

    public static int[] calculateNutrients(double totalCalories, String goal, String activityLevel) {

        // oпределение соотношение белков, углеводов и жиров в общем рационе питания
        double proteinPercentage = 0.20;
        double carbsPercentage = 0.50;
        double fatsPercentage = 0.30;

        // В зависимости от цели, корректирование пропорций
        switch (goal) {
            case "WEIGHT_LOSS":
                proteinPercentage += 0.05;
                carbsPercentage -= 0.05;
                break;
            case "WEIGHT_GAIN":
                proteinPercentage -= 0.05;
                carbsPercentage += 0.05;
                break;
            case "MAINTENANCE":
            default:
                break;
        }

        // В зависимости от уровня активности, корректировка пропорций
        switch (activityLevel) {
            case "SEDENTARY":
                proteinPercentage -= 0.10;
                carbsPercentage += 0.10;
                break;
            case "LIGHT":
                proteinPercentage -= 0.05;
                carbsPercentage += 0.05;
                break;
            case "ACTIVE":
                proteinPercentage += 0.05;
                carbsPercentage -= 0.05;
                break;
            case "VERY_ACTIVE":
                proteinPercentage += 0.10;
                carbsPercentage -= 0.10;
                break;
            case "MODERATE":
            default:
                break;
        }

        // расчет количества белков, углеводов и жиров в граммах на день
        int[] nutrients = new int[3];
        nutrients[0] = (int) Math.round(totalCalories * proteinPercentage / 4); // 1 г белка = 4 ккал
        nutrients[1] = (int) Math.round(totalCalories * carbsPercentage / 4); // 1 г углеводов = 4 ккал
        nutrients[2] = (int) Math.round(totalCalories * fatsPercentage / 9); // 1 г жиров = 9 ккал

        return nutrients;
    }

    private static double getActivityMultiplier(String activityLevel) {
        switch (activityLevel) {
            case "SEDENTARY":
                return SEDENTARY_ACTIVITY;
            case "LIGHT":
                return LIGHT_ACTIVITY;
            case "MODERATE":
                return MODERATE_ACTIVITY;
            case "ACTIVE":
                return ACTIVE_ACTIVITY;
            case "VERY_ACTIVE":
                return VERY_ACTIVE_ACTIVITY;
            default:
                return MODERATE_ACTIVITY;
        }
    }
}
