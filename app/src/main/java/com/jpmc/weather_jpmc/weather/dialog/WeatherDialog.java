package com.jpmc.weather_jpmc.weather.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.jpmc.weather_jpmc.Constants;


/**
 * Created by Harsh Raj on 10/25/17.
 */

/**
 * Weather dialog class which will take title and dialog message to display to user.
 * The idea is to make this dialog generic so that it can used by anywhere in project.
 */
public class WeatherDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString(Constants.dialogTitle);
        String titleMessage = getArguments().getString(Constants.dialogMessage);

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                        // Set Dialog Message
                .setMessage(titleMessage)
                        // Positive button
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                        getActivity().finish();
                    }
                }).create();
    }
}



