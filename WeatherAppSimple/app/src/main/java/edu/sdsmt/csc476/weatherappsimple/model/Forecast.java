package edu.sdsmt.csc476.weatherappsimple.model;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import edu.sdsmt.csc476.weatherappsimple.IListeners;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.util.JsonReader;
import android.util.Log;

public class Forecast implements Parcelable
{

	private static final String TAG = "Weather:LoadForecast";
	
	//@formatter:off
	
	// http://developer.weatherbug.com/docs/read/WeatherBug_API_JSON
	
	private String _URL = "http://i.wxbug.net/REST/Direct/GetForecastHourly.ashx?zip=" + "%s" + 
	                      "&ht=t&ht=i&ht=cp&ht=fl&ht=h" + 
	                      "&api_key=q3wj56tqghv7ybd8dy6gg4e7";
	
	// http://developer.weatherbug.com/docs/read/List_of_Icons
		
	private String _imageURL = "http://img.weather.weatherbug.com/forecast/icons/localized/500x420/en/trans/%s.png";
	
	//@formatter:on

	public String Temp;
	public String FeelsLikeTemp;
	public String Humidity;
	public String ChanceOfPrecipitation;
	public String AsOfTime;
	public Bitmap Image;
	
	public Forecast()
	{
		Temp = null;
		FeelsLikeTemp = null;
		Humidity = null;
		ChanceOfPrecipitation = null;
		Image = null;
	}

	private Forecast(Parcel parcel)
	{
		Temp = parcel.readString();
		FeelsLikeTemp = parcel.readString();
		Humidity = parcel.readString();
		ChanceOfPrecipitation = parcel.readString();
		AsOfTime = parcel.readString();
		Image = parcel.readParcelable(Bitmap.class.getClassLoader());
		
//		byte[] data = null;
//		parcel.readByteArray(data);
//		Image = convertByteArrayToBitmap(data);
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(Temp);
		dest.writeString(FeelsLikeTemp);
		dest.writeString(Humidity);
		dest.writeString(ChanceOfPrecipitation);
		dest.writeString(AsOfTime);
		dest.writeParcelable(Image, 0);
		
//		dest.writeByteArray(convertBitmapToByteArray(Image));
	}

	public static final Parcelable.Creator<Forecast> Creator = new Parcelable.Creator<Forecast>()
	{
		@Override
		public Forecast createFromParcel(Parcel pc)
		{
			return new Forecast(pc);
		}
		
		@Override
		public Forecast[] newArray(int size)
		{
			return new Forecast[size];
		}
	};

	public class LoadForecast extends AsyncTask<String, Void, Forecast>
	{
		private IListeners _listener;
		private Context _context;

		private int bitmapSampleSize = -1;

		public LoadForecast(Context context, IListeners listener)
		{
			_context = context;
			_listener = listener;
		}

		protected Forecast doInBackground(String... params)
		{
			Forecast forecast = null;

			try
			{
				if (params.length == 1 && params[0] != null)
				{
					URL webServiceURL = new URL(String.format(_URL, params[0]));

					// Open stream and assign to JsonReader.
					// NOTE:  This is a "bulk" read, not buffered.
					Reader streamReader = new InputStreamReader(webServiceURL.openStream());
					JsonReader jsonReader = new JsonReader(streamReader);

					// Consume first token.
					jsonReader.beginObject();

					if (jsonReader.nextName().equals("forecastHourlyList") == true)
					{
						forecast = readJSON(jsonReader);
					}

					// Always close a stream.
					jsonReader.close();
					streamReader.close();
				}

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
			_listener.onForecastLoaded(forecast);
		}

		private Bitmap readIconBitmap(String conditionString, int bitmapSampleSize)
		{
			Bitmap iconBitmap = null;
			try
			{
				URL weatherURL = new URL(String.format(_imageURL, conditionString));

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

		private Forecast readJSON(JsonReader reader)
		{
			Forecast forecast = new Forecast();

			try
			{
				// Consume first array token.
				reader.beginArray();
				
				// Consume first token in first entry of array.
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
					else if (name.equals("dateTime") == true)
					{
						forecast.AsOfTime = DateUtils.formatDateTime(_context, reader.nextLong(), DateUtils.FORMAT_SHOW_TIME);
					}
					else if (name.equals("icon") == true)
					{
						forecast.Image = readIconBitmap(reader.nextString(), bitmapSampleSize);
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

//	private byte[] convertBitmapToByteArray(Bitmap image)
//	{
//		ByteArrayOutputStream stream = new ByteArrayOutputStream();
//		image.compress(Bitmap.CompressFormat.PNG, 100, stream);
//		byte[] byteArray = stream.toByteArray();
//
//		try
//		{
//			stream.close();
//		}
//		catch (IOException e)
//		{
//			Log.e(TAG, e.toString());
//		}
//		finally
//		{
//			stream = null;
//			byteArray = null;
//		}
//
//		return byteArray;
//	}
//
//	private Bitmap convertByteArrayToBitmap(byte[] data)
//	{
//		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//		return bitmap;
//	}
}
