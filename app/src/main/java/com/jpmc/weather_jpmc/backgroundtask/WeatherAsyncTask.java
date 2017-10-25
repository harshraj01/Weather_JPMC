package com.jpmc.weather_jpmc.backgroundtask;

import android.os.AsyncTask;

import com.jpmc.weather_jpmc.service.openweathermapservice.MapService;
import com.jpmc.weather_jpmc.weather.model.CityWeatherModel;


/**
 * Created by Harsh Raj on 10/24/17.
 */

/**
 * Asynchronous task for getting weather data from server.
 *
 * Should use EventBus if we have to notify weather data to more that 1 places.
 * For eg., if there are two fragment on main screen which requires to be updated when we
 * get the weather data from server. EvenBus use publish/subscribe architecture.
 * First of all we have to define event, in this case for eg., it will be weatherUpdate.
 * The classes have to first subscribe for the event "weatherUpdate" through EventBus.
 * Then through EventBus over here in this it will notify about the weather data received from server.
 *
 * Using broadcast receiver is also the option but
 * it is heavy operation and causes a lot of system overhead.
 */
public class WeatherAsyncTask extends AsyncTask<String,Void,CityWeatherModel> {

    private IWeatherCallBack weatherCallBack;

    /**
     * WeatherAsyncTask constructor.
     * @param weatherCallBack set the callback for getting notified for weather data.
     */
    public WeatherAsyncTask(IWeatherCallBack weatherCallBack){
        this.weatherCallBack = weatherCallBack;
    }

    /**
     * Runs on background thread to do long running task.
     * Call the MapService to get the weather data from server.
     * @param params
     * @return
     */
    @Override
    protected CityWeatherModel doInBackground(String... params) {
        if (isCancelled()) return null;
        return new MapService().getWeatherData(params[0]);
    }

    /**
     * Runs on UI thread.
     * Gets the result from server with weather data.
     * If the CityWeatherModel is null then then there is some some issue in getting data from server.
     * @param cityWeatherModel
     */
    @Override
    protected void onPostExecute(CityWeatherModel cityWeatherModel) {
        super.onPostExecute(cityWeatherModel);
        if (!isCancelled()){
            weatherCallBack.weatherData(cityWeatherModel);
        }
    }
}

