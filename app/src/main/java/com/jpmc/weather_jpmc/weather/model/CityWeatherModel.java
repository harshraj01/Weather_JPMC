package com.jpmc.weather_jpmc.weather.model;

/**
 * Created by Harsh Raj on 10/24/17.
 */

/**
 * Model class of corresponding JSON revceived from OpenWeatherMap.
 * There will lot more weather properties which should present but right now I have
 * taken less properties due to time constraint.
 */
public class CityWeatherModel {

    public WeatherModel[] weather; // contain id, description, icon and main object.
    public WeatherMainModel main; // main model which contain temp., pressure, humidity, min temp, max temp, sea level and ground level.
    public String name; // Name of the city to get the weather
    public Integer cod; // Successful or unsuccessful response code.
}