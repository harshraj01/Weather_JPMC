package com.jpmc.weather_jpmc.weather;


import com.jpmc.weather_jpmc.weather.model.CityWeatherModel;

/**
 * Created by Harsh Raj on 10/25/17.
 */
public interface IRefreshWeatherUI {
    /**
     * Refresh UI with weather data fetched from server.
     * @param cityWeatherModel
     */
    void refrestWeatherDataUI(CityWeatherModel cityWeatherModel);

    /**
     * Refresh UI with weather image fetched from server.
     * @param iconId
     */
    void refrestWeatherIconUI(String iconId);

    /**
     * Display error message if there is some error from OpenWeatherMap server.
     */
    void displayErrorMessage();
}
