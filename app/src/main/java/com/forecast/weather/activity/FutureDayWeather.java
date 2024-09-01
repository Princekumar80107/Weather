package com.forecast.weather.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.forecast.weather.R;
import com.forecast.weather.adapter.FutureDayWeatherAdapter;
import com.forecast.weather.model.FutureDayWeatherDataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class FutureDayWeather extends AppCompatActivity {
    private RecyclerView next5DayWeather;
    private List<String> daysList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future_day_weather);

        next5DayWeather = findViewById(R.id.next5DaysDataView);
        Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();

        daysList = generateDaysList();
        String cityName = getIntent().getStringExtra("cityName");

        if (cityName != null) {
            fetchWeatherData(cityName);
        } else {
            Toast.makeText(this, "City name is missing", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("SimpleDateFormat")
    private List<String> generateDaysList() {
        List<String> days = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Calendar calendar = new GregorianCalendar();

        for (int i = 0; i < 6; i++) {
            calendar.add(Calendar.DATE, i);
            days.add(sdf.format(calendar.getTime()));
        }
        return days;
    }

    private void fetchWeatherData(String city) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + city + "&appid=9d19bf6e7b55e97eff19cb5b9b278751";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                this::parseWeatherData,
                error -> Toast.makeText(FutureDayWeather.this, "Failed to load data", Toast.LENGTH_SHORT).show()
        );

        queue.add(jsonObjectRequest);
    }

    private void parseWeatherData(JSONObject response) {
        try {
            JSONArray list = response.getJSONArray("list");
            ArrayList<FutureDayWeatherDataModel> weatherDataList = new ArrayList<>();
            int dayIndex = 1;

            for (int i = 0; i < list.length(); i++) {
                JSONObject weatherObject = list.getJSONObject(i);
                String time = weatherObject.getString("dt_txt").substring(11);

                if ("09:00:00".equals(time)) {
                    FutureDayWeatherDataModel model = createWeatherModel(weatherObject, dayIndex);
                    if (model != null) {
                        weatherDataList.add(model);
                        dayIndex++;
                    }
                }
            }

            updateUI(weatherDataList);

        } catch (JSONException e) {
            Toast.makeText(this, "Failed to parse data", Toast.LENGTH_SHORT).show();
        }
    }

    private FutureDayWeatherDataModel createWeatherModel(JSONObject weatherObject, int dayIndex) throws JSONException {
        JSONObject main = weatherObject.getJSONObject("main");
        JSONArray weatherArray = weatherObject.getJSONArray("weather");

        double temp = main.getDouble("temp") - 273.15; // Kelvin to Celsius
        String condition = weatherArray.getJSONObject(0).getString("main");

        int animationResId = getAnimationResource(condition);
        if (animationResId == -1) {
            return null;
        }

        return new FutureDayWeatherDataModel(daysList.get(dayIndex), String.format("%.1f°C", temp), condition, animationResId);
    }

    private int getAnimationResource(String condition) {
        switch (condition) {
            case "Clear":
            case "Sunny":
                return R.raw.sunny;
            case "Clouds":
            case "Overcast clouds":
                return R.raw.cloudy_day;
            case "Mist":
            case "Fog":
            case "Haze":
            case "Smoke":
                return R.raw.fogg_mist;
            case "Rain":
            case "Drizzle":
            case "Showers":
                return R.raw.rain;
            case "Snow":
                return R.raw.snow_fall;
            default:
                return R.raw.sunny;
        }
    }

    private void updateUI(ArrayList<FutureDayWeatherDataModel> weatherDataList) {
        FutureDayWeatherAdapter adapter = new FutureDayWeatherAdapter(this, weatherDataList);
        next5DayWeather.setLayoutManager(new GridLayoutManager(this, 1));
        next5DayWeather.setAdapter(adapter);
    }
}