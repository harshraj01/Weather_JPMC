package com.jpmc.weather_jpmc.backgroundtask;

import android.os.AsyncTask;

import com.jpmc.weather_jpmc.service.openweathermapservice.MapService;

import java.io.File;


/**
 * Created by Harsh Raj on 10/24/17.
 */

/**
 * This class should be ServiceIntent and not AsyncTask.
 * Since the image download can be long running operation, it is better and recommended to use IntentService.
 * Asynchronous task for getting weather image from server.
 *
 *
 * Should use EventBus if we have to notify weather image to more that 1 places.
 * For eg., if there are two fragment on main screen which requires to be updated when we
 * get the weather image from server. EvenBus use publish/subscribe architecture.
 * First of all we have to define event, in this case for eg., it will be weatherImageUpdate.
 * The classes have to first subscribe for the event "weatherImageUpdate" through EventBus.
 * Then through EventBus over here in this it will notify about the weather image received from server.
 *
 * Using broadcast receiver is also the option but
 * it is heavy operation and causes a lot of system overhead.
 */
public class WeatherImageAsyncTask extends AsyncTask<String,Void,Boolean> {
    private IWeatherImageCallBack weatherImageCallBack;

    /**
     * WeatherImageAsyncTask constructor.
     * @param weatherImageCallBack Set the callback to get the image result from server.
     */
    public WeatherImageAsyncTask(IWeatherImageCallBack weatherImageCallBack){
        this.weatherImageCallBack = weatherImageCallBack;

    }

    /**
     * Runs on background thread to do long running task.
     * Checks is image is already present in save location provided. If not then request from server
     * to get the weather image.
     * @param params iconId and image save location.
     * @return
     */
    @Override
    protected Boolean doInBackground(String... params) {
        String iconId = params[0];
        String imageSaveLocation = params[1];
        File imageFile = new File(imageSaveLocation);
        boolean imageSaveSuccess = false;
        if (!imageFile.exists()){
            imageSaveSuccess = new MapService().saveWeatherImage(iconId, imageSaveLocation);
        }else{
            imageSaveSuccess = true;
        }
        return imageSaveSuccess;
    }

    /**
     * Runs on UI thread.
     * Get the boolean result after image is successfully saved to provided location or not..
     * @param imageSaveSuccess
     */
    @Override
    protected void onPostExecute(Boolean imageSaveSuccess) {
        super.onPostExecute(imageSaveSuccess);
        if (!isCancelled()){
            weatherImageCallBack.weatherImage(imageSaveSuccess);
        }
    }
}

