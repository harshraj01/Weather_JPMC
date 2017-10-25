package com.jpmc.weather_jpmc.weather.view.activity;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;

import com.jpmc.weather_jpmc.Constants;
import com.jpmc.weather_jpmc.R;
import com.jpmc.weather_jpmc.base.BaseActivity;
import com.jpmc.weather_jpmc.weather.IUpdateWeather;
import com.jpmc.weather_jpmc.weather.dialog.WeatherDialog;
import com.jpmc.weather_jpmc.weather.presenter.WeatherActivityPresenter;
import com.jpmc.weather_jpmc.weather.view.fragment.CityWeatherFragment;

/**
 * Created by Harsh Raj on 10/24/17.
 */

/**
 * This is main landing screen when user launches weather app.
 * This is only fragment attached to this activity
 * which shows City name, temperature in fahrenheit, weather description, min and max temperature and weather image.
 * All these data are recived from OpenWeatherMap server.
 */
public class WeatherActivity extends BaseActivity implements IUpdateWeather {

    private CityWeatherFragment cityWeatherFragment;
    private WeatherActivityPresenter weatherActivityPresenter;

    /**
     * Sets the WeatherActivityPresenter instance.
     *
     * @param savedInstanceState if savedInstanceState is null the it will create a new fragment instance and attach to it
     *                           otheerwise it will use the existing fragment instance.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        weatherActivityPresenter = new WeatherActivityPresenter(this, (IUpdateWeather) this);

        if (savedInstanceState == null) {
            cityWeatherFragment = new CityWeatherFragment();
            commitFragment(R.id.city_weather_fragment, cityWeatherFragment);
        } else {
            cityWeatherFragment = (CityWeatherFragment) getFragment(R.id.city_weather_fragment);
        }
    }

    /**
     * Connect to google api client.
     */
    @Override
    protected void onResume() {
        super.onResume();
        weatherActivityPresenter.connectGoogleAPIClient();
    }

    /**
     * Disconnect google api client.
     */
    @Override
    protected void onPause() {
        super.onPause();
        weatherActivityPresenter.disconnectGoogleAPIClient();
    }

    /**
     * Save city name data to shared preference.
     */
    @Override
    protected void onStop() {
        super.onStop();
        //Save city name to shared preference since we want to show same city data if on relaunch of app.
        weatherActivityPresenter.saveToSharedPrefrence();
    }

    /**
     * Set WeatherActivityPresenter instance to null.
     * Since WeatherActivityPresenter contains activity instance so it is better to nullify
     * WeatherActivityPresenter instance to avoid memory leak.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        weatherActivityPresenter = null;
    }

    /**
     * Prompt user if permission is not given which is required by weather application.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        weatherActivityPresenter.requestPermissionResult(requestCode, permissions, grantResults);
    }

    /**
     * Set the search option menu.
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_weather, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                //This will called every time user do changes to text in search view.
                // So we do not require this.
                return true;
            }

            public boolean onQueryTextSubmit(String cityNameWithCountry) {
                /**
                 *Here you can get the value "cityNameWithCountry" which is entered in the search box.
                 * We have to do validation whether city name provided by user is correct format or not.
                 * Also we can use Google places API PlaceAutoComplete feataure to give user option for city name.
                 *
                 * Right now if we provide any randowm text then OpenAppWeatheMap is returning weather of any random place.
                 * This can be solved by validation of string and use of  Google places API PlaceAutoComplete.
                 *
                 */
                weatherActivityPresenter.setCityNameWithCountry(cityNameWithCountry);
                updateWeather(cityNameWithCountry);
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);


        return true;
    }

    /**
     * Update weather fragment when we receive async weather data from server.
     *
     * @param cityNameWithCountry
     */
    @Override
    public void updateWeather(String cityNameWithCountry) {
        cityWeatherFragment.weatherUpdate(cityNameWithCountry);
    }

    /**
     * Show dialog message when there is not network.
     */
    @Override
    public void notifyNetworkError() {
        WeatherDialog weatherDialog = new WeatherDialog();
        Bundle args = new Bundle();
        args.putString(Constants.dialogTitle, getResources().getString(R.string.error_title));
        args.putString(Constants.dialogMessage, getResources().getString(R.string.network_error_messsage));
        weatherDialog.setArguments(args);
        weatherDialog.show(getFragmentManager(), "Network Error");
    }

    /**
     *  Show dialog message when there is location service error.
     */
    @Override
    public void notifyLocationServiceError() {
        WeatherDialog weatherDialog = new WeatherDialog();
        Bundle args = new Bundle();
        args.putString(Constants.dialogTitle, getResources().getString(R.string.error_title));
        args.putString(Constants.dialogMessage, getResources().getString(R.string.location_service_error_messsage));
        weatherDialog.setArguments(args);
        weatherDialog.show(getFragmentManager(), "Location Service Error");
    }

}
