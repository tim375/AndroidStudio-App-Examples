package edu.sdsmt.csc476.weatherappsimple;

import edu.sdsmt.csc476.weatherappsimple.model.Forecast;
import edu.sdsmt.csc476.weatherappsimple.model.ForecastLocation;


public interface IListeners
{
	public void onLocationLoaded(ForecastLocation forecastLocation);
	public void onForecastLoaded(Forecast forecast);
}
