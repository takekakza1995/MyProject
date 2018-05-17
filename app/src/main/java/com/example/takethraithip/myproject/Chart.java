package com.example.takethraithip.myproject;

import java.util.ArrayList;

public class Chart {
    float water;
    float light;
    float behavior;

    public Chart(float water,float light, float behavior){
        this.water = water;
        this.light = light;
        this.behavior = behavior;
    }

    public float getWater() {
        return water;
    }

    public void setWater(float water) {
        this.water = water;
    }

    public float getLight() {
        return light;
    }

    public void setLight(float light) {
        this.light = light;
    }

    public float getBehavior() {
        return behavior;
    }

    public void setBehavior(float behavior) {
        this.behavior = behavior;
    }

    public static ArrayList<Chart> getSampleChartData(int size) {
        ArrayList<Chart> charts = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            charts.add(new Chart((float) Math.random() * 100,(float) Math.random() * 100,(float) Math.random() * 100));
        }
        return charts;
    }
}
