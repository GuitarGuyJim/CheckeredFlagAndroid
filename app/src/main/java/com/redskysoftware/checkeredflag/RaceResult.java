package com.redskysoftware.checkeredflag;

import java.util.UUID;

public class RaceResult {

    private UUID mEventId;
    private UUID mDriverId; // The id of the driver this result is for
    private int  mStarted;  // starting position, -1 to indicate not recorded
    private int  mFinished; // finishing position, -1 to indicate DNF
    private int  mPoints;   // points earned in the event

    public RaceResult(UUID id, UUID driverId, int started, int finished, int points) {
        mEventId = id;
        mDriverId = driverId;
        mStarted = started;
        mFinished = finished;
        mPoints = points;
    }

    public UUID getEventId() {
        return mEventId;
    }

    public UUID getDriverId() {
        return mDriverId;
    }

    public int getStarted() {
        return mStarted;
    }

    public int getFinished() {
        return mFinished;
    }

    public int getPoints() {
        return mPoints;
    }
}
