package com.jpmc.weather_jpmc.weather;

/**
 * Created by Harsh Raj on 10/25/17.
 */

/**
 *Callback for updating weather data and network error.
 */
public interface IUpdateWeather {
    /**
     * Called to get update from OpenWeatherMap server.
     * @param cityNameWithCountry
     */
    void updateWeather(String cityNameWithCountry);

    /**
     * Notify UI if there is network error.
     */
    void notifyNetworkError();

    /**
     * Notufy UI if there is location service error.
     */
    void notifyLocationServiceError();
}
