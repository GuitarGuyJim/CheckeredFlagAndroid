package com.redskysoftware.checkeredflag;

import java.util.List;

public class DriverStandingsSeriesFragment extends DriverStandingsFragment {

    @Override
    protected List<Driver> getDrivers() {
        DataModel model = DataModel.get(getActivity());
        return model.getDriversInSeries("Formula A");  //TODO need to pass series name to the fragment...
    }
}
