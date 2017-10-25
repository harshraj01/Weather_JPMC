package com.jpmc.weather_jpmc.weather.presenter;

import android.app.Activity;
import android.support.annotation.VisibleForTesting;

import com.jpmc.weather_jpmc.backgroundtask.IWeatherCallBack;
import com.jpmc.weather_jpmc.backgroundtask.IWeatherImageCallBack;
import com.jpmc.weather_jpmc.backgroundtask.WeatherAsyncTask;
import com.jpmc.weather_jpmc.backgroundtask.WeatherImageAsyncTask;
import com.jpmc.weather_jpmc.weather.IRefreshWeatherUI;
import com.jpmc.weather_jpmc.weather.model.CityWeatherModel;


/**
 * Created by Harsh Raj on 10/25/17.
 */

/**
 * This class is reponsible for starting asyn task to get weather data and image from OpenWeatherMap server.
 */
public class CityWeatherFragmentPresenter implements IWeatherCallBack, IWeatherImageCallBack {


    private WeatherAsyncTask weatherAsyncTask;
    private WeatherImageAsyncTask weatherImageAsyncTask;
    private IRefreshWeatherUI refreshWeatherUI;
    private Activity activity;
    private String imageId;

    /**
     * CityWeatherFragmentPresenter constructor.
     * @param activity
     * @param refreshWeatherUI weather data recceived the async task.
     */
    public CityWeatherFragmentPresenter(Activity activity, IRefreshWeatherUI refreshWeatherUI){
        this.activity = activity;
        this.refreshWeatherUI = refreshWeatherUI;
    }

    /**
     * Start asyn task to get the weather update from server.
     * @param cityNameWithCountry
     */
    public void weatehrUpdate(String cityNameWithCountry) {
        cancelAsyncTasks();
        weatherAsyncTask = new WeatherAsyncTask(this);
        weatherAsyncTask.execute(cityNameWithCountry);
    }

    /**
     * Cancel Aync task.If it is already running then it won't stop if this tread is running.
     */
    public void cancelAsyncTasks() {
        if (weatherAsyncTask != null){
            weatherAsyncTask.cancel(false);
            weatherAsyncTask = null;
        }
        if (weatherImageAsyncTask != null){
            weatherImageAsyncTask.cancel(false);
            weatherImageAsyncTask = null;
        }
    }

    /**
     * Get image save location.
     * @param icon
     * @return
     */
    public String getImageSaveLocation(String icon){
        return activity.getFilesDir().getPath() + "/weatherImage" + icon + ".png";
    }

    /**
     * Get weather data from server.
     * Over here we can use EventBus.
     * We can use @subscribe annotation to get the async data.
     * But EventBus is useful when there are more that one place which are listening to data.
     * @param cityWeatherModel
     */
    @Override
    public void weatherData(CityWeatherModel cityWeatherModel) {
        if(cityWeatherModel == null || cityWeatherModel.cod != 200){
            refreshWeatherUI.displayErrorMessage();
            return;
        }
        refreshWeatherUI.refrestWeatherDataUI(cityWeatherModel);
    }

    /**
     * Get weather image from server.
     * Over here we can use EventBus.
     * We can use @subscribe annotation to get the async data.
     * But EventBus is useful when there are more that one place which are listening to data.
     * @param imageSaveSuccess
     */
    @Override
    public void weatherImage(boolean imageSaveSuccess) {
        if(imageSaveSuccess){
            refreshWeatherUI.refrestWeatherIconUI(imageId);
        }else {
            refreshWeatherUI.displayErrorMessage();
        }
    }

    /**
     * Call to aync task to get the weather image from server.
     * @param icon
     */
    public void requestWeatherImage(String icon){
        imageId = icon;
        weatherImageAsyncTask = new WeatherImageAsyncTask(this);
        weatherImageAsyncTask.execute(icon, getImageSaveLocation(icon));
    }

    @VisibleForTesting
    public void setImageId(String imageId){
        this.imageId = imageId;
    }
}
