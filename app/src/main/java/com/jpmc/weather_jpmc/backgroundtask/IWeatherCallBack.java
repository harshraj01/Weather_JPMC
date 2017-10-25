package com.jpmc.weather_jpmc.backgroundtask;


/**
 * Created by Harsh Raj on 10/24/17.
 */

import com.jpmc.weather_jpmc.weather.model.CityWeatherModel;

/**
 * Callback for getting weather data from server.
 */
public interface IWeatherCallBack {
    /**
     * Get the updated weather data.
     * @param cityWeatherModel
     */
    void weatherData(CityWeatherModel cityWeatherModel);
}
