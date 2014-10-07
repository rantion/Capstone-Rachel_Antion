package com.example.rachel.mapstest;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Rachel on 10/2/14.
 */
public class MyServiceLocationListener implements LocationListener {
    final static String LOGTAG = "Location Monitoring";

    @Override
    public void onLocationChanged(Location location){
        int threadID = android.os.Process.getThreadPriority(android.os.Process.myTid());
        Log.d(LOGTAG, "Location Monitoring onLocationChanged "+threadID);
        String logMessage = LogHelper.FormatLocationInfo(location);
        Log.d(LOGTAG, "Location Monitoring - "+logMessage);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
