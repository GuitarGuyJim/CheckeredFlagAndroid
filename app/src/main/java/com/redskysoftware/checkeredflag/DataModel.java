package com.redskysoftware.checkeredflag;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import com.redskysoftware.checkeredflag.database.DatabaseHelper;
import com.redskysoftware.checkeredflag.database.DatabaseSchema;
import com.redskysoftware.checkeredflag.database.DatabaseSchema.EventTable;
import com.redskysoftware.checkeredflag.database.DatabaseSchema.ResultsTable;
import com.redskysoftware.checkeredflag.database.DatabaseSchema.SeasonTable;
import com.redskysoftware.checkeredflag.database.DatabaseSchema.TeamTable;
import com.redskysoftware.checkeredflag.database.DriverCursorWrapper;
import com.redskysoftware.checkeredflag.database.EventCursorWrapper;
import com.redskysoftware.checkeredflag.database.ResultCursorWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

public class DataModel {

    private static DataModel sModel;
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private List<RaceEvent> mCalendar;

    /**
     * The current series the data model is working with.  This will be set by the DataModel to be
     * the first series found in the database.  It can be set by the app using the setSeries()
     * method.
     */
    private UUID mSeries;

    public static DataModel get(Context context) {
        if (sModel == null) {
            sModel = new DataModel(context);
        }
        return sModel;
    }

    /**
     * Gets the events for the specified season
     * @return  A list of RaceEvent objects, one for each race in the season and in order of when
     *          the races occur.
     */
    public List<RaceEvent> getRaceEvents() {

        List<RaceEvent> events = new ArrayList<>();

        // For now we're cheating and assuming only one season in the db.  Get that season's ID
        CursorWrapper seasonCursor = new CursorWrapper(mDatabase.query(
                SeasonTable.NAME,
                null,
                null,
                null,
                null,
                null,
                null));

        UUID seasonId = null;

        try {
            seasonCursor.moveToFirst();
            seasonId = UUID.fromString(seasonCursor.getString(seasonCursor.getColumnIndex(SeasonTable.Cols.UUID)));

        } finally {
            seasonCursor.close();
        }

        EventCursorWrapper eventCursor =
                new EventCursorWrapper(mDatabase.query(EventTable.NAME,
                        null,
                        "season=?",
                        new String[] { seasonId.toString() } ,
                        null,
                        null,
                        null));

        try {

            eventCursor.moveToFirst();
            while (!eventCursor.isAfterLast()) {

                events.add(eventCursor.getRaceEvent());
                eventCursor.moveToNext();
            }
        } finally {
            eventCursor.close();
        }

        return events;
    }

    /**
     * Gets the drivers racing in the specified series
     * @param series  The series to get the drivers for
     * @return List of drivers in the specified series, ordered by current standings.  The points
     *         attribute of each Driver object is the total points the driver has earned so far in
     *         the season.
     */
    public List<Driver> getDriversInSeries(String series) {

        List<Driver> drivers = new ArrayList<>();

        //
        // First, get the teams in the specified series...
        //
        CursorWrapper teamCursor = new CursorWrapper(mDatabase.query(
                TeamTable.NAME,
                null,
                "series=?",
                new String[] { series },
                null,
                null,
                null));

        DriverCursorWrapper driverCursor = new DriverCursorWrapper(mDatabase.query(
                DatabaseSchema.DriverTable.NAME,
                null,  // all columns
                null,
                null,
                null,
                null,
                null));

        try {

            //
            // Loop over each team.  If the team is in the series, find the drivers for that team
            // and add them to the drivers list.
            //
            teamCursor.moveToFirst();
            while (!teamCursor.isAfterLast()) {

                //
                // The team is in the series we're interested in.  We'll look for drivers that
                // drive for this team.
                //
                int numDriversFound = 0;

                driverCursor.moveToFirst();

                //
                // Loop until we've run out of drivers or we've found the two drivers for the
                // current team.
                //
                while ( !driverCursor.isAfterLast() && (numDriversFound < 2) ) {

                    Driver driver = driverCursor.getDriver();

                    if ((teamCursor.getString(teamCursor.getColumnIndex(TeamTable.Cols.DRIVER1)).equals(driver.getId().toString())) ||
                        (teamCursor.getString(teamCursor.getColumnIndex(TeamTable.Cols.DRIVER2)).equals(driver.getId().toString()))) {

                        driver.setTeamName(teamCursor.getString(teamCursor.getColumnIndex(TeamTable.Cols.NAME)));
                        drivers.add(driver);
                        numDriversFound++;
                    }

                    driverCursor.moveToNext();
                }

                teamCursor.moveToNext();
            }
        } finally {
            teamCursor.close();
            driverCursor.close();
        }

        return drivers;
    }

    public void addResult(RaceEvent race, UUID driverId, int start, int finish, int points) {

        /* Insert row in the Results Table */
        mDatabase.insert(ResultsTable.NAME, null,
                getResultContentValues(race.getEventId(), driverId, start, finish, points));

        /* Mark the race as completed */
        mDatabase.update(EventTable.NAME,
                DatabaseHelper.getEventContentValues(race.getEventId(), race.getName(), race.getLocation(),
                        race.getSeriesId(), new GregorianCalendar(2019, 2, 30).getTime(), race.getRaceDate(), true),
                EventTable.Cols.UUID + "= ?",
                new String[] { race.getEventId().toString() } );
    }

    /**
     * Gets the results for the specified event.
     * @param eventId  The event to get the result for.
     * @return List of drivers in the specified event, ordered by finishing position.  The points
     *         attribute of each Driver object is the points earned in the event.
     */
    public List<Driver> getResultsForEvent(UUID eventId) {

        List<Driver> drivers = new ArrayList<>();
        ResultCursorWrapper resultsCursor2 = new ResultCursorWrapper(mDatabase.query(
                ResultsTable.NAME,
                null,
                null,
                null,
                null,
                null,
                null));

        try {

            //
            // We have a result item for each driver in the race.  Loop over the results, getting
            // the driver for each result.
            //
            resultsCursor2.moveToFirst();
            while (!resultsCursor2.isAfterLast()) {

                RaceResult result = resultsCursor2.getResult();

                resultsCursor2.moveToNext();
            }
        } finally {
            resultsCursor2.close();
        }

        ResultCursorWrapper resultsCursor = new ResultCursorWrapper(mDatabase.query(
                ResultsTable.NAME,
                null,
                "event=?",
                new String[] { eventId.toString() },
                null,
                null,
                null));

        try {

            //
            // We have a result item for each driver in the race.  Loop over the results, getting
            // the driver for each result.
            //
            resultsCursor.moveToFirst();
            while (!resultsCursor.isAfterLast()) {

                RaceResult result = resultsCursor.getResult();

                //
                // Get the driver this result is for...
                //
                DriverCursorWrapper driverCursor = new DriverCursorWrapper(mDatabase.query(
                        DatabaseSchema.DriverTable.NAME,
                        null,
                        "uuid=?",
                        new String[] { result.getDriverId().toString() },
                        null,
                        null,
                        null));

                driverCursor.moveToFirst();
                if (driverCursor.getCount() == 1) {

                    Driver driver = driverCursor.getDriver();
                    driver.setPoints(result.getPoints());
                    drivers.add(driver);
                }

                driverCursor.close();
                resultsCursor.moveToNext();
            }
        } finally {
            resultsCursor.close();
        }

        /* Sort the drivers so the highest points are first in the list */
        Collections.sort(drivers);

        return drivers;
    }

    /**
     * Constructor.
     * @param context
     */
    private DataModel(Context context) {
        mContext = context.getApplicationContext();

        mDatabase = new DatabaseHelper(mContext).getWritableDatabase();

        mCalendar = new ArrayList<>();

        Date date = new GregorianCalendar(2019, 2, 7).getTime();
        RaceEvent event = new RaceEvent("Australian GP", new UUID(0,1), date, "Melbourne", true, UUID.randomUUID());
        mCalendar.add(event);

        date = new GregorianCalendar(2019, 3, 7).getTime();
        event = new RaceEvent("Long Beach GP", new UUID(0,1), date, "USA", false, UUID.randomUUID());
        mCalendar.add(event);

        date = new GregorianCalendar(2019, 4, 7).getTime();
        event = new RaceEvent("Monte Carlo GP", new UUID(0,1), date, "Monaco", false, UUID.randomUUID());
        mCalendar.add(event);

        mSeries = UUID.randomUUID();
        //end TODO temp
    }

    private String getTeamName(UUID id) {

        String teamName = null;

        CursorWrapper cursor = new CursorWrapper(mDatabase.query(
                TeamTable.NAME,
                null,  // all columns
                null,
                null,
                null,
                null,
                null));

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                if ((cursor.getString(cursor.getColumnIndex(TeamTable.Cols.DRIVER1)).equals(id.toString())) ||
                    (cursor.getString(cursor.getColumnIndex(TeamTable.Cols.DRIVER2)).equals(id.toString())))
                {
                    teamName = cursor.getString(cursor.getColumnIndex(TeamTable.Cols.NAME));
                    break;
                }

                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return teamName;
    }

    private ContentValues getResultContentValues(UUID raceId, UUID driverId, int start,
                                                 int finish, int points) {

        ContentValues values = new ContentValues();
        values.put(ResultsTable.Cols.UUID, UUID.randomUUID().toString());
        values.put(ResultsTable.Cols.EVENT, raceId.toString());
        values.put(ResultsTable.Cols.DRIVER, driverId.toString());
        values.put(ResultsTable.Cols.STARTED, start);
        values.put(ResultsTable.Cols.FINISHED, finish);
        values.put(ResultsTable.Cols.POINTS, points);

        return values;
    }
}
