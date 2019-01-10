package com.redskysoftware.checkeredflag;

import java.util.List;

public class RaceResultsFragment extends DriverStandingsFragment {

    @Override
    protected List<Driver> getDrivers() {
        DataModel model = DataModel.get(getActivity());
        return model.getResultsForEvent(mObjectId);
    }
}