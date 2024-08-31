package com.forecast.weather.model;

public class FutureDayWeatherDataModel {
    String day, temp, condition;
    int conditionView;

    public FutureDayWeatherDataModel(String day, String temp, String condition, int conditionView) {
        this.day = day;
        this.temp = temp;
        this.condition = condition;
        this.conditionView = conditionView;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public int getConditionView() {
        return conditionView;
    }

    public void setConditionView(int conditionView) {
        this.conditionView = conditionView;
    }
}

