package com.bignerdranch.android.bikeshareSQLite;

//Includes Bikeshare V4 + Delete + Landscape +
//                   automatic update (observer) + fetching data from server +active buttons green

import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.UUID;

public class BikeShareActivity extends FragmentActivity {

  final static String serverURL= "http://realm.itu.dk:8080";
  private static RidesDB sharedRides;
  private static SQLiteDatabase mDatabase;
  private static final String TAG="BikeShareActivity";

  private Boolean activeRide=false; //activeRide indicates that startRide has finished

  // GUI variables
  private Button addRide, endRide, getRides;
  private FragmentManager fm= getSupportFragmentManager();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_bike_share);
    sharedRides = RidesDB.get(this);
    activeRide= sharedRides.activeRide();

    // Buttons
    addRide= findViewById(R.id.add_button);
    endRide= findViewById(R.id.end_button);
    getRides= findViewById(R.id.get_button);

    if (!activeRide) {
      addRide.setBackgroundColor(Color.GREEN);
      endRide.setBackgroundColor(Color.RED);
    } else {
      endRide.setBackgroundColor(Color.GREEN);
      addRide.setBackgroundColor(Color.RED);
    }

    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
      // landscape orientation

      if (!activeRide) {
        addRide.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            setUpFragment(new StartRideFragment(), R.id.fragment_button_container);
          }
        });
      } else {
        endRide.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            UUID id =RidesDB.get(getApplicationContext()).currentRide().getId();
            setUpFragment(EndRideFragment.newInstance(id), R.id.fragment_button_container);
          }
        });
      }

      getRides.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          new FetchRides(sharedRides).execute(serverURL);
          Log.d(TAG,"Get rides when button clicked");}
      });
      setUpFragment(new ListFragment(), R.id.fragment_list_container);
    } else {
      //vertical orientation
      if (!activeRide) {
        addRide.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            setUpFragment(new StartRideFragment(), R.id.fragment_list_container);
          }
        });
      } else {
        endRide.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            UUID id =RidesDB.get(getApplicationContext()).currentRide().getId();
            setUpFragment(EndRideFragment.newInstance(id), R.id.fragment_list_container);
          }
        });
      }
      setUpFragment(new ListFragment(), R.id.fragment_list_container);
      getRides.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          new FetchRides(sharedRides).execute(serverURL);
        }
      });
    }
  }

  private void setUpFragment(Fragment newFragment, int res) {
    Fragment fragment= fm.findFragmentById(res);
    if (fragment == null) {
      fragment= newFragment;
      fm.beginTransaction()
          .add(res, fragment)
          .commit();
    } else {
      fm.beginTransaction()
          .remove(fragment)
          .add(res, newFragment)
          .commit();
    }
  }
}
