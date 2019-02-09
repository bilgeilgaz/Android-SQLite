package com.bignerdranch.android.bikeshareSQLite.database;


import android.database.Cursor;
import android.database.CursorWrapper;
import com.bignerdranch.android.bikeshareSQLite.Ride;

import java.util.UUID;

public class RideCursorWrapper extends CursorWrapper {
    public RideCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Ride getRide() {
        String uuidString = getString(getColumnIndex(RidesDbSchema.RideTable.Cols.UUID));
        String whatBike = getString(getColumnIndex(RidesDbSchema.RideTable.Cols.WHATBIKE));
        String startRide = getString(getColumnIndex(RidesDbSchema.RideTable.Cols.STARTRIDE));
        String endRide = getString(getColumnIndex(RidesDbSchema.RideTable.Cols.ENDRIDE));

        Ride ride = new Ride(UUID.fromString(uuidString));
        ride.setMbikeName(whatBike);
        ride.setMstartRide(startRide);
        ride.setMendRide(endRide);
        return ride;
    }

}

