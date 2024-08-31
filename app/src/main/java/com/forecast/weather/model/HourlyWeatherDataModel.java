package com.forecast.weather.model;

public class HourlyWeatherDataModel {
    int conditionView;
    String temp, time;

    public HourlyWeatherDataModel(int conditionView, String temp, String time) {
        this.conditionView = conditionView;
        this.temp = temp;
        this.time = time;
    }

    public int getConditionView() {
        return conditionView;
    }

    public void setConditionView(int conditionView) {
        this.conditionView = conditionView;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

