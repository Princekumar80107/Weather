package com.forecast.weather.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.forecast.weather.R;
import com.forecast.weather.adapter.HourlyWeatherAdapter;
import com.forecast.weather.model.HourlyWeatherDataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView cityName, date, temperature, condition, humidity, windSpeed, visibility, next5Days, conditionDescription;
    private RecyclerView hourlyView;
    private LottieAnimationView lottieAnimationView;
    private SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupWeatherData("Chandigarh");
        setupSearchFunctionality();
        setupSwipeRefresh();
        setupNext5DaysClickListener();
    }

    private void initializeViews() {
        cityName = findViewById(R.id.cityName);
        date = findViewById(R.id.date);
        lottieAnimationView = findViewById(R.id.lottieAnimationView);
        temperature = findViewById(R.id.temperature);
        condition = findViewById(R.id.condition);
        humidity = findViewById(R.id.humidity);
        windSpeed = findViewById(R.id.wind);
        visibility = findViewById(R.id.visibility);
        hourlyView = findViewById(R.id.hourlyView);
        next5Days = findViewById(R.id.next_5_days);
        searchView = findViewById(R.id.searchView);
        conditionDescription = findViewById(R.id.conditionDescription);
        swipeRefreshLayout = findViewById(R.id.refreshLayout);
    }

    private void setupNext5DaysClickListener() {
        next5Days.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), FutureDayWeather.class);
            intent.putExtra("cityName", cityName.getText().toString());
            startActivity(intent);
        });
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            setupWeatherData(cityName.getText().toString());
            Toast.makeText(MainActivity.this, "Refreshing...", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void setupSearchFunctionality() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setupWeatherData(query);
                searchView.setQuery("", false);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void setupWeatherData(String city) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + city + "&appid=9d19bf6e7b55e97eff19cb5b9b278751";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                this::parseWeatherData,
                error -> Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show());

        queue.add(jsonObjectRequest);
    }

    @SuppressLint("SetTextI18n")
    private void parseWeatherData(JSONObject response) {
        try {
            JSONObject city = response.getJSONObject("city");
            JSONArray list = response.getJSONArray("list");

            cityName.setText(city.getString("name"));
            date.setText(list.getJSONObject(0).getString("dt_txt").substring(0, 10));

            ArrayList<HourlyWeatherDataModel> hourlyData = new ArrayList<>();
            for (int i = 0; i < 10; i++) { // First 10 3-hourly forecasts
                JSONObject weatherData = list.getJSONObject(i);
                populateHourlyWeather(hourlyData, weatherData, i == 0);
            }

            setupHourlyWeatherAdapter(hourlyData);
        } catch (JSONException e) {
            Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }
    }

    private void populateHourlyWeather(ArrayList<HourlyWeatherDataModel> hourlyData, JSONObject weatherData, boolean isCurrent) throws JSONException {
        JSONObject main = weatherData.getJSONObject("main");
        JSONArray weatherArray = weatherData.getJSONArray("weather");
        double temp = main.getDouble("temp") - 273.15; // Kelvin to Celsius
        String conditionView = weatherArray.getJSONObject(0).getString("main");

        if (isCurrent) {
            updateCurrentWeather(main, weatherData, weatherArray, temp, conditionView);
        }

        String time = weatherData.getString("dt_txt").substring(11, 16);
        int animationResource = getAnimationResource(conditionView);
        hourlyData.add(new HourlyWeatherDataModel(animationResource, String.format("%.1f°C", temp), time));
    }

    private void updateCurrentWeather(JSONObject main, JSONObject weatherData, JSONArray weatherArray, double temp, String conditionView) throws JSONException {
        temperature.setText(String.format("%.1f°C", temp));
        condition.setText(conditionView);
        conditionDescription.setText(weatherArray.getJSONObject(0).getString("description"));
        humidity.setText(main.getString("humidity") + "%");
        windSpeed.setText(weatherData.getJSONObject("wind").getString("speed") + "Km/hr");
        visibility.setText(weatherData.getInt("visibility") / 1000 + "Km");
        updateLottieAnimation(conditionView);
    }

    private int getAnimationResource(String conditionView) {
        switch (conditionView) {
            case "Clear":
                return R.raw.sunny;
            case "Clouds":
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

    private void updateLottieAnimation(String conditionView) {
        lottieAnimationView.setAnimation(getAnimationResource(conditionView));
        lottieAnimationView.playAnimation();
    }

    private void setupHourlyWeatherAdapter(ArrayList<HourlyWeatherDataModel> hourlyData) {
        hourlyView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        HourlyWeatherAdapter adapter = new HourlyWeatherAdapter(this, hourlyData);
        hourlyView.setAdapter(adapter);
    }
}