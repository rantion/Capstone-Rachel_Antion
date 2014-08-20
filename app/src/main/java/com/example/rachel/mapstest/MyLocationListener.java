package com.example.rachel.mapstest;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Rachel on 8/20/14.
 */
public class MyLocationListener implements LocationListener {

    final String _logTag = "MonitorLocation";

    @Override
    public void onLocationChanged(Location location) {
        String provider = location.getProvider();
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        float accuracy = location.getAccuracy();
        long time = location.getTime();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(_logTag,"Monitor Location - providerEnabled" +provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(_logTag,"Monitor Location - providerDisabled" +provider);
    }
}
