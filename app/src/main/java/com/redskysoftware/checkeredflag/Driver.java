package com.redskysoftware.checkeredflag;

import java.util.UUID;

/**
 * Information about a driver, including name, performance characteristics, and the UUID.
 */
public class Driver implements Comparable<Driver> {

    /** The driver's first name */
    private String mFirstName;

    /** The driver's last name */
    private String mLastName;

    /** The name of the team the driver drives for in this series */
    private String mTeamName;

    /**
     * The low and high performance values indicate a range of performance.  Values should be
     * between 0 and 100, with the low value being <= to the high value.  low/high ranges that
     * are small define a more consistent driver.
     */
    private int    mHighPerformance;
    private int    mLowPerformance;

    /**
     * The points this driver has.  This value is relative to the reason the Driver object exists.
     * For a single race, this is the points the driver earned in that race.  For a series standing,
     * this is the driver's current point total in that series.
     */
    private int    mPoints;

    /** Unique id for the driver */
    private UUID   mId;

    /**
     * Constructor
     * @param firstName  Driver's first name
     * @param lastName   Driver's last name
     * @param teamName   The name of the team the driver is driving for.
     * @param highPerformance  Low performance value for the driver
     * @param lowPerformance   High performance value for the driver
     * @param points     Points value to assign to the driver
     * @param id   Unique id for the driver
     */
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

    /**
     * Constructor
     * @param id  Unique id for the driver
     * @param firstName  Driver's first name
     * @param lastName  Driver's last name
     * @param high  High performance value for the driver
     * @param low   Low performance value for the driver
     */
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
