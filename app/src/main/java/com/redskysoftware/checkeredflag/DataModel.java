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

/**
 * Manages the driver, race, and team data in the application.  This implementation stores data
 * in a SQLite database (see the database.DatabaseSchema class for the schema definition).  This
 * class is a singleton.
 */
public class DataModel {

    /** The singleton instance of the DataModel */
    private static DataModel sModel;

    /** The database that contains the application data */
    private SQLiteDatabase mDatabase;

    /**
     * The current series the data model is working with.  This will be set by the DataModel to be
     * the first series found in the database.  It can be set by the app using the setSeries()
     * method.
     */
    private UUID mSeries;

    /**
     * Gets the singleton instance of the DataModel, creating one if one doesn't exist.
     * @param context  The application context, used to initialize the database connection.
     * @return  The single instance of the DataModel.
     */
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

        UUID seasonId;

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

        //
        // At this point the drivers list contains the drivers in the series.  Now we need to get
        // the results for each driver and total up their points for the season.
        //
        for (Driver driver : drivers) {

            List<RaceResult> results = getResultsForDriverForSeason(driver.getId());

            driver.setPoints(0);

            for (RaceResult result : results) {
                driver.setPoints(driver.getPoints() + result.getPoints());
            }
        }

        Collections.sort(drivers);

        return drivers;
    }

    /**
     * Adds a race result to the data model.
     * @param race  Race event the result is for
     * @param driverId  The ID of the driver this result is for.
     * @param start     The driver's starting position
     * @param finish    The driver's finishing position
     * @param points    The points the driver earned.
     */
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

        //
        // Query the RaceResult objects for this event
        //
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

                    //
                    // Get a Driver object for the race results and driver and set the driver's
                    // points to the points earned in the race.
                    //
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
     * Get all of the results for the specified driver in the specified season.
     * @param driverId  The id of the driver to get the results for
     * @return List of RaceResults objects, one object for each result the driver had in the season.
     */
    public List<RaceResult> getResultsForDriverForSeason(UUID driverId) {

        List<RaceResult> results = new ArrayList<>();

        //
        // First, we need the list of events in the specified season.
        //
        List<RaceEvent> events = getRaceEvents();

        //
        // For each event, get the result for the driver.
        //
        for (RaceEvent event : events) {

            // Get all the results for this event...
            ResultCursorWrapper resultsCursor = new ResultCursorWrapper(mDatabase.query(
                    ResultsTable.NAME,
                    null,
                    "event=?",
                    new String[]{event.getEventId().toString()},
                    null,
                    null,
                    null));

            try {

                //
                // We have a result item for each driver in the race.  Loop over the results,
                // looking for the result, if any, that belongs to our driver.
                //
                resultsCursor.moveToFirst();
                while (!resultsCursor.isAfterLast()) {

                    RaceResult result = resultsCursor.getResult();

                    if (result.getDriverId().equals(driverId)) {
                        results.add(result);
                        break;
                    }
                    resultsCursor.moveToNext();
                }
            } finally {
                resultsCursor.close();
            }
        }

        return results;
    }

    /**
     * Constructor.
     * @param context
     */
    private DataModel(Context context) {

        mDatabase = new DatabaseHelper(context).getWritableDatabase();
        mSeries = UUID.randomUUID();
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

    /**
     * Constructs a ContentValues for a new entry in the RaceResults table.
     * @param raceId   The unique ID of the race the result is for
     * @param driverId The ID of the driver the result is for
     * @param start    The driver's start position in the race
     * @param finish   The driver's finish position in the race
     * @param points   The points the driver earned in the race.
     * @return  A new ContentValues object that can be used to add an entry to the RaceResults table
     */
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
