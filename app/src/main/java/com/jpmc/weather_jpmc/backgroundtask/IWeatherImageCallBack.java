package com.jpmc.weather_jpmc.backgroundtask;

/**
 * Created by Harsh Raj on 10/24/17.
 */

/**
 * Callback for getting weather image from server.
 */
public interface IWeatherImageCallBack {
    /**
     * Get the image result.
     * @param imageSaveSuccess
     */
    void weatherImage(boolean imageSaveSuccess);
}
