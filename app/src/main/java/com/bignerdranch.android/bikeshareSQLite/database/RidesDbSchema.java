package com.bignerdranch.android.bikeshareSQLite.database;


public class RidesDbSchema {
    public static final class RideTable {
        public static final String NAME = "rides";

        public static final class Cols {

            public static final String UUID= "uuid";
            public static final String WHATBIKE= "whatbike";
            public static final String STARTRIDE= "startride";
            public static final String ENDRIDE= "endride";
        }
    }
}
