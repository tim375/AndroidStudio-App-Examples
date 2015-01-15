// WeatherViewerActivity.java
// Main Activity for the Weather Viewer app.
package edu.sdsmt.csc476.weatherapp;

import java.util.HashMap;
import java.util.Map;

import edu.sdsmt.csc476.weatherapp.FragmentCityList.CitiesListChangeListener;
import edu.sdsmt.csc476.weatherapp.FragmentDialogAddCity.DialogFinishedListener;
import edu.sdsmt.csc476.weatherapp.model.ForecastLocation;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity implements DialogFinishedListener
{
	public static final String PREFERRED_CITY_NAME_KEY = "preferred_city_name";
	public static final String PREFERRED_CITY_ZIPCODE_KEY = "preferred_city_zipcode";
	public static final String SHARED_PREFERENCES_NAME = "weather_viewer_shared_preferences";

	private String lastSelectedCity;
	private SharedPreferences weatherSharedPreferences;

	private Map<String, String> favoriteCitiesMap;
	private FragmentCityList listCitiesFragment;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		listCitiesFragment = (FragmentCityList) getFragmentManager().findFragmentById(R.id.cities);

		listCitiesFragment.setCitiesListChangeListener(citiesListChangeListener);

		favoriteCitiesMap = new HashMap<String, String>();

		weatherSharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceStateBundle)
	{
		super.onSaveInstanceState(savedInstanceStateBundle);
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceStateBundle)
	{
		super.onRestoreInstanceState(savedInstanceStateBundle);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();

		if (favoriteCitiesMap.isEmpty())
		{
			loadSavedCities();
		}

		if (favoriteCitiesMap.isEmpty())
		{
			addSampleCities();
		}

		loadSelectedForecast();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
	
		inflater.inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == R.id.add_city_item)
		{
			showAddCityDialog();
			return true;
		}
	
		return false; 
	}

	@Override
	public void onDialogFinished(String zipcodeString, boolean preferred)
	{
		getCityNameFromZipcode(zipcodeString, preferred);
	}

	private CitiesListChangeListener citiesListChangeListener =
			new CitiesListChangeListener()
			{
				@Override
				public void onSelectedCityChanged(String cityNameString)
				{
					selectForecast(cityNameString);
				}

				@Override
				public void onPreferredCityChanged(String cityNameString)
				{
					setPreferred(cityNameString);
				}
			};

	public void setPreferred(String cityNameString)
	{
		String cityZipcodeString = favoriteCitiesMap.get(cityNameString);
		Editor preferredCityEditor = weatherSharedPreferences.edit();

		preferredCityEditor.putString(PREFERRED_CITY_NAME_KEY, cityNameString);
		preferredCityEditor.putString(PREFERRED_CITY_ZIPCODE_KEY, cityZipcodeString);
		preferredCityEditor.apply();
		
		lastSelectedCity = null;
		loadSelectedForecast();

	}

	public void addCity(String city, String zipcode, boolean select)
	{
		favoriteCitiesMap.put(city, zipcode);
		listCitiesFragment.addCity(city, select);
		
		Editor preferenceEditor = weatherSharedPreferences.edit();
		preferenceEditor.putString(city, zipcode);
		preferenceEditor.apply();
	}

	public void selectForecast(String name)
	{
		lastSelectedCity = name;
		String zipcodeString = favoriteCitiesMap.get(name);
		if (zipcodeString == null)
		{
			return;
		}

		// get the current visible ForecastFragment
		FragmentForecast currentForecastFragment = (FragmentForecast) getFragmentManager().findFragmentById(R.id.forecast_replacer);

		if (currentForecastFragment == null || !currentForecastFragment.getZipcode().equals(zipcodeString))
		{
			currentForecastFragment = FragmentForecast.newInstance(zipcodeString);

			FragmentTransaction forecastFragmentTransaction = getFragmentManager().beginTransaction();

			forecastFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			forecastFragmentTransaction.replace(R.id.forecast_replacer, currentForecastFragment);

			forecastFragmentTransaction.commit();
		}
	}

	private void addSampleCities()
	{
		String[] sampleCityNamesArray = getResources().getStringArray(R.array.default_city_names);
		String[] sampleCityZipcodesArray = getResources().getStringArray(R.array.default_city_zipcodes);
	
		for (int i = 0; i < sampleCityNamesArray.length; i++)
		{
			if (i == 0)
			{
				setPreferred(sampleCityNamesArray[i]);
			}
	
			addCity(sampleCityNamesArray[i], sampleCityZipcodesArray[i], false);
		}
	}

	private void loadSavedCities()
	{
		Map<String, ?> citiesMap = weatherSharedPreferences.getAll();
	
		for (String cityString : citiesMap.keySet())
		{
			if (!(cityString.equals(PREFERRED_CITY_NAME_KEY) || cityString.equals(PREFERRED_CITY_ZIPCODE_KEY)))
			{
				addCity(cityString, (String) citiesMap.get(cityString), false);
			}
		}
	}

	private void loadSelectedForecast()
	{
		if (lastSelectedCity != null)
		{
			selectForecast(lastSelectedCity);
		} 
		else
		{
			String cityNameString = weatherSharedPreferences.getString(PREFERRED_CITY_NAME_KEY, getResources().getString(R.string.default_zipcode));
			selectForecast(cityNameString); 
		}
	}

	private void showAddCityDialog()
	{
		FragmentDialogAddCity newAddCityDialogFragment = new FragmentDialogAddCity();

		FragmentManager thisFragmentManager = getFragmentManager();

		FragmentTransaction addCityFragmentTransition = thisFragmentManager.beginTransaction();

		newAddCityDialogFragment.show(addCityFragmentTransition, "");
	}

	private void getCityNameFromZipcode(String zipcodeString, boolean preferred)
	{
		if (favoriteCitiesMap.containsValue(zipcodeString))
		{
			Toast errorToast = Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.duplicate_zipcode_error), Toast.LENGTH_LONG);
			errorToast.setGravity(Gravity.CENTER, 0, 0);
			errorToast.show();
		}
		else
		{
			ForecastLocation forecastLocation = new ForecastLocation();
			forecastLocation.new LoadLocation(new CityNameLocationLoadedListener()).execute(zipcodeString);
		}
	}

	private class CityNameLocationLoadedListener implements IListeners
	{
		private boolean preferred;

		@Override
		public void onLocationLoaded(ForecastLocation location)
		{

            if (location == null)
            {
                Toast.makeText(getApplication(), R.string.error_location_null, Toast.LENGTH_LONG).show();
                return;
            }

			if (location.City != null)
			{
				addCity(location.City, location.ZipCode, !preferred);

				if (preferred)
				{
					setPreferred(location.City);
				}
			}
			else
			{
				Toast zipcodeToast = Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.invalid_zipcode_error), Toast.LENGTH_LONG);
				zipcodeToast.setGravity(Gravity.CENTER, 0, 0);
				zipcodeToast.show();
			}
		}

		@Override
		public void onForecastLoaded(Bitmap image, String temperature, String feelsLike, String humidity, String precipitation)
		{
			// TODO Auto-generated method stub
			
		}
	}
}