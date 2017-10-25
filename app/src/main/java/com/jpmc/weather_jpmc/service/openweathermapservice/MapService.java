package com.jpmc.weather_jpmc.service.openweathermapservice;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jpmc.weather_jpmc.Constants;
import com.jpmc.weather_jpmc.weather.model.CityWeatherModel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;


/**
 * Created by Harsh Raj on 10/24/17.
 */

/**
 * OpenWeather map service calls.
 */
public class MapService {

    private final String USER_AGENT = "jpmc weather";
    private String TAG = MapService.class.getName();

    /**
     * Get the weather data from OpenWeatherMap.
     * If there is any error from server then return null. The calling method should handle null and display error messae accordingly.
     * This method is synchronized because other thread should not run any method of this class since there will
     * new weather data coming from server which might have new weather image.
     * That is why class object lock is required, so that we always have latest updated weather image from server as per
     * the new data.
     * @param cityNameWithCountry in format cityname,state
     * @return
     */
    public synchronized CityWeatherModel getWeatherData(String cityNameWithCountry) {

        if (cityNameWithCountry == null){
            return null;
        }
        BufferedReader bufferedReader = null;
        try {
            String uriString = String.format(Locale.US, "http://api.openweathermap.org/data/2.5/weather?q=%s&units=%s&APPID=%s", cityNameWithCountry, "imperial", Constants.OPEN_WEATHER_MAP_API_ID);

            URL url = new URL(uriString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (!TextUtils.isEmpty(USER_AGENT)) {
                connection.setRequestProperty("User-Agent", USER_AGENT);
            }
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = bufferedReader.readLine()) != null) {
                response.append(inputLine);
            }
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(response.toString(), CityWeatherModel.class);
        } catch (IOException e) {
            Log.e(TAG, "Exception in getting weather: " + e.getMessage());
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Log.e(TAG, "Exception in closing buffered reader: " + e.getMessage());
                }
            }
        }
        return null;
    }

    /**
     * Get the save weather image from OpenWeatherMap.
     * Save the image received from server to image location provided from caller of this method.
     * This method is synchronized because other thread should not run any method of this class since there might
     * a new weather data going geting fetch from server. So in that case we need latest and updated weather image.
     * So class lock object is required.
     * @param iconId to query OpenWeatherMap API.
     * @param imageSaveLocation to save the image received from OpenWeatherMap.
     * @return the boolean result on image save successful or not.
     */
    public synchronized boolean saveWeatherImage(String iconId, String imageSaveLocation) {
        BufferedOutputStream bufferedOutputStream = null;
        HttpURLConnection httpURLConnection = null;
        boolean imageSaveSuccess = false;
        try {
            String uriString = String.format(Locale.US, "http://openweathermap.org/img/w/%s.png", iconId);
            URL url = new URL(uriString);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            if (!TextUtils.isEmpty(USER_AGENT)) {
                httpURLConnection.setRequestProperty("User-Agent", USER_AGENT);
            }
            if (httpURLConnection.getResponseCode() == 200) {
                BufferedInputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
                File file = new File(imageSaveLocation);
                bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
                int i;
                while ((i = in.read()) != -1) {
                    bufferedOutputStream.write(i);
                }
            }
            imageSaveSuccess = true;
        } catch (IOException e) {
            Log.e(TAG, "Exception in getting weather icon: " + e.getMessage());
        } finally {
            if (bufferedOutputStream != null && httpURLConnection != null) {
                try {
                    bufferedOutputStream.flush();
                    httpURLConnection.disconnect();
                } catch (IOException e) {
                    Log.e(TAG, "Exception in clearing output stream or url connection: " + e.getMessage());
                }
            }

        }
        return imageSaveSuccess;
    }


}
