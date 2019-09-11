package com.redskysoftware.checkeredflag;

import java.util.Date;
import java.util.UUID;

/**
 * RaceEvent contains the information for a single race event.
 */
public class RaceEvent {

    /** The event name */
    private String mName;

    /** The racing series this event is part of */
    private UUID mSeriesId;

    /** The date of the face */
    private Date mRaceDate;

    /** The location of the event */
    private String mLocation;

    /** True if the event has been completed */
    private boolean mCompleted;

    /** Unique id for the event */
    private UUID mEventId;

    /**
     * Constructor
     * @param name
     * @param series
     * @param raceDate
     * @param location
     * @param completed
     */
    public RaceEvent(String name, UUID series, Date raceDate, String location, boolean completed,
                     UUID eventId) {
        mName = name;
        mSeriesId = series;
        mRaceDate = raceDate;
        mLocation = location;
        mCompleted = completed;
        mEventId = eventId;
    }

    public boolean isCompleted() {
        return mCompleted;
    }

    public void setCompleted(boolean completed) {
        mCompleted = completed;
    }

    public String getName() {
        return mName;
    }

    public UUID getSeriesId() {
        return mSeriesId;
    }

    public Date getRaceDate() {
        return mRaceDate;
    }

    public String getLocation() {
        return mLocation;
    }

    public UUID getEventId() { return mEventId; }
}
