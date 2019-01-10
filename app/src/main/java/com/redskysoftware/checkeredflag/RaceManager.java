package com.redskysoftware.checkeredflag;

import android.content.Context;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RaceManager {

    private DataModel mDataModel;

    /**
     * Constructor.
     */
    public RaceManager(Context context) {

        mDataModel = DataModel.get(context);
    }

    void runRace(RaceEvent race) {

        /*
         * Get the drivers in this race (we'll get the drivers for the series the race is in).
         * The point value of each driver returned will be the driver's total points in the season.
         * We're going to replace that value with the points earned in the race.
         */
        List<Driver> drivers = mDataModel.getDriversInSeries("Formula A");

        Random random = new Random();

        /*
         * Loop over each driver and calculate their performance.  Store the performance in the
         * driver's points attribute.
         */
        for (Driver driver : drivers) {
            if ((driver.getLowPerformance() > 0) && (driver.getHighPerformance() > 0)) {
                driver.setPoints(random.nextInt(driver.getHighPerformance() - driver.getLowPerformance()) + driver.getLowPerformance());
            }
        }

        /*
         * Now sort the drivers based on the points.
         */
        Collections.sort(drivers);

        /*
         * Now add a race result for each driver
         */
        int index = 0;
        int[] points = { 25, 18, 15, 12, 10, 8, 6, 4, 2, 1 };

        for (Driver driver : drivers) {

            int pointsForRace = 0;
            if (index < points.length) {
                pointsForRace = points[index++];
            }
            mDataModel.addResult(race, driver.getId(), 0, driver.getPoints(), pointsForRace);
        }
    }
}
