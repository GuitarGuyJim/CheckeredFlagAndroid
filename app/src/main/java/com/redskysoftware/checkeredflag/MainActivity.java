package com.redskysoftware.checkeredflag;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.UUID;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        //
        // Setup the spinner at the top of the activity that lets the user select what type of
        // list view they want to see.  As part of this creation, the onItemSelected callback
        // for the spinner will be called.  When that happens, we'll populate the activity with
        // the correct fragment to start with.
        //
        Spinner spinner = findViewById(R.id.fragment_type_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                                 R.array.fragment_list_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        // Set this activity as the callback listener for the spinner
        spinner.setOnItemSelectedListener(this);
    }

    /**
     * Implements the method defined in the OnItemSelectedListener interface.  It puts the correct
     * fragment on the display based on what item was selected in the spinner.
     * @param parent
     * @param view
     * @param pos
     * @param id
     */
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {

        if (pos == 0)
        {
            FragmentManager fm = getSupportFragmentManager();

            fm.beginTransaction()
                    .replace(R.id.fragment_container, new RaceCalendarFragment())
                    .commit();

        } else if (pos == 1) {
            FragmentManager fm = getSupportFragmentManager();

            //
            // We want to display a DriversStandingsSeriesFragment.  That fragment needs the UUID
            // of the series.  We'll pass the series id to the fragment as a fragment argument.
            //
            DriverStandingsFragment fragment = new DriverStandingsSeriesFragment();
            Bundle args = DriverStandingsFragment.createBundle(UUID.randomUUID()); //TODO needs to be the series ID
            fragment.setArguments(args);

            fm.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        } else {
            Toast.makeText(getApplicationContext(), "Unsupported option", Toast.LENGTH_SHORT).show();
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
