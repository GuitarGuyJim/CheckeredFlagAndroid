package com.redskysoftware.checkeredflag.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.redskysoftware.checkeredflag.RaceResult;
import com.redskysoftware.checkeredflag.database.DatabaseSchema.ResultsTable;

import java.util.UUID;

public class ResultCursorWrapper extends CursorWrapper {

    public ResultCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public RaceResult getResult() {
        String raceIdString = getString(getColumnIndex(ResultsTable.Cols.EVENT));
        String driverIdString = getString(getColumnIndex(ResultsTable.Cols.DRIVER));
        int started = getInt(getColumnIndex(ResultsTable.Cols.STARTED));
        int finished = getInt(getColumnIndex(ResultsTable.Cols.FINISHED));
        int points = getInt(getColumnIndex(ResultsTable.Cols.POINTS));

        RaceResult result = new RaceResult(UUID.fromString(raceIdString),
                UUID.fromString(driverIdString),
                started,
                finished,
                points);

        return result;
    }
}
