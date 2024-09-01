package com.forecast.weather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.forecast.weather.R;
import com.forecast.weather.model.FutureDayWeatherDataModel;

import java.util.ArrayList;

public class FutureDayWeatherAdapter extends RecyclerView.Adapter<FutureDayWeatherAdapter.next5WeatherHolder>{

    Context context;
    ArrayList<FutureDayWeatherDataModel> futureDayDataModelArrayList;

    public FutureDayWeatherAdapter(Context context, ArrayList<FutureDayWeatherDataModel> futureDayDataModelArrayList) {
        this.context = context;
        this.futureDayDataModelArrayList = futureDayDataModelArrayList;
    }

    @NonNull
    @Override
    public next5WeatherHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.future_day_weather_layout, null);
        return new next5WeatherHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull next5WeatherHolder holder, int position) {
        FutureDayWeatherDataModel model = futureDayDataModelArrayList.get(position);
        holder.conditionView.setAnimation(model.getConditionView());
        holder.temperature.setText(model.getTemp());
        holder.condition.setText(model.getCondition());
        holder.day.setText(model.getDay());
    }

    @Override
    public int getItemCount() {
        return futureDayDataModelArrayList.size();
    }

    public class next5WeatherHolder extends RecyclerView.ViewHolder{
        LottieAnimationView conditionView;
        TextView temperature, day, condition;
        public next5WeatherHolder(@NonNull View itemView) {
            super(itemView);
            conditionView = itemView.findViewById(R.id.conditionViewNext5);
            temperature = itemView.findViewById(R.id.tempTxtNext5);
            day = itemView.findViewById(R.id.dayNext5);
            condition = itemView.findViewById(R.id.conditionTxtNext5);
        }
    }
}
