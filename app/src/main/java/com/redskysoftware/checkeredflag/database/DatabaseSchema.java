package com.redskysoftware.checkeredflag.database;

public class DatabaseSchema {

    //
    // There is an entry in the driver table for each driver.
    //
    public static final class DriverTable {
        public static final String NAME = "Drivers";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String FIRST_NAME = "first_name";
            public static final String LAST_NAME = "last_name";
            public static final String HIGH = "high";
            public static final String LOW = "low";
            public static final String NATIONALITY = "nationality";
        }
    }

    //
    // There is an entry in the series table for each racing series
    //
    public static final class SeriesTable {
        public static final String NAME = "Series";

        public static final class Cols {
            public static final String NAME = "name";
            public static final String POINTS = "points";  // The points system used
            // was doing to have a NEXT EVENT id.  Don't think we need it
        }
    }

    //
    // There is an entry for each team that has exists in a series.
    //
    public static final class TeamTable {
        public static final String NAME = "Teams";

        public static final class Cols {
            public static final String NAME = "name";
            public static final String DRIVER1 = "driver1"; // id of driver 1
            public static final String DRIVER2 = "driver2"; // id of driver 2
            public static final String SERIES = "series";   // id of the series the team races in
        }
    }

    //
    // There is an entry in the SeasonTable for each season conducted in a series.  The season allows
    // events to be grouped into a "calendar" of events.
    //
    public static final class SeasonTable {
        public static final String NAME = "Seasons";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String NAME = "name";
            public static final String SERIES = "series";   // name of the series this season is part of
        }
    }

    //
    // Locations/tracks in the world.
    //
    public static final class LocationTable {
        public static final String NAME = "Locations";

        public static final class Cols {
            public static final String NAME = "name";
            //TODO add location/country string
        }
    }

    //
    // Entries in the table make up a calendar for a season in a series.  Entries are added at the
    // start of the season to define the calendar.  All new entries will typically have their
    // "completed" field set to false to indicate that the event has not been completed yet.
    //
    public static final class EventTable {
        public static final String NAME = "Events";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String NAME = "name";
            public static final String SEASON = "season"; // the season this event is part of
            public static final String START_DATE = "start_date";
            public static final String RACE_DATE = "race_date";
            public static final String LOCATION = "location"; // name of the location entry where this event occurred
            public static final String COMPLETED = "completed";
        }
    }

    //
    // There is an entry in the results table for each driver that competes in an event/race.
    //
    public static final class ResultsTable {
        public static final String NAME = "Results";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String EVENT = "event";   // The id of the event this result is for
            public static final String DRIVER = "driver"; // The id of the driver this result is for
            public static final String STARTED = "started"; // starting position, -1 to indicate not recorded
            public static final String FINISHED = "finished"; // finishing position, -1 to indicate DNF
            public static final String POINTS = "points";     // points earned in the event
        }
    }
}
