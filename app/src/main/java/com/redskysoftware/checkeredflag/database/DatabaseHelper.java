package com.redskysoftware.checkeredflag.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.redskysoftware.checkeredflag.Driver;
import com.redskysoftware.checkeredflag.database.DatabaseSchema.DriverTable;
import com.redskysoftware.checkeredflag.database.DatabaseSchema.EventTable;
import com.redskysoftware.checkeredflag.database.DatabaseSchema.LocationTable;
import com.redskysoftware.checkeredflag.database.DatabaseSchema.ResultsTable;
import com.redskysoftware.checkeredflag.database.DatabaseSchema.SeasonTable;
import com.redskysoftware.checkeredflag.database.DatabaseSchema.SeriesTable;
import com.redskysoftware.checkeredflag.database.DatabaseSchema.TeamTable;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "checkeredFlagDb.db";
    private static final String SERIES_NAME = "Formula A";

    private static final String[] teamNames = { "IMOCHI",
                                                "Team Jacom Mobile",
                                                "AQI Racing",
                                                "Mixlub Racing",
                                                "Decksbern Motorsport",
                                                "WRB Motorsport",
                                                "ADVAN Racing",
                                                "APEX Racing",
                                                "BNS Birdia",
                                                "OBX Watches Racing" };

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //
        // Construct a new database...
        //
        db.execSQL("create table " + DriverTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                DriverTable.Cols.UUID + ", " +
                DriverTable.Cols.FIRST_NAME + ", " +
                DriverTable.Cols.LAST_NAME + ", " +
                DriverTable.Cols.NATIONALITY + ", " +
                DriverTable.Cols.HIGH + ", " +
                DriverTable.Cols.LOW +
                ")"
        );

        //
        // Load the driver table with the drivers...
        //
        db.insert(DriverTable.NAME, null, getDriverContentValues("John", "Brocksopp", "England", 100, 90));
  //      db.insert(DriverTable.NAME, null, getDriverContentValues("Giovanni", "Morelli", "Italy", 100, 90));
        db.insert(DriverTable.NAME, null, getDriverContentValues("James", "Kibble", "Canada", 100, 90));
        db.insert(DriverTable.NAME, null, getDriverContentValues("Oli", "Gill", "Italy", 100, 90));
        db.insert(DriverTable.NAME, null, getDriverContentValues("George", "Usoh", "Belgium", 100, 90));
        db.insert(DriverTable.NAME, null, getDriverContentValues("Dominic", "Schoenberner", "Germany", 100, 90));
        db.insert(DriverTable.NAME, null, getDriverContentValues("Volker", "Keim", "Germany", 100, 90));
        db.insert(DriverTable.NAME, null, getDriverContentValues("Craig", "Parkes", "USA", 100, 90));
        db.insert(DriverTable.NAME, null, getDriverContentValues("Pedro", "Antunes", "Spain", 100, 90));
        db.insert(DriverTable.NAME, null, getDriverContentValues("Luis", "Barata", "Italy", 100, 90));
        db.insert(DriverTable.NAME, null, getDriverContentValues("Alan", "Smith", "Ireland", 100, 90));
        db.insert(DriverTable.NAME, null, getDriverContentValues("Darren", "Wakeman", "Canada", 100, 90));
        db.insert(DriverTable.NAME, null, getDriverContentValues("Steven", "Unsen", "Finland", 100, 90));
        db.insert(DriverTable.NAME, null, getDriverContentValues("Alain", "Kohler", "France", 100, 90));
        db.insert(DriverTable.NAME, null, getDriverContentValues("David", "Rezac", "Netherlands", 100, 90));
        db.insert(DriverTable.NAME, null, getDriverContentValues("Julien", "Borkaubert", "France", 100, 90));
        db.insert(DriverTable.NAME, null, getDriverContentValues("Umer", "Ahmad", "Egypt", 100, 90));
        db.insert(DriverTable.NAME, null, getDriverContentValues("Keith", "Brunnerkant", "USA", 100, 90));
        db.insert(DriverTable.NAME, null, getDriverContentValues("Artam", "Ermolov", "Russia", 100, 90));
        db.insert(DriverTable.NAME, null, getDriverContentValues("Sarah", "Clark", "England", 100, 90));
        db.insert(DriverTable.NAME, null, getDriverContentValues("Jim", "Kalinowski", "USA", -1, -1));

        db.execSQL("create table " + SeriesTable.NAME + "(" +
                SeriesTable.Cols.NAME + ", " +
                SeriesTable.Cols.POINTS +
                ")"
        );

        /******************************************************************************************
         * SEASON TABLE
         ******************************************************************************************/
        db.execSQL("create table " + SeasonTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                SeasonTable.Cols.UUID + ", " +
                SeasonTable.Cols.NAME + ", " +
                SeasonTable.Cols.SERIES +
                ")"
        );

        UUID seasonId = UUID.randomUUID();

        // Add an initial season for the default series.
        db.insert(SeasonTable.NAME, null, getSeasonContentValues("2019", SERIES_NAME, seasonId));

        /******************************************************************************************
         * TEAM TABLE
         ******************************************************************************************/
        db.execSQL("create table " + TeamTable.NAME + "(" +
                TeamTable.Cols.NAME + ", " +
                TeamTable.Cols.SERIES + ", " +
                TeamTable.Cols.DRIVER1 + ", " +
                TeamTable.Cols.DRIVER2 +
                ")"
        );

        //
        // Populate the team table.  Each team needs two drivers.  We'll assign drivers from the
        // driver table in pairs to the teams in the order they are in the driver table.
        //
        DriverCursorWrapper cursor = new DriverCursorWrapper(db.query(
                DatabaseSchema.DriverTable.NAME,
                null,  // all columns
                null,
                null,
                null,
                null,
                null));

        try {
            cursor.moveToFirst();

            for (String name : teamNames) {

                //
                // Get the next two drivers in the driver table and add them to the next team.
                //
                Driver driver1 = cursor.getDriver();
                cursor.moveToNext();
                Driver driver2 = cursor.getDriver();
                cursor.moveToNext();

                db.insert(TeamTable.NAME, null,
                        getTeamContentValues(name, driver1.getId(), driver2.getId(), SERIES_NAME));
            }

        } finally {
            cursor.close();
        }

        /*****************************************************************************************'
         * LOCATION table...
         ******************************************************************************************/
        db.execSQL("create table " + LocationTable.NAME + "(" +
                LocationTable.Cols.NAME +
                ")"
        );

        db.insert(LocationTable.NAME, null, getLocationContentValues("Algarve"));
        db.insert(LocationTable.NAME, null, getLocationContentValues("Dubai"));
        db.insert(LocationTable.NAME, null, getLocationContentValues("Spain"));
        db.insert(LocationTable.NAME, null, getLocationContentValues("Monte Carlo"));
        db.insert(LocationTable.NAME, null, getLocationContentValues("Silverstone"));
        db.insert(LocationTable.NAME, null, getLocationContentValues("A-1 Ring"));
        db.insert(LocationTable.NAME, null, getLocationContentValues("Hockenheim"));
        db.insert(LocationTable.NAME, null, getLocationContentValues("Spa"));
        db.insert(LocationTable.NAME, null, getLocationContentValues("Monza"));
        db.insert(LocationTable.NAME, null, getLocationContentValues("Long Beach"));
        db.insert(LocationTable.NAME, null, getLocationContentValues("Brno"));
        db.insert(LocationTable.NAME, null, getLocationContentValues("Suzuka"));


        /******************************************************************************************
         * EVENT TABLE
         ******************************************************************************************/
        db.execSQL("create table " + EventTable.NAME + "(" +
                "_id integer primary key autoincrement, " +
                EventTable.Cols.UUID + ", " +
                EventTable.Cols.NAME + ", " +
                EventTable.Cols.LOCATION + ", " +
                EventTable.Cols.SEASON + ", " +
                EventTable.Cols.START_DATE + ", " +
                EventTable.Cols.RACE_DATE + ", " +
                EventTable.Cols.COMPLETED +
                ")"
        );

        // Add the events for the season for our default series
        db.insert(EventTable.NAME, null,
                getEventContentValues(UUID.randomUUID(),"Portugal GP", "Algarve",
                seasonId, new GregorianCalendar(2019, 2, 9).getTime(),
                new GregorianCalendar(2019, 2, 10).getTime(), false));
        db.insert(EventTable.NAME, null,
                getEventContentValues(UUID.randomUUID(),"Dubai GP", "Dubai",
                seasonId, new GregorianCalendar(2019, 2, 30).getTime(),
                new GregorianCalendar(2019, 2, 31).getTime(), false));
        db.insert(EventTable.NAME, null,
                getEventContentValues(UUID.randomUUID(),"European GP", "Brno",
                        seasonId, new GregorianCalendar(2019, 2, 30).getTime(),
                        new GregorianCalendar(2019, 2, 31).getTime(), false));
        db.insert(EventTable.NAME, null,
                getEventContentValues(UUID.randomUUID(),"Spanish GP", "Spain",
                        seasonId, new GregorianCalendar(2019, 3, 27).getTime(),
                        new GregorianCalendar(2019, 3, 28).getTime(), false));
        db.insert(EventTable.NAME, null,
                getEventContentValues(UUID.randomUUID(),"Monaco GP", "Monte Carlo",
                        seasonId, new GregorianCalendar(2019, 2, 30).getTime(),
                        new GregorianCalendar(2019, 2, 31).getTime(), false));
        db.insert(EventTable.NAME, null,
                getEventContentValues(UUID.randomUUID(),"British GP", "Silverstone",
                        seasonId, new GregorianCalendar(2019, 2, 30).getTime(),
                        new GregorianCalendar(2019, 2, 31).getTime(), false));
        db.insert(EventTable.NAME, null,
                getEventContentValues(UUID.randomUUID(),"Austrian GP", "A-1 Ring",
                        seasonId, new GregorianCalendar(2019, 2, 30).getTime(),
                        new GregorianCalendar(2019, 2, 31).getTime(), false));
        db.insert(EventTable.NAME, null,
                getEventContentValues(UUID.randomUUID(),"German GP", "Hockenheim",
                        seasonId, new GregorianCalendar(2019, 2, 30).getTime(),
                        new GregorianCalendar(2019, 2, 31).getTime(), false));
        db.insert(EventTable.NAME, null,
                getEventContentValues(UUID.randomUUID(),"Belgium GP", "Spa",
                        seasonId, new GregorianCalendar(2019, 2, 30).getTime(),
                        new GregorianCalendar(2019, 2, 31).getTime(), false));
        db.insert(EventTable.NAME, null,
                getEventContentValues(UUID.randomUUID(),"Italian GP", "Monza",
                        seasonId, new GregorianCalendar(2019, 2, 30).getTime(),
                        new GregorianCalendar(2019, 2, 31).getTime(), false));
        db.insert(EventTable.NAME, null,
                getEventContentValues(UUID.randomUUID(),"United States GP", "Long Beach",
                        seasonId, new GregorianCalendar(2019, 2, 30).getTime(),
                        new GregorianCalendar(2019, 2, 31).getTime(), false));
        db.insert(EventTable.NAME, null,
                getEventContentValues(UUID.randomUUID(),"Japanese GP", "Suzuka",
                        seasonId, new GregorianCalendar(2019, 2, 30).getTime(),
                        new GregorianCalendar(2019, 2, 31).getTime(), false));

        /******************************************************************************************
         * RESULTS TABLE
         ******************************************************************************************/
        db.execSQL("create table " + ResultsTable.NAME + "(" +
                "_id integer primary key autoincrement, " +
                ResultsTable.Cols.UUID + ", " +
                ResultsTable.Cols.DRIVER + ", " +
                ResultsTable.Cols.EVENT + ", " +
                ResultsTable.Cols.STARTED + ", " +
                ResultsTable.Cols.FINISHED + ", " +
                ResultsTable.Cols.POINTS +
                ")"
        );

        // The initial results table is empty...
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }

    /**
     * Construct a ContentValues object with driver info
     * @param firstName  The driver's first name
     * @param lastName   The driver's last name
     * @param nationality The driver's nationality
     * @param high   The high performance value for the driver.
     * @param low    The low performance value for the driver
     * @return ContentValues to use to create a row in the DriverTable.
     */
    private ContentValues getDriverContentValues(String firstName, String lastName,
                                                 String nationality, int high, int low) {
        ContentValues values = new ContentValues();

        values.put(DriverTable.Cols.UUID, UUID.randomUUID().toString());
        values.put(DriverTable.Cols.FIRST_NAME, firstName);
        values.put(DriverTable.Cols.LAST_NAME, lastName);
        values.put(DriverTable.Cols.NATIONALITY, nationality);
        values.put(DriverTable.Cols.HIGH, high);
        values.put(DriverTable.Cols.LOW, low);

        return values;
    }

    private ContentValues getTeamContentValues(String name, UUID driver1, UUID driver2, String series) {
        ContentValues values = new ContentValues();

        values.put(TeamTable.Cols.NAME, name);
        values.put(TeamTable.Cols.DRIVER1, driver1.toString());
        values.put(TeamTable.Cols.DRIVER2, driver2.toString());
        values.put(TeamTable.Cols.SERIES, series);

        return values;
    }

    private ContentValues getLocationContentValues(String name) {

        ContentValues values = new ContentValues();
        values.put(LocationTable.Cols.NAME, name);

        return values;
    }

    private ContentValues getSeasonContentValues(String name, String series, UUID seasonId) {

        ContentValues values = new ContentValues();
        values.put(SeasonTable.Cols.UUID, seasonId.toString());
        values.put(SeasonTable.Cols.NAME, name);
        values.put(SeasonTable.Cols.SERIES, series);

        return values;
    }

    public static ContentValues getEventContentValues(UUID id, String name, String location, UUID seasonId,
                                                      Date startDate, Date raceDate, boolean completed) {

        ContentValues values = new ContentValues();
        values.put(EventTable.Cols.UUID, id.toString());
        values.put(EventTable.Cols.NAME, name);
        values.put(EventTable.Cols.LOCATION, location);
        values.put(EventTable.Cols.SEASON, seasonId.toString());
        values.put(EventTable.Cols.START_DATE, startDate.getTime());
        values.put(EventTable.Cols.RACE_DATE, raceDate.getTime());
        values.put(EventTable.Cols.COMPLETED, completed);

        return values;
    }
}
