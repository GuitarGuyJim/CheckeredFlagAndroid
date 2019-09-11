package com.redskysoftware.checkeredflag.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.redskysoftware.checkeredflag.Driver;
import com.redskysoftware.checkeredflag.database.DatabaseSchema.DriverTable;

import java.util.UUID;

public class DriverCursorWrapper extends CursorWrapper {

    public DriverCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Driver getDriver() {
        String uuidString = getString(getColumnIndex(DriverTable.Cols.UUID));
        String firstName = getString(getColumnIndex(DriverTable.Cols.FIRST_NAME));
        String lastName = getString(getColumnIndex(DriverTable.Cols.LAST_NAME));
        int high = getInt(getColumnIndex(DriverTable.Cols.HIGH));
        int low = getInt(getColumnIndex(DriverTable.Cols.LOW));

        Driver driver = new Driver(UUID.fromString(uuidString), firstName, lastName, high, low);

        return driver;
    }
}
