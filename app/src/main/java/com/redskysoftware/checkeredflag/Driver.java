package com.redskysoftware.checkeredflag;

import java.util.UUID;

/**
 * Information about a driver, including name, performance characteristics, and the UUID.
 */
public class Driver implements Comparable<Driver> {

    private String mFirstName;
    private String mLastName;

    /** The name of the team the driver drives for in this series */
    private String mTeamName;

    private int    mHighPerformance;
    private int    mLowPerformance;

    /**
     * The points this driver has.  This value is relative to the reason the Driver object exists.
     * For a single race, this is the points the driver earned in that race.  For a series standing,
     * this is the driver's current point total in that series.
     */
    private int    mPoints;

    private UUID   mId;

    public Driver(String firstName, String lastName, String teamName,
                  int highPerformance, int lowPerformance, int points, UUID id) {
        mFirstName = firstName;
        mLastName = lastName;
        mTeamName = teamName;
        mHighPerformance = highPerformance;
        mLowPerformance = lowPerformance;
        mPoints = points;
        mId = id;
    }

    public Driver(UUID id, String firstName, String lastName, int high, int low) {
        mId = id;
        mFirstName = firstName;
        mLastName = lastName;
        mHighPerformance = high;
        mLowPerformance = low;
        mPoints = 0;
        mTeamName = "";
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getTeamName() {
        return mTeamName;
    }

    public void setTeamName(String name) { mTeamName = name; }

    public int getHighPerformance() {
        return mHighPerformance;
    }

    public int getLowPerformance() {
        return mLowPerformance;
    }

    public int getPoints() {
        return mPoints;
    }
    public void setPoints(int value) { mPoints = value; }

    public UUID getId() {
        return mId;
    }

    @Override
    public int compareTo(Driver d) {
        if (mPoints < d.mPoints) {
            return 1;
        } else if (mPoints == d.mPoints) {
            return 0;
        } else {
            return -1;
        }
    }
}
