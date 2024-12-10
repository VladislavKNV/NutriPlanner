package com.example.nutriPlanner.Model;

public class WaterModel {
    private int id;
    private String date;
    private float count;

    public WaterModel(String date, float count) {
        this.date = date;
        this.count = count;
    }

    public WaterModel(int id, String date, float count) {
        this.id = id;
        this.date = date;
        this.count = count;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public float getCount() { return count; }
    public void setCount(float count) { this.count = count; }
}

