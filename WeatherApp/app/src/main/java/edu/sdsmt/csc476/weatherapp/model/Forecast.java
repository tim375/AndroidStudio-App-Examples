package edu.sdsmt.csc476.weatherapp.model;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import edu.sdsmt.csc476.weatherapp.IListeners;

public class Forecast
{

	// http://developer.weatherbug.com/docs/read/WeatherBug_API_JSON

	public Forecast()
	{
		Temp = null;
		FeelsLikeTemp = null;
		Humidity = null;
		ChanceOfPrecipitation = null;
		Image = null;
	}

	public String Temp;
	public String FeelsLikeTemp;
	public String Humidity;
	public String ChanceOfPrecipitation;
	public Bitmap Image;

	public class LoadForecast extends AsyncTask<String, Void, Forecast>
	{
		private static final String TAG = "ReadForecastTask.java";
		private IListeners _listener;

		private int bitmapSampleSize = -1;

		public LoadForecast(IListeners listener)
		{
			_listener = listener;
		}

		public void setSampleSize(int sampleSize)
		{
			this.bitmapSampleSize = sampleSize;
		}

		protected Forecast doInBackground(String... params)
		{
			Forecast forecast = null;

			try
			{
				URL webServiceURL = new URL("http://i.wxbug.net/REST/Direct/GetForecastHourly.ashx?zip="
						+ params[0] + "&ht=t&ht=i&ht=cp&ht=fl&ht=h&api_key=q3wj56tqghv7ybd8dy6gg4e7");

				Reader forecastReader = new InputStreamReader(webServiceURL.openStream());

				JsonReader forecastJsonReader = new JsonReader(forecastReader);

				forecastJsonReader.beginObject();

				String name = forecastJsonReader.nextName();

				if (name.equals("forecastHourlyList") == true)
				{
					forecast = readForecast(forecastJsonReader);
				}

				forecastJsonReader.close();

			}
			catch (MalformedURLException e)
			{
				Log.e(TAG, e.toString());
			}
			catch (IOException e)
			{
				Log.e(TAG, e.toString());
			}
			catch (IllegalStateException e)
			{
				Log.e(TAG, e.toString() + params[0]);
			}
			catch (Exception e)
			{
				Log.e(TAG, e.toString());
			}

			return forecast;
		}

		protected void onPostExecute(Forecast forecast)
		{
			_listener.onForecastLoaded(forecast.Image, forecast.Temp, forecast.FeelsLikeTemp, forecast.Humidity, forecast.ChanceOfPrecipitation);
		}

		private Bitmap getIconBitmap(String conditionString, int bitmapSampleSize)
		{
			Bitmap iconBitmap = null;
			try
			{
				URL weatherURL = new URL("http://img.weather.weatherbug.com/forecast/icons/localized/500x420/en/trans/"
						+ conditionString + ".png");

				BitmapFactory.Options options = new BitmapFactory.Options();
				if (bitmapSampleSize != -1)
				{
					options.inSampleSize = bitmapSampleSize;
				}

				iconBitmap = BitmapFactory.decodeStream(weatherURL.openStream(), null, options);
			}
			catch (MalformedURLException e)
			{
				Log.e(TAG, e.toString());
			}
			catch (IOException e)
			{
				Log.e(TAG, e.toString());
			}
			catch (Exception e)
			{
				Log.e(TAG, e.toString());
			}

			return iconBitmap;
		}

		private Forecast readForecast(JsonReader reader)
		{
			Forecast forecast = new Forecast();

			try
			{
				reader.beginArray();
				reader.beginObject();

				while (reader.hasNext())
				{
					String name = reader.nextName();

					if (name.equals("temperature") == true)
					{
						forecast.Temp = reader.nextString();
					}
					else if (name.equals("feelsLike") == true)
					{
						forecast.FeelsLikeTemp = reader.nextString();
					}
					else if (name.equals("humidity") == true)
					{
						forecast.Humidity = reader.nextString();
					}
					else if (name.equals("chancePrecip") == true)
					{
						forecast.ChanceOfPrecipitation = reader.nextString();
					}
					else if (name.equals("icon") == true)
					{
						forecast.Image = getIconBitmap(reader.nextString(), bitmapSampleSize);
					}
					else
					{
						reader.skipValue();
					}
				}
			}
			catch (IOException e)
			{
				Log.e(TAG, e.toString());
			}
			catch (Exception e)
			{
				Log.e(TAG, e.toString());
			}
			return forecast;
		}
	}
}
