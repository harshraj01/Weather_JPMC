package com.jpmc.weather_jpmc.weather.view.fragment;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jpmc.weather_jpmc.R;
import com.jpmc.weather_jpmc.weather.IRefreshWeatherUI;
import com.jpmc.weather_jpmc.weather.model.CityWeatherModel;
import com.jpmc.weather_jpmc.weather.presenter.CityWeatherFragmentPresenter;


/**
 * Created by Harsh Raj on 10/24/17.
 */

/**
 * CityWeatherFragment is the fragment class which contains all the weather information UI.
 */
public class CityWeatherFragment extends Fragment implements IRefreshWeatherUI {

    private CityWeatherFragmentPresenter cityWeatherFragmentPresenter;

    /**
     * Sets the CityWeatherFragmentPresenter.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        cityWeatherFragmentPresenter = new CityWeatherFragmentPresenter(getActivity(), this);
        return inflater.inflate(R.layout.city_weather_ragment, container, false);
    }

    /**
     * Cancel the asyn task if it running.
     */
    @Override
    public void onPause() {
        super.onPause();
        cityWeatherFragmentPresenter.cancelAsyncTasks();
    }

    /**
     * Set CityWeatherFragmentPresenter to null.
     * To avoid memory leak since we are passing activyt context.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        cityWeatherFragmentPresenter = null;
    }

    /**
     * Call back when we receive weather data from server.
     *
     * Over here we can use EventBus with @subscribe annotation to ger event.
     * But since right we have only one place to update so it is not required.
     * @param cityWeatherModel
     */
    @Override
    public void refrestWeatherDataUI(CityWeatherModel cityWeatherModel) {
        View fragmentView = getView();
        if (fragmentView != null){
            TextView cityName = (TextView) fragmentView.findViewById(R.id.cityName);
            TextView weatherTemperature = (TextView) fragmentView.findViewById(R.id.weatherTemperature);
            TextView weatherMinMax = (TextView) fragmentView.findViewById(R.id.weatherMinMax);
            TextView weatherDescription = (TextView) fragmentView.findViewById(R.id.weatherDescription);

            weatherMinMax.setText(String.valueOf(cityWeatherModel.main.temp_min)+"/"+String.valueOf(cityWeatherModel.main.temp_max));
            cityName.setText(String.valueOf(cityWeatherModel.name));
            weatherTemperature.setText(String.valueOf(cityWeatherModel.main.temp)+"F");
            weatherDescription.setText(String.valueOf(cityWeatherModel.weather[0].description));

            //Once we get weather data from server then only we know the icon id of weather image.
            // Based on that icon id we do another server request to get the weather image.
            cityWeatherFragmentPresenter.requestWeatherImage(cityWeatherModel.weather[0].icon);
        }
    }

    /**
     * Uodate UI with weather icon.
     * @param iconId
     */
    @Override
    public void refrestWeatherIconUI(String iconId) {
        View fragmentView = getView();
        if (fragmentView != null){
            ImageView weatherImageView = (ImageView) fragmentView.findViewById(R.id.weatherIcon);
            weatherImageView.setImageBitmap(BitmapFactory.decodeFile(cityWeatherFragmentPresenter.getImageSaveLocation(iconId)));
        }
    }

    /**
     * Diplay error message if these is some issue fro server.
     * Right now displaying error message in toast.
     */
    @Override
    public void displayErrorMessage() {
        Toast.makeText(getActivity(), getResources().getString(R.string.error_message), Toast.LENGTH_SHORT);
    }

    /**
     * Called from WeatherActity to get weather data.
     * @param cityNameWithCountry
     */
    public void weatherUpdate(String cityNameWithCountry){
        cityWeatherFragmentPresenter.weatehrUpdate(cityNameWithCountry);
    }
}

