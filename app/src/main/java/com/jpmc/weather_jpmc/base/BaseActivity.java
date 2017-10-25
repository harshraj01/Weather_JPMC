package com.jpmc.weather_jpmc.base;


import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by Harsh Raj on 10/24/17.
 */

/**
 * BaseActivity which contains common implementation which can be used by all Activity class.
 */
public class BaseActivity extends ActionBarActivity {

    /**
     * Attach fragment to activity.
     * @param fragmentId
     * @param fragment
     */
    protected void commitFragment(int fragmentId, Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .add(fragmentId, fragment)
                .commit();
    }

    /**
     * Get existing fragment instance.
     * @param fragmentId
     * @return
     */
    protected Fragment getFragment(int fragmentId){
        return getSupportFragmentManager()
                .findFragmentById(fragmentId);
    }
}
