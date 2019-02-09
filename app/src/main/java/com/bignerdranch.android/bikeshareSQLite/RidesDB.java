package com.bignerdranch.android.bikeshareSQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.bignerdranch.android.bikeshareSQLite.database.RideBaseHelper;
import com.bignerdranch.android.bikeshareSQLite.database.RideCursorWrapper;
import com.bignerdranch.android.bikeshareSQLite.database.RidesDbSchema;

import java.util.ArrayList;
import java.util.Observable;
import java.util.UUID;


public class RidesDB extends Observable {   // Singleton
  private static RidesDB sRidesDB;
  private static SQLiteDatabase mDatabase;
  private Context mContext;
  //private ArrayList<Ride> mallRides;
  private Ride mlastRide= new Ride("", "");

  public synchronized static RidesDB get(Context context) {
     if (sRidesDB == null) { sRidesDB= new RidesDB(context);}
    return sRidesDB;
  }
  private static ContentValues getContentValues(Ride ride){
        ContentValues values = new ContentValues();
        values.put(RidesDbSchema.RideTable.Cols.UUID, ride.getId().toString());
        values.put(RidesDbSchema.RideTable.Cols.WHATBIKE, ride.getMbikeName());
        values.put(RidesDbSchema.RideTable.Cols.STARTRIDE, ride.getMstartRide());
        values.put(RidesDbSchema.RideTable.Cols.ENDRIDE, ride.getMendRide());
        return values;
  }

  public synchronized ArrayList<Ride> getRidesDB() {
      ArrayList<Ride> rides= new ArrayList<Ride>();
      RideCursorWrapper cursor=queryRides(null, null);
      cursor.moveToFirst();
      while (!cursor.isAfterLast()) {
          rides.add(cursor.getRide());
          cursor.moveToNext();
      }
      cursor.close();
      return rides;
  }

  public synchronized void startRide(String what, String where) {
    mlastRide.setMbikeName(what);
    mlastRide.setMstartRide(where);
  }

  public synchronized void endRide(String where) {
    mlastRide.setMendRide(where);
    //mallRides.add(mlastRide);
      this.addFullRide(mlastRide);
    mlastRide= new Ride("", "");
    this.setChanged();
    notifyObservers();
}

  public synchronized void addFullRide(Ride r) {
    //mallRides.add(r);
    //this.setChanged();
    //notifyObservers();
      ContentValues values= getContentValues(r);
      mDatabase.insert(RidesDbSchema.RideTable.NAME, null, values);
      this.setChanged();
      notifyObservers();
  }
  Ride getRide(UUID id){
      RideCursorWrapper cursor= queryRides(
              RidesDbSchema.RideTable.Cols.UUID+" =?",
              new String[]{
                      id.toString()
              });
      try {
          if (cursor.getCount() == 0) {
              return null;
          }
          cursor.moveToFirst();
          Ride ride = cursor.getRide();
          if( ride.getId()== null){
              return new Ride();
          }else{
              return ride;
          }
      } finally {
          cursor.close();
      }
  }


  public synchronized void delete(Ride ride) {

      ContentValues values= getContentValues(ride);
      mDatabase.delete(RidesDbSchema.RideTable.NAME, RidesDbSchema.RideTable.Cols.UUID+"='"+ ride.getId().toString()+"'",null);
  }

  public synchronized boolean activeRide(){
    return (  (!mlastRide.getMbikeName().equals("")) && (!mlastRide.getMstartRide().equals("")) );
  }

  public synchronized Ride currentRide(){
    return activeRide() ? mlastRide : new Ride();
  }

  private RidesDB(Context context) {
      mContext= context.getApplicationContext();
      mDatabase = new RideBaseHelper(mContext).getWritableDatabase();
      //mallRides = new ArrayList<>();
      //mlastRide.setMstartRide(""); // indicates that no ride is ongoing

      // Add some rides for testing purposes
     // mallRides.add(new Ride("Peters bike", "ITU", "Fields"));
      //mallRides.add(new Ride("Peters bike", "Fields", "Kongens Nytorv"));
      //mallRides.add(new Ride("Jørgens bike", "Home", "ITU"));
      this.setChanged();
      notifyObservers();
  }
    public static RideCursorWrapper queryRides(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                RidesDbSchema.RideTable.NAME, null, whereClause, whereArgs, null, null, null);
        return new RideCursorWrapper(cursor);
    }
    public static void updateRide(Ride ride) {
        String uuidString = ride.getId().toString();
        ContentValues values = getContentValues(ride);
        mDatabase.update(RidesDbSchema.RideTable.NAME, values,
                RidesDbSchema.RideTable.Cols.UUID + " = ?",
        new String[] { uuidString });
  }
}