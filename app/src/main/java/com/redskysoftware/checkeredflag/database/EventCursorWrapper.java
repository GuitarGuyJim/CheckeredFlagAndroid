package com.redskysoftware.checkeredflag.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.redskysoftware.checkeredflag.RaceEvent;
import com.redskysoftware.checkeredflag.database.DatabaseSchema.EventTable;

import java.util.Date;
import java.util.UUID;

public class EventCursorWrapper extends CursorWrapper {

    public EventCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public RaceEvent getRaceEvent() {
        String eventId = getString(getColumnIndex(EventTable.Cols.UUID));
        String seriesId = getString(getColumnIndex(EventTable.Cols.SEASON));
        String name = getString(getColumnIndex(EventTable.Cols.NAME));
        String location = getString(getColumnIndex(EventTable.Cols.LOCATION));
        long raceDate = getLong(getColumnIndex(EventTable.Cols.RACE_DATE));
        int completed = getInt(getColumnIndex(EventTable.Cols.COMPLETED));

        RaceEvent event = new RaceEvent(name, UUID.fromString(seriesId), new Date(raceDate),
                                        location, completed != 0, UUID.fromString(eventId));

        return event;
    }
}
