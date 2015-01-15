// SingleForecastFragment.java
// Displays forecast information for a single city.
package edu.sdsmt.csc476.weatherapp;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import edu.sdsmt.csc476.weatherapp.model.Forecast;
import edu.sdsmt.csc476.weatherapp.model.ForecastLocation;

public class FragmentForecast extends Fragment
{
	private String zipcodeString;

	private static final String LOCATION_KEY = "location";
	private static final String TEMPERATURE_KEY = "temperature";
	private static final String FEELS_LIKE_KEY = "feels_like";
	private static final String HUMIDITY_KEY = "humidity";
	private static final String PRECIPITATION_KEY = "chance_precipitation";
	private static final String IMAGE_KEY = "image";

	private static final String ZIP_CODE_KEY = "id_key";

	private View forecastView;
	private TextView temperatureTextView;
	private TextView feelsLikeTextView;
	private TextView humidityTextView;

	private TextView locationTextView;

	private TextView chanceOfPrecipitationTextView;
	private ImageView conditionImageView;
	private TextView loadingTextView;
	private Context context;
	private Bitmap conditionBitmap;

	public static FragmentForecast newInstance(String zipcodeString)
	{
		FragmentForecast newForecastFragment = new FragmentForecast();

		Bundle argumentsBundle = new Bundle();
		argumentsBundle.putString(ZIP_CODE_KEY, zipcodeString);
		newForecastFragment.setArguments(argumentsBundle);

		return newForecastFragment;

	}

	public static FragmentForecast newInstance(Bundle argumentsBundle)
	{
		String zipcodeString = argumentsBundle.getString(ZIP_CODE_KEY);
		return newInstance(zipcodeString);
	}

	@Override
	public void onCreate(Bundle argumentsBundle)
	{
		super.onCreate(argumentsBundle);

		this.zipcodeString = getArguments().getString(ZIP_CODE_KEY);
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceStateBundle)
	{
		super.onSaveInstanceState(savedInstanceStateBundle);

		savedInstanceStateBundle.putString(LOCATION_KEY, locationTextView.getText().toString());
		savedInstanceStateBundle.putString(TEMPERATURE_KEY, temperatureTextView.getText().toString());
		savedInstanceStateBundle.putString(FEELS_LIKE_KEY, feelsLikeTextView.getText().toString());
		savedInstanceStateBundle.putString(HUMIDITY_KEY, humidityTextView.getText().toString());
		savedInstanceStateBundle.putString(PRECIPITATION_KEY, chanceOfPrecipitationTextView.getText().toString());
		savedInstanceStateBundle.putParcelable(IMAGE_KEY, conditionBitmap);
	}

	public String getZipcode()
	{
		return zipcodeString;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.forecast_fragment_layout, null);

		forecastView = rootView.findViewById(R.id.forecast_layout);
		loadingTextView = (TextView) rootView.findViewById(R.id.loading_message);
		locationTextView = (TextView) rootView.findViewById(R.id.location);
		temperatureTextView = (TextView) rootView.findViewById(R.id.temperature);
		feelsLikeTextView = (TextView) rootView.findViewById(R.id.feels_like);
		humidityTextView = (TextView) rootView.findViewById(R.id.humidity);
		chanceOfPrecipitationTextView = (TextView) rootView.findViewById(R.id.chance_of_precipitation);
		conditionImageView = (ImageView) rootView.findViewById(R.id.forecast_image);

		context = rootView.getContext();

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceStateBundle)
	{
		super.onActivityCreated(savedInstanceStateBundle);

		if (savedInstanceStateBundle == null)
		{
			forecastView.setVisibility(View.GONE);
			loadingTextView.setVisibility(View.VISIBLE);

			ForecastLocation forecastLocation = new ForecastLocation();
			forecastLocation.new LoadLocation(new AsynTaskListener()).execute(zipcodeString);

		}
		else
		{
			conditionImageView.setImageBitmap((Bitmap) savedInstanceStateBundle.getParcelable(IMAGE_KEY));
			locationTextView.setText(savedInstanceStateBundle.getString(LOCATION_KEY));
			temperatureTextView.setText(savedInstanceStateBundle.getString(TEMPERATURE_KEY));
			feelsLikeTextView.setText(savedInstanceStateBundle.getString(FEELS_LIKE_KEY));
			humidityTextView.setText(savedInstanceStateBundle.getString(HUMIDITY_KEY));
			chanceOfPrecipitationTextView.setText(savedInstanceStateBundle.getString(PRECIPITATION_KEY));
		}
	}

	private class AsynTaskListener implements IListeners
	{
		@Override
		public void onLocationLoaded(ForecastLocation location)
		{
            if (location == null)
            {
                Toast.makeText(getActivity(), R.string.error_location_null, Toast.LENGTH_LONG).show();
                return;
            }

			if (location.City == null)
			{
				Toast errorToast = Toast.makeText(context, context.getResources().getString(R.string.null_data_toast), Toast.LENGTH_LONG);
				errorToast.setGravity(Gravity.CENTER, 0, 0);
				errorToast.show();
				return;
			}

			locationTextView.setText(location.City + " " + location.State + ", " + location.ZipCode + " " + location.Country);

			Forecast forecast = new Forecast();
			forecast.new LoadForecast(new AsynTaskListener()).execute(location.ZipCode);
		}

		@Override
		public void onForecastLoaded(Bitmap image, String temperature, String feelsLike, String humidity, String precipitation)
		{
			if (FragmentForecast.this.isAdded() == false)
			{
				return;
			}
			else if (image == null)
			{
				Toast errorToast = Toast.makeText(context, context.getResources().getString(R.string.null_data_toast), Toast.LENGTH_LONG);
				errorToast.setGravity(Gravity.CENTER, 0, 0);
				errorToast.show();
				return;
			}

			Resources resources = FragmentForecast.this.getResources();

			conditionImageView.setImageBitmap(image);
			conditionBitmap = image;
			temperatureTextView.setText(temperature + (char) 0x00B0 + resources.getString(R.string.temperature_unit));
			feelsLikeTextView.setText(feelsLike + (char) 0x00B0 + resources.getString(R.string.temperature_unit));
			humidityTextView.setText(humidity + (char) 0x0025);
			chanceOfPrecipitationTextView.setText(precipitation + (char) 0x0025);
			loadingTextView.setVisibility(View.GONE);
			forecastView.setVisibility(View.VISIBLE);
		}
	}
}