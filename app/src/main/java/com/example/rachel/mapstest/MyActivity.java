package com.example.rachel.mapstest;

import android.app.Activity;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import java.net.NetworkInterface;
import java.util.List;

/**
 * Created by Rachel on 8/20/14.
 */
public class MyActivity extends FragmentActivity {
    final String _logTag = "Monitor Location";

    LocationListener _networkListener;
    LocationListener _gpsListener;
    LocationListener _passiveLocationListener;
    private GoogleMap mMap;


    NetworkProviderStatusReciever _statusReciever;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.accurate_provider) {
            onAccurateProvider(item);
            return true;
        }
        if (id == R.id.low_power_provider) {
            onLowPowerProvider(item);
            return true;
        }
        if (id == R.id.network_listener) {
            onStartNetworkListener(item);
            return true;
        }
        if (id == R.id.passive_listener) {
            onStartPassiveListener(item);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onAccurateProvider(MenuItem item){
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setSpeedRequired(true);
        criteria.setAltitudeRequired(true);
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        List<String> matchingProviderNames = lm.getProviders(criteria, false);
        for(String providerName : matchingProviderNames){
            LocationProvider provider = lm.getProvider(providerName);
            String logMessage = LogHelper.formatLocationProvider(this,provider);
            Log.d(_logTag, logMessage);
        }
    }

    public void onLowPowerProvider(MenuItem item){
        Criteria criteria = new Criteria();

        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setSpeedRequired(true);
        criteria.setAltitudeRequired(true);
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        List<String> matchingProviderNames = lm.getProviders(criteria, false);
        for(String providerName : matchingProviderNames){
            LocationProvider provider = lm.getProvider(providerName);
            String logMessage = LogHelper.formatLocationProvider(this,provider);
            Log.d(_logTag, logMessage);
        }

    }

    public void onStartNetworkListener(MenuItem item){
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(confirmNetworkProviderAvailable(lm)) {
            _statusReciever = new NetworkProviderStatusReciever();
            _statusReciever.start(this);
            _networkListener = new MyLocationListener();
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, _networkListener);
        }
    }

    public void onStartPassiveListener(MenuItem item){
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        _passiveLocationListener = new MyLocationListener();
        Log.d(_logTag,"Starting Passive");
        lm.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER,0, 0, _passiveLocationListener);
    }

    public void stopLocationListener(){

        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(_networkListener == null){
            lm.removeUpdates(_networkListener);
            _networkListener = null;
        }

        if(_statusReciever != null){
            _statusReciever.stop(this);
            _statusReciever = null;

        }
    }

    boolean confirmNetworkProviderAvailable (LocationManager lm) {

        boolean networkAvailable =  confirmAirplaneModeIsOff()&&
                confirmNetworkProviderEnabled(lm)&&
                confirmWifiAvailable()
                ;
        return networkAvailable;
    }

    public boolean confirmNetworkProviderEnabled (LocationManager lm){
        boolean isAvailable = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(!isAvailable){
            AlertUserDialog dialog = new AlertUserDialog("Please Enable Location Services" ,Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            dialog.show(getFragmentManager(),null);
        }
        return isAvailable;
    }

    public boolean confirmAirplaneModeIsOff (){

        Log.d(_logTag, "inside AirPlaneMode");
        boolean isOff =
                Settings.System.getInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 0;
        Log.d(_logTag, "AirPlane Mode Is : "+isOff);
        if(!isOff){
            AlertUserDialog dialog = new AlertUserDialog("Please disable Airplane mode", Settings.ACTION_AIRPLANE_MODE_SETTINGS);
            dialog.show(getFragmentManager(),null);
        }
        return isOff;
    }

    public boolean confirmWifiAvailable(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isAvailable = wifiInfo.isAvailable();
        if(!isAvailable){
            AlertUserDialog dialog = new AlertUserDialog("Please Enable Your WiFi", Settings.ACTION_WIFI_SETTINGS);
            dialog.show(getFragmentManager(),null);
        }
        return isAvailable;
    }

    public void onStartListening(MenuItem item){
        Log.d(_logTag, "Monitor Location - Start Listening");
        try {
            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            _networkListener = new MyLocationListener();
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,0, _networkListener);

            _gpsListener = new MyLocationListener();
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, _gpsListener);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onStopListening(MenuItem item){
        Log.d(_logTag, "Monitor Location - Stop Listening");
        doStopListening();
    }

    public void onRecentLocation(MenuItem item) {
        Log.d(_logTag, "Monitor - Recent Location");

        Location networkLocation;
        Location gpsLocation;

        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        networkLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        gpsLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        String networkLogMessage = LogHelper.FormatLocationInfo(networkLocation);
        String gpsLogMessages = LogHelper.FormatLocationInfo(gpsLocation);

        Log.d(_logTag, "Monitor - Network Location" + networkLogMessage);
        Log.d(_logTag, "Monitor - GPS Location" + gpsLogMessages);
    }

    public void onSingleLocation(MenuItem item){
        Log.d(_logTag, "Monitor - Single Location");
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        _networkListener = new MyLocationListener();
        lm.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, _networkListener, null);

        _gpsListener = new MyLocationListener();
        lm.requestSingleUpdate(LocationManager.GPS_PROVIDER, _gpsListener, null);

    }

    public void onExit(MenuItem item){
        Log.d(_logTag, "Monitor Location Exit");
        doStopListening();
        finish();
    }

    public void doStopListening(){

        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(_networkListener == null){
            lm.removeUpdates(_networkListener);
            _networkListener = null;
        }
    }
}
