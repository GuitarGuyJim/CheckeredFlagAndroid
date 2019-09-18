package com.redskysoftware.checkeredflag;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

/**
 *  This dialog allows the user to enter their finishing position in a race.
 */
public class FinishPositionDialog extends DialogFragment {

    public static final  String EXTRA_FINISH_POSITION = "com.redskysoftware.finish_position";

    private static final String NUM_DRIVERS = "NUM_DRIVERS";

    /** Num drivers in the series.  The user supplied finish position must be between 1 and this value. */
    int mNumDrivers;

    /** Finish position selected by the user */
    int mFinishPosition = 1;

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

        Spinner spinner = v.findViewById(R.id.position_spinner);
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < mNumDrivers; i++) {
            list.add(Integer.toString(i + 1));
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, list);
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                /* the position arg is 0 based, so add 1 to get the selected finish position */
                mFinishPosition = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });


        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.finish_position_title)
                .setPositiveButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //
                            // Need to send the finish position back to the target fragment
                            //
                            if (getTargetFragment() == null) {
                                return;
                            }

                            //
                            // create an intent and add the finish position to it
                            //
                            Intent intent = new Intent();
                            intent.putExtra(EXTRA_FINISH_POSITION, mFinishPosition);

                            // send the intent to the target fragment
                            getTargetFragment().onActivityResult(getTargetRequestCode(),
                                                                 Activity.RESULT_OK, intent);
                        }
                    })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }
}
