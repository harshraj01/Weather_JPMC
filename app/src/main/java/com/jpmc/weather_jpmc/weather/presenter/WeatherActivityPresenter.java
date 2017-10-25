package com.jpmc.weather_jpmc.weather.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.jpmc.weather_jpmc.weather.IUpdateWeather;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by Harsh Raj on 10/25/17.
 */
public class WeatherActivityPresenter implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient googleApiClient;
    private Activity activity;
    private String cityNameWithCountry = "";
    private final String PREF_CITY_NAME_WITHCOUNTRY = "cityNameWithCountry";
    private final int PERMISSION_REQUEST_CODE = 123;
    private boolean isCheckPermissionNeeded = false;
    private final String TAG = WeatherActivityPresenter.class.getName();
    private IUpdateWeather updateWeather;
    private Location location;


    /**
     * WeatherActivityPresenter conatructor.
     *
     * @param activity      context
     * @param updateWeather callback to notyfy network errror to UI of update weather data.
     */
    public WeatherActivityPresenter(Activity activity, IUpdateWeather updateWeather) {
        this.activity = activity;
        this.updateWeather = updateWeather;
        initialize();
    }

    /**
     * Update data shared from share preference if it is stored.
     * Right noe only saving city name.
     * Set google api client.
     */
    private void initialize() {
        updateFromSharedPrefrence();
        setGoogleAPIClient();
    }

    /**
     * Set google api client if it is null.
     */
    public void setGoogleAPIClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(activity).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API)
                    .build();
        }
    }

    /**
     * connect to google api client.
     * Check if there is network error.
     */
    public void connectGoogleAPIClient() {
        boolean isNetworkConnected = isNetworkConnection();
        if (isCheckPermissionNeeded || !isNetworkConnected) {
            if (!isNetworkConnected) {
                updateWeather.notifyNetworkError();
            } else {
                updateWeather.notifyLocationServiceError();
            }
            return;
        }

        googleApiClient.connect();
        updateFromSharedPrefrence();
    }

    /**
     * Disconnect from google api client.
     */
    public void disconnectGoogleAPIClient() {
        googleApiClient.disconnect();
    }

    /**
     * Update data from shared preference.
     */
    private void updateFromSharedPrefrence() {
        SharedPreferences preferences = activity.getPreferences(activity.MODE_PRIVATE);
        cityNameWithCountry = preferences.getString(PREF_CITY_NAME_WITHCOUNTRY, "");
    }

    /**
     * Save tp shared preference.
     */
    public void saveToSharedPrefrence() {
        SharedPreferences preferences = activity.getPreferences(activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_CITY_NAME_WITHCOUNTRY, cityNameWithCountry);
        editor.commit();
    }

    /**
     * Check if there is network on device or not.
     *
     * @return
     */
    public boolean isNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    /**
     * Request permission and check if it is needed.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void requestPermissionResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0)
            for (int i = 0; i < grantResults.length; i++) {
                if (!permissions[i].equals("android.permission.INTERACT_ACROSS_USERS_FULL") && grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, " permission is denied, cannot continue to use  app");
                    isCheckPermissionNeeded = true;
                }
            }
    }

    /**
     * @param cityNameWithCountry
     */
    public void setCityNameWithCountry(String cityNameWithCountry) {
        this.cityNameWithCountry = cityNameWithCountry;
    }

    /**
     * Called when connection is failed from google api cleint.
     * Notity user about network error.
     *
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        updateWeather.notifyNetworkError();

    }

    /**
     * Called when the google api cleint is connected.
     *
     * @param bundle
     */
    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            checkAllPermission();
            return;
        }
        if (!TextUtils.isEmpty(cityNameWithCountry)) {
            // We always want to see the updated data from server. That is why requesting server on every launch of app.
            updateWeather.updateWeather(cityNameWithCountry);
        } else {
            /**
             * This is done to show the weather data when user launch the app for the
             * first time. Since it does not not look correct to show
             * user with empty data when user launch app for first time.
             */
            if (googleApiClient.isConnected()) {
                location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                if (location != null) {
                    Address address = getAddress(location.getLatitude(), location.getLongitude());
                    cityNameWithCountry = address.getLocality() + "," + address.getAdminArea();
                    updateWeather.updateWeather(cityNameWithCountry);
                } else {
                    updateWeather.notifyLocationServiceError();
                }
            } else {
                updateWeather.notifyLocationServiceError();
            }
        }
    }

    /**
     * Get the Address to pull the city name and state to use it to fetch result from server.
     *
     * @param latitude
     * @param longitude
     * @return
     */
    private Address getAddress(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(activity, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            return addresses.get(0);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    /**
     * Called when google api client connection suspended.
     *
     * @param i
     */
    @Override
    public void onConnectionSuspended(int i) {
        updateWeather.notifyNetworkError();
    }

    /**
     * Check permission mentioned in this app.
     */
    private void checkAllPermission() {

        // get all the permissions from package manager
        ArrayList<String> permissionArray;
        isCheckPermissionNeeded = false;
        permissionArray = new ArrayList<>();
        try {
            PackageInfo info = activity.getPackageManager().getPackageInfo(activity.getApplicationContext().getPackageName(), PackageManager.GET_PERMISSIONS);
            if (info != null && info.requestedPermissions != null) {
                for (String permission : info.requestedPermissions) {
                    if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                        permissionArray.add(permission);
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Unable to get permission info, " + e.getMessage());
        }
        if (!permissionArray.isEmpty()) {
            String[] requestPermissionArray = new String[permissionArray.size()];
            permissionArray.toArray(requestPermissionArray);
            ActivityCompat.requestPermissions(activity, requestPermissionArray,
                    PERMISSION_REQUEST_CODE);
        }
    }

    @VisibleForTesting
    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
    }

    @VisibleForTesting
    public WeatherActivityPresenter(){

    }

    @VisibleForTesting
    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @VisibleForTesting
    public void setUpdateWeather(IUpdateWeather updateWeather) {
        this.updateWeather = updateWeather;
    }

}
