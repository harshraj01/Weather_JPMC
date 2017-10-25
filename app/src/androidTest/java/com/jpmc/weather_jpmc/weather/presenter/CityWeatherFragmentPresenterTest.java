package com.jpmc.weather_jpmc.weather.presenter;

import android.app.Activity;
import android.test.AndroidTestCase;

import com.jpmc.weather_jpmc.weather.IRefreshWeatherUI;
import com.jpmc.weather_jpmc.weather.model.CityWeatherModel;

import java.io.File;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Harsh Raj on 10/25/17.
 */
public class CityWeatherFragmentPresenterTest extends AndroidTestCase {

    CityWeatherFragmentPresenter cityWeatherFragmentPresenter;
    Activity activity;
    IRefreshWeatherUI refreshWeatherUI;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        activity = mock(Activity.class);
        refreshWeatherUI = mock(IRefreshWeatherUI.class);
        setCityWeatherFragmentPresenter();
    }

    private void setCityWeatherFragmentPresenter(){
        cityWeatherFragmentPresenter = new CityWeatherFragmentPresenter(activity, refreshWeatherUI);
    }

    public void test_CityWeatherFragmentPresenter_Ctor(){
        assertNotNull(cityWeatherFragmentPresenter);
    }

    public void test_getImageSaveLocation(){
        File file = mock(File.class);
        when(activity.getFilesDir()).thenReturn(file);
        when(activity.getFilesDir().getPath()).thenReturn("test");
        assertNotNull(cityWeatherFragmentPresenter.getImageSaveLocation("10"));
        String saveLocation = cityWeatherFragmentPresenter.getImageSaveLocation("10");
        assertTrue(saveLocation.contains("10"));
    }

    public void test_weatherData(){
        CityWeatherModel cityWeatherModel = mock(CityWeatherModel.class);
        cityWeatherModel.cod = 200;
        cityWeatherFragmentPresenter.weatherData(cityWeatherModel);
        verify(refreshWeatherUI, times(1)).refrestWeatherDataUI(cityWeatherModel);
    }

    public void test_weatherData_NullCityWeatherModel(){
        cityWeatherFragmentPresenter.weatherData(null);
        verify(refreshWeatherUI, times(1)).displayErrorMessage();
    }

    public void test_weatherImage_ImageSaveSuccess_True(){
        String imageId = "test01";
        cityWeatherFragmentPresenter.setImageId(imageId);
        cityWeatherFragmentPresenter.weatherImage(true);
        verify(refreshWeatherUI, times(1)).refrestWeatherIconUI(imageId);
    }

    public void test_weatherImage_ImageSaveSuccess_False(){
        cityWeatherFragmentPresenter.weatherImage(false);
        verify(refreshWeatherUI, times(1)).displayErrorMessage();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
