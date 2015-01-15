package edu.sdsmt.csc476.weatherapp.model;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import edu.sdsmt.csc476.weatherapp.IListeners;

public class ForecastLocation
{

	public ForecastLocation()
	{
		ZipCode = null;
		City = null;
		State = null;
		Country = null;
		CurrentForecast = null;
	}

	public String ZipCode;
	public String City;
	public String State;
	public String Country;
	public Forecast CurrentForecast;

	public class LoadLocation extends AsyncTask<String, Void, ForecastLocation>
	{
		private static final String TAG = "ReadLocatonTask.java";

		private IListeners _listener;

		public LoadLocation(IListeners listener)
		{
			_listener = listener;
		}

		@Override
		protected ForecastLocation doInBackground(String... params)
		{
			ForecastLocation forecastLocation = null;

			try
			{

				URL url = new URL("http://i.wxbug.net/REST/Direct/GetLocation.ashx?zip=" + params[0]
						+ "&api_key=q3wj56tqghv7ybd8dy6gg4e7");

				Reader streamReader = new InputStreamReader(url.openStream());

				JsonReader jsonReader = new JsonReader(streamReader);

				forecastLocation = readJSON(jsonReader);

				jsonReader.close();
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
			return forecastLocation;
		}

		protected void onPostExecute(ForecastLocation forecastLocation)
		{
			_listener.onLocationLoaded(forecastLocation);
		}

		private ForecastLocation readJSON(JsonReader jsonReader) throws IOException
		{

			ForecastLocation forecastLocation = new ForecastLocation();

			try
			{
				jsonReader.beginObject();

				String name = jsonReader.nextName();

				if (name.equals("location") == true)
				{
					jsonReader.beginObject();

					String nextNameString;

					while (jsonReader.hasNext())
					{
						nextNameString = jsonReader.nextName();

						if (nextNameString.equals("city") == true)
						{
							forecastLocation.City = jsonReader.nextString();
						}
						else if (nextNameString.equals("state") == true)
						{
							forecastLocation.State = jsonReader.nextString();
						}
						else if (nextNameString.equals("country") == true)
						{
							forecastLocation.Country = jsonReader.nextString();
						}
						else if (nextNameString.equals("zipCode") == true)
						{
							forecastLocation.ZipCode = jsonReader.nextString();
						}
						else
						{
							jsonReader.skipValue();
						}
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

			return forecastLocation;
		}
	}

}
