package com.redskysoftware.checkeredflag;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

/**
 *  This dialog allows the user to enter their finishing position in a race.
 */
public class FinishPositionDialog extends DialogFragment {

    private static final String NUM_DRIVERS = "NUM_DRIVERS";

    /** Num drivers in the series.  The user supplied finish position must be between 1 and this value. */
    int mNumDrivers;

    /**
     *  Creates an instance of the dialog, putting the number of drives in the series in the bundle
     *  args that will be passed to the onCreateDialog() method.
     * @param numDrivers  Number of drivers in the series
     * @return  New instance of the dialog
     */
    public static FinishPositionDialog newInstance(int numDrivers) {

        // Stash the number of drivers in the series in a Bundle
        Bundle args = new Bundle();
        args.putSerializable(NUM_DRIVERS, numDrivers);

        // Create a new dialog object, setting its args to the bundle we just created
        FinishPositionDialog dialog = new FinishPositionDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mNumDrivers = (int)getArguments().getSerializable(NUM_DRIVERS);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_finish_position, null);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.finish_position_title)
                .setPositiveButton(android.R.string.ok, null) //TODO add listener
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }
}
