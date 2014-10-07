package com.example.rachel.mapstest;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Rachel on 10/6/14.
 */
public class LocationUpdateService extends IntentService {

    private final String LOG_TAG = "Monitor Location";

    public LocationUpdateService(){
        super("LocationUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        Log.d(LOG_TAG, "Monitor Location SERVICE Intent Action: " + action);

        if(action.equals(Globals.ACTION_LOCATION_CHANGED)){
            Bundle extras = intent.getExtras();
            Location location = (Location)extras.get(LocationManager.KEY_LOCATION_CHANGED);
            if(location != null){
                String logMessage = LogHelper.FormatLocationInfo(location);
                Log.d(LOG_TAG,"******** Location Changed: "+logMessage);
            }

        }
    }

}
