package edu.sdsmt.csc476.weatherappsimple.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

import edu.sdsmt.csc476.weatherappsimple.IListeners;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.JsonReader;
import android.util.Log;

public class ForecastLocation implements Parcelable
{

	private static final String TAG = "Weather:ForecastLocation";

	//@formatter:off
	private String _URL = "http://i.wxbug.net/REST/Direct/GetLocation.ashx?zip=" + "%s" + 
			             "&api_key=q3wj56tqghv7ybd8dy6gg4e7";
	
	//@formatter:on

	public ForecastLocation()
	{
		ZipCode = null;
		City = null;
		State = null;
		Country = null;
	}

	private ForecastLocation(Parcel parcel)
	{
		ZipCode = parcel.readString();
		City = parcel.readString();
		State = parcel.readString();
		Country = parcel.readString();
	}

	public String ZipCode;
	public String City;
	public String State;
	public String Country;

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(ZipCode);
		dest.writeString(City);
		dest.writeString(State);
		dest.writeString(Country);
	}

	public static final Parcelable.Creator<ForecastLocation> Creator = new Parcelable.Creator<ForecastLocation>()
	{
		@Override
		public ForecastLocation createFromParcel(Parcel pc)
		{
			return new ForecastLocation(pc);
		}

		@Override
		public ForecastLocation[] newArray(int size)
		{
			return new ForecastLocation[size];
		}
	};

	public class LoadLocation extends AsyncTask<String, Void, ForecastLocation>
	{
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
				if (params.length == 1 && params[0] != null)
				{

					/*
					// ******************************************************************
					// OPTION 1:
					// ******************************************************************
					// Since there is a zip code passed to the task, build
					// the URL.
					URL url = new URL(String.format(_URL, params[0]));

					// Open stream and assign to JsonReader.
					// NOTE: This is a "bulk" read, not buffered.
					Reader streamReader = new InputStreamReader(url.openStream());
					JsonReader jsonReader = new JsonReader(streamReader);

					// Parse the JSON-based stream.
					forecastLocation = readJSON(jsonReader);

					// Always close a stream.
					jsonReader.close();
					streamReader.close();
					// ******************************************************************
					*/
					
					// ******************************************************************
					// OPTION 2:
					// ******************************************************************
					StringBuilder stringBuilder = new StringBuilder();
					HttpClient client = new DefaultHttpClient();

					HttpResponse response = client.execute(new HttpGet(String.format(_URL, params[0])));
					if (response.getStatusLine().getStatusCode() == 200)
					{
						HttpEntity entity = response.getEntity();
						InputStream content = entity.getContent();
						BufferedReader reader = new BufferedReader(new InputStreamReader(content));

						// Read the JSON.
						String line;
						while ((line = reader.readLine()) != null)
						{
							stringBuilder.append(line);
						}

						forecastLocation = readJSON(stringBuilder.toString());
					}
					// ******************************************************************
				}
			}
			catch (MalformedURLException e)
			{
				forecastLocation = null;
				Log.e(TAG, e.toString());
			}
			catch (IOException e)
			{
				forecastLocation = null;
				Log.e(TAG, e.toString());
			}
			catch (JSONException e)
			{
				forecastLocation = null;
				Log.e(TAG, e.toString());
			}
			catch (Exception e)
			{
				forecastLocation = null;
				Log.e(TAG, e.toString());
			}
			return forecastLocation;
		}

		protected void onPostExecute(ForecastLocation forecastLocation)
		{
			_listener.onLocationLoaded(forecastLocation);
		}

		@SuppressWarnings("unused")
		private ForecastLocation readJSON(JsonReader jsonReader) throws IOException
		{

			ForecastLocation forecastLocation = new ForecastLocation();

			jsonReader.beginObject();

			String name = jsonReader.nextName();

			if (name.equals("location") == true)
			{
				jsonReader.beginObject();

				while (jsonReader.hasNext())
				{
					name = jsonReader.nextName();

					if (name.equals("city") == true)
					{
						forecastLocation.City = jsonReader.nextString();
					}
					else if (name.equals("state") == true)
					{
						forecastLocation.State = jsonReader.nextString();
					}
					else if (name.equals("country") == true)
					{
						forecastLocation.Country = jsonReader.nextString();
					}
					else if (name.equals("zipCode") == true)
					{
						forecastLocation.ZipCode = jsonReader.nextString();
					}
					else
					{
						jsonReader.skipValue();
					}
				}
			}

			return forecastLocation;
		}

		private ForecastLocation readJSON(String jsonString) throws JSONException
		{
			ForecastLocation forecastLocation = new ForecastLocation();

			JSONObject jToken = new JSONObject(jsonString);

			if (jToken.has("location") == true)
			{
				JSONObject location = jToken.getJSONObject("location");
				
				forecastLocation.City = location.getString("city");
				forecastLocation.State = location.getString("state");
				forecastLocation.Country = location.getString("country");
				forecastLocation.ZipCode = location.getString("zipCode");
			}
			
			return forecastLocation;
		}
	}
}
