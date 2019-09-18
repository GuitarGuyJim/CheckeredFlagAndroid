package com.redskysoftware.checkeredflag;

import android.content.Context;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RaceManager {

    private DataModel mDataModel;

    /** User's first name, used to find their Driver object */
    private String mUserFirstName = "Jim";

    /** User's last name, used to find their Driver object */
    private String mUserLastName = "Kalinowski";

    /**
     * Constructor.
     */
    public RaceManager(Context context) {

        mDataModel = DataModel.get(context);
    }

    /**
     * Simulates a race
     * @param race  The race event to simulate
     * @param finishPosition  The finish position of the user
     */
    void runRace(RaceEvent race, int finishPosition) {

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
            if ((driver.getFirstName().equals(mUserFirstName)) &&
                    (driver.getLastName().equals(mUserLastName))) {
                // This is the user's driver, so set the performance to 0 for now
                driver.setPoints(0);
            }
            else if ((driver.getLowPerformance() > 0) && (driver.getHighPerformance() > 0)) {
                driver.setPoints(random.nextInt(driver.getHighPerformance() - driver.getLowPerformance()) + driver.getLowPerformance());
            }
        }

        /*
         * Now sort the drivers based on the performance.
         */
        Collections.sort(drivers);

        /*
         * The user's driver is last.  Get that driver object, then remove it.  Then we'll
         * insert it in the correct finish position.
         */
        Driver user = drivers.get(drivers.size() - 1);
        drivers.remove(drivers.size() - 1);
        drivers.add(finishPosition - 1, user);

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
