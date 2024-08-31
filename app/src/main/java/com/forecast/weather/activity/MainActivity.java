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

    TextView cityName, date, temperature, condition, humidity, windSpeed, visibility, next5Days, conditionDescription;
    RecyclerView hourlyView;
    LottieAnimationView lottieAnimationView;
    SearchView searchView;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findId(); // finding the id of all view of the xml file

        addWeatherData("Chandigarh");

        SearchCity();

        next5Days.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FutureDayWeather.class);
                intent.putExtra("cityName", cityName.getText().toString());
                startActivity(intent);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                addWeatherData("Chandigarh");
                Toast.makeText(MainActivity.this, "Refreshing...", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void SearchCity() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                addWeatherData(query);

                searchView.setQuery("", false);

                Toast.makeText(MainActivity.this, "Refreshing...", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void addWeatherData(String city) {
        ArrayList<HourlyWeatherDataModel> hourlyDataModelArrayList = new ArrayList<>();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.openweathermap.org/data/2.5/forecast?q="+ city +"&appid=0f26150d15bc00aefc58595aad69934c";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response){
                        try {
                            JSONObject City = response.getJSONObject("city");
                            JSONArray list = response.getJSONArray("list");
                            date.setText(list.getJSONObject(0).getString("dt_txt").substring(0, 10));
                            cityName.setText(City.getString("name"));

                            for(int i = 0; i < 10; i++) // for maintaining the 3 hourly weather data
                            {
                                JSONObject main = list.getJSONObject(i).getJSONObject("main");
                                JSONArray weather = list.getJSONObject(i).getJSONArray("weather");
                                int temp = main.getInt("temp");
                                String conditionView = weather.getJSONObject(0).getString("main");
                                temp -= 273.15;

                                if(i == 0) // applying the data of the current weather
                                {
                                    temperature.setText(Integer.toString(temp) + "°C");
                                    condition.setText(conditionView);
                                    conditionDescription.setText(weather.getJSONObject(0).getString("description"));
                                    humidity.setText(main.getString("humidity") + "%");
                                    windSpeed.setText(list.getJSONObject(0).getJSONObject("wind").getString("speed") + "Km/hr");
                                    visibility.setText(Integer.toString(list.getJSONObject(0).getInt("visibility") / 1000) + "Km");
                                }

                                String time = list.getJSONObject(i).getString("dt_txt").substring(11, 16);

                                switch (conditionView) {  // applying lottie animation to the hourly weather forecast
                                    case "Clear Sky":
                                    case "Sunny":
                                    case "Clear":
                                        hourlyDataModelArrayList.add(new HourlyWeatherDataModel(R.raw.sunny, Integer.toString(temp) + "°C", time));
                                        break;
                                    case "Partly Clouds":
                                    case "Clouds":
                                    case "overcast clouds":
                                        hourlyDataModelArrayList.add(new HourlyWeatherDataModel(R.raw.cloudy_day, Integer.toString(temp) + "°C", time));
                                        break;
                                    case "Mist":
                                    case "Foggy":
                                    case "Haze":
                                    case "Smoke":
                                        hourlyDataModelArrayList.add(new HourlyWeatherDataModel(R.raw.fogg_mist, Integer.toString(temp) + "°C", time));
                                        break;
                                    case "Rain":
                                    case "Light Rain":
                                    case "Drizzle":
                                    case "Moderate Rain":
                                    case "Showers":
                                    case "Heavy Rain":
                                        hourlyDataModelArrayList.add(new HourlyWeatherDataModel(R.raw.rain, Integer.toString(temp) + "°C", time));
                                        break;
                                    case "Light Snow":
                                    case "Moderate Snow":
                                    case "Heavy Snow":
                                    case "Blizzard":
                                        hourlyDataModelArrayList.add(new HourlyWeatherDataModel(R.raw.snow_fall, Integer.toString(temp) + "°C", time));
                                        break;
                                    default:
                                        hourlyDataModelArrayList.add(new HourlyWeatherDataModel(R.raw.sunny, Integer.toString(temp) + "°C", time));
                                        break;
                                }
                            }
                            hourlyView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            HourlyWeatherAdapter adapter = new HourlyWeatherAdapter(MainActivity.this, hourlyDataModelArrayList);
                            hourlyView.setAdapter(adapter);

                            changeImageAccordingToWeatherCondition(condition.getText().toString()); // changing the lottie animation to the current weather report
                        } catch (JSONException e) {
                            Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(jsonObjectRequest);
    }

    private void changeImageAccordingToWeatherCondition(String string) {

    }

    private void findId() {
    }
}