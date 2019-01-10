package com.redskysoftware.checkeredflag;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import java.util.UUID;

public class RaceResultsActivity extends AppCompatActivity {

    public static final String EXTRA_EVENT_ID =
            "com.redskysoftware.checkeredflag.event_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_results);

        UUID eventId = (UUID)getIntent().getSerializableExtra(EXTRA_EVENT_ID);

        //TODO get the name of the event using the ID and display it at the top of the activity

        FragmentManager fm = getSupportFragmentManager();

        RaceResultsFragment fragment = new RaceResultsFragment();
        Bundle args = RaceResultsFragment.createBundle(eventId);
        fragment.setArguments(args);

        fm.beginTransaction()
                .replace(R.id.race_results_fragment_container, fragment)
                .commit();
    }

    public static Intent newIntent(Context packageContext, UUID eventId) {
        Intent intent = new Intent(packageContext, RaceResultsActivity.class);
        intent.putExtra(EXTRA_EVENT_ID, eventId);
        return intent;
    }
}
