package com.redskysoftware.checkeredflag;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.UUID;

/**
 * Displays the drivers in order of points.  This class uses an abstract function getDrivers() to
 * get the list of drivers and their points to display.  Child classes must implement the
 * getDrivers() function and determine what drivers are put in the list of drivers to display.
 */
public abstract class DriverStandingsFragment extends Fragment {

    /**
     * @return A ordered list of drivers to display on the fragment.  Drivers with higher point
     *         totals should be towards the beginning of the list.
     */
    protected abstract List<Driver> getDrivers();

    /** id of the event or other item related to why we are displaying the drivers */
    protected UUID mObjectId = null;

    private static final String ARG_OBJECT_ID =
            "com.redskysoftware.checkeredflag.object_id";

    /** The view that displays the list of drivers */
    private RecyclerView mDriverRecyclerView;
    private DriverStandingsFragment.DriverAdapter mAdapter;

    /**
     * Creates a Bundle object that can be used as args to a concrete DriverStandingsFragment obj.
     * @param objectId  The UUID of the driver/event/etc. that the fragment will work with
     * @return The Bundle that can be passed to the concrete fragment
     */
    public static Bundle createBundle(UUID objectId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_OBJECT_ID, objectId);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); //TODO....need this here?

        //
        // Get the bundle arguments and set the mObjectId value.
        //
        Bundle args = getArguments();
        if (args != null) {
            mObjectId = (UUID)args.getSerializable(ARG_OBJECT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_standings_list, container, false);

        mDriverRecyclerView = view.findViewById(R.id.driver_standings_recycler_view);
        mDriverRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // if (savedInstanceState != null) {
        //     mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        // }
        updateUI();

        return view;
    }

    /**
     *  Updates the data displayed in the view.
     */
    private void updateUI() {

        List<Driver> drivers = getDrivers();

        if (mAdapter == null) {
            mAdapter = new DriverAdapter(drivers);
            mDriverRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setDrivers(drivers);
            mAdapter.notifyDataSetChanged();
        }

        //  updateSubtitle();
    }

    /**
     * The DriverHolder class manages the displaying of data for a single driver in the view.
     */
    private class DriverHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        /** Displays the driver's position in the list */
        private TextView  mDriverPositionTextView;

        /** Displays the driver's points value */
        private TextView  mPointsTextView;

        /** Displays the driver's full name */
        private TextView  mDriverNameTextView;

        /** Displays the name of the team the driver is driving for */
        private TextView  mTeamNameTextView;

        /** The driver info being displayed by the Holder */
        private Driver    mDriver;

        /**
         * Constructor.  Sets up the text view objects by getting the correct resources.  It does
         * not populate the text view objects.  That is done in the bind() function.
         * @param inflater
         * @param parent
         */
        public DriverHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_driver_series, parent, false));
            itemView.setOnClickListener(this);

            mDriverPositionTextView = itemView.findViewById(R.id.driver_position);
            mDriverNameTextView = itemView.findViewById(R.id.driver_name);
            mTeamNameTextView = itemView.findViewById(R.id.driver_team);
            mPointsTextView = itemView.findViewById(R.id.driver_points);
        }

        /**
         * Populates the ViewHolder with data for the specified driver.
         * @param driver  The driver to display in the ViewHolder
         * @param position  The driver's position in the list
         */
        public void bind(Driver driver, int position) {
            mDriver = driver;

            mDriverPositionTextView.setText(String.format("%d.", position + 1));
            String driverName = mDriver.getFirstName() + " " + mDriver.getLastName();
            mDriverNameTextView.setText(driverName);
            mTeamNameTextView.setText(mDriver.getTeamName());
            mPointsTextView.setText(Integer.toString(mDriver.getPoints()));
        }

        @Override
        public void onClick(View view) {
            // We don't currently do anything when you click on a driver.
        }
    }

    private class DriverAdapter extends RecyclerView.Adapter<DriverHolder> {

        /** The list of drivers the adapter can display */
        private List<Driver> mDrivers;

        /**
         * Constructor
         * @param drivers  The list of drivers data to display
         */
        public DriverAdapter(List<Driver> drivers) {
            mDrivers = drivers;
        }

        @NonNull
        @Override
        public DriverHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new DriverHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull DriverHolder holder, int i) {
            holder.bind(mDrivers.get(i), i);
        }

        @Override
        public int getItemCount() {
            return mDrivers.size();
        }

        public void setDrivers(List<Driver> drivers) {
            mDrivers = drivers;
        }
    }
}
