package com.jpmc.weather_jpmc.weather.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.test.AndroidTestCase;

import com.google.android.gms.common.api.GoogleApiClient;
import com.jpmc.weather_jpmc.weather.IUpdateWeather;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Harsh Raj on 10/25/17.
 */
public class WeatherActivityPresenterTest extends AndroidTestCase {

    WeatherActivityPresenter weatherActivityPresenter;
    Activity activity;
    IUpdateWeather updateWeather;
    SharedPreferences preferences;
    private GoogleApiClient googleApiClient;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        activity = mock(Activity.class);
        updateWeather = mock(IUpdateWeather.class);
        preferences = mock(SharedPreferences.class);
        googleApiClient = mock(GoogleApiClient.class);
        setWeatherActivityPresenter();
    }

    private void setWeatherActivityPresenter() {
        weatherActivityPresenter = new WeatherActivityPresenter();
        weatherActivityPresenter.setActivity(activity);
        weatherActivityPresenter.setUpdateWeather(updateWeather);
    }

    public void test_WeatherActivityPresenter_Ctor() {
        assertNotNull(weatherActivityPresenter);
    }

    public void test_connectGoogleAPIClient() {
        WeatherActivityPresenter spyWeatherActivityPresenter = spy(weatherActivityPresenter);
        when(activity.getPreferences(activity.MODE_PRIVATE)).thenReturn(preferences);
        when(preferences.getString("cityNameWithCountry", "")).thenReturn("test");
        spyWeatherActivityPresenter.setActivity(activity);
        spyWeatherActivityPresenter.setUpdateWeather(updateWeather);
        ConnectivityManager connectivityManager = mock(ConnectivityManager.class);
        when(activity.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager);
        NetworkInfo networkInfo = mock(NetworkInfo.class);
        when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        when(spyWeatherActivityPresenter.isNetworkConnection()).thenReturn(true);
        spyWeatherActivityPresenter.setGoogleApiClient(googleApiClient);
        spyWeatherActivityPresenter.connectGoogleAPIClient();
        verify(googleApiClient, times(1)).connect();
    }

    public void test_onConnectionSuspended() {
        weatherActivityPresenter.onConnectionSuspended(1);
        verify(updateWeather, times(1)).notifyNetworkError();
    }

    public void test_onConnectionFailed(){
        weatherActivityPresenter.onConnectionSuspended(1);
        verify(updateWeather, times(1)).notifyNetworkError();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
