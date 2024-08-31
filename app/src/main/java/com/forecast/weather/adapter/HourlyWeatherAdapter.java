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
import com.forecast.weather.model.HourlyWeatherDataModel;

import java.util.ArrayList;

public class HourlyWeatherAdapter extends RecyclerView.Adapter<HourlyWeatherAdapter.hourlyDataViewHolder>{

    Context context;
    ArrayList<HourlyWeatherDataModel> hourlyDataModelArray;

    public HourlyWeatherAdapter(Context context, ArrayList<HourlyWeatherDataModel> hourlyDataModel) {
        this.context = context;
        this.hourlyDataModelArray = hourlyDataModel;
    }

    @NonNull
    @Override
    public hourlyDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.hourly_weather_layout, null);
        return new hourlyDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull hourlyDataViewHolder holder, int position) {
        HourlyWeatherDataModel model = hourlyDataModelArray.get(position);
        holder.conditionView.setAnimation(model.getConditionView());
        holder.temperature.setText(model.getTemp());
        holder.time.setText(model.getTime());
    }

    @Override
    public int getItemCount() {
        return hourlyDataModelArray.size();
    }

    public class hourlyDataViewHolder extends RecyclerView.ViewHolder{
        LottieAnimationView conditionView;
        TextView temperature, time;
        public hourlyDataViewHolder(@NonNull View itemView) {
            super(itemView);
            conditionView = itemView.findViewById(R.id.hourly_lottie);
            temperature = itemView.findViewById(R.id.hourlyTemp);
            time = itemView.findViewById(R.id.hourTxt);
        }
    }
}
