package com.redskysoftware.checkeredflag;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * This RaceCalendarFragment displays a race calendar as a list of RaceEvent objects.
 */
public class RaceCalendarFragment extends Fragment {

    private static final String FINISH_POSITION_DIALOG = "DialogFinishPosition";

    private RecyclerView mCalendarRecyclerView;
    private RaceAdapter  mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setHasOptionsMenu(true); //TODO....need this here?
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_race_calendar_list, container, false);

        mCalendarRecyclerView = view.findViewById(R.id.race_calendar_recycler_view);
        mCalendarRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

       // if (savedInstanceState != null) {
       //     mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
       // }
        updateUI();

        return view;
    }

    private void updateUI() {
        DataModel model = DataModel.get(getActivity());
        List<RaceEvent> events = model.getRaceEvents();

        if (mAdapter == null) {
            mAdapter = new RaceAdapter(events);
            mCalendarRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setEvents(events);
            mAdapter.notifyDataSetChanged();
        }

      //  updateSubtitle();
    }

    private class RaceHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mCompletedImageView;
        private RaceEvent    mEvent;

        public RaceHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_race_event, parent, false));
            itemView.setOnClickListener(this);

            mTitleTextView = itemView.findViewById(R.id.event_title);
            mDateTextView = itemView.findViewById(R.id.event_date);
            mCompletedImageView = itemView.findViewById(R.id.event_completed);
        }

        public void bind(RaceEvent event) {
            mEvent = event;
            mTitleTextView.setText(mEvent.getName());
            mDateTextView.setText(mEvent.getRaceDate().toString());
            mCompletedImageView.setVisibility(mEvent.isCompleted() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View view) {
            //
            // A race in the calendar was clicked.  If the race was completed, show the race
            // results, otherwise, show the dialog that lets the user run the race.
            //
            if (mEvent.isCompleted()) {
                Intent intent = RaceResultsActivity.newIntent(getActivity(), mEvent.getEventId());
                startActivity(intent);
            } else {

                //
                // The event is not completed, so run it now.  First, we need to ask the user
                // what position they finished in.  We'll want to bounds check their answer, so
                // get the list of drivers in the current series.  This will tell us how many
                // drivers are in the series (and the upper bound).
                //
                List<Driver> drivers = DataModel.get(getActivity()).getDriversInSeries("Formula A");

                /*
                 * Create a FinishPositionDialog, passing in the number of drivers in the series
                 */
                FragmentManager manager = getFragmentManager();
                FinishPositionDialog dialog = FinishPositionDialog.newInstance(drivers.size());
                dialog.show(manager, FINISH_POSITION_DIALOG);

                /*
                //TODO move this code to where the finish position dialog sends its data back
                RaceManager raceManager = new RaceManager(getContext());
                raceManager.runRace(mEvent);

                // Display the race results
                Intent intent = RaceResultsActivity.newIntent(getActivity(), mEvent.getEventId());
                startActivity(intent);
                 */
            }
        }
    }

    private class RaceAdapter extends RecyclerView.Adapter<RaceHolder> {
        private List<RaceEvent> mEvents;

        public RaceAdapter(List<RaceEvent> events) {
            mEvents = events;
        }


        @NonNull
        @Override
        public RaceHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new RaceHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull RaceHolder raceHolder, int i) {
            raceHolder.bind(mEvents.get(i));
        }

        @Override
        public int getItemCount() {
            return mEvents.size();
        }

        public void setEvents(List<RaceEvent> events) {
            mEvents = events;
        }
    }
}
