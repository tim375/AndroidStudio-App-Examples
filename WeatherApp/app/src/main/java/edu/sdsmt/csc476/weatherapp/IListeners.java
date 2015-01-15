package edu.sdsmt.csc476.weatherapp;

import android.graphics.Bitmap;

import edu.sdsmt.csc476.weatherapp.model.ForecastLocation;

public interface IListeners
{
	public void onLocationLoaded(ForecastLocation forecastLocation);
	public void onForecastLoaded(Bitmap image, String temperature, String feelsLike, String humidity, String precipitation);
}
