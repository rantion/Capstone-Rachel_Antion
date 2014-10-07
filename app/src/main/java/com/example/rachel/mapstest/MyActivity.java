package com.example.rachel.mapstest;

import android.app.PendingIntent;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuInflater;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

/**
 * Created by Rachel on 8/20/14.
 */
public class MyActivity extends FragmentActivity {
    final String _logTag = "Monitor Location";

    private PendingIntent _locationChangeServicePendingIntent;
    private PendingIntent _locationChangeBroadcastPendingIntent;

    LocationListener _networkListener;
    LocationListener _gpsListener;
    LocationListener _passiveLocationListener;


    NetworkProviderStatusReciever _statusReciever;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_maps);
//        TrackingFragment fragment = new TrackingFragment();
//        setContentView(R.layout.tracking_fragment);

        Intent intent = new Intent(Globals.ACTION_LOCATION_CHANGED);
        _locationChangeServicePendingIntent = PendingIntent.getService(this,0,intent,0);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main_menu, menu);
        inflater.inflate(R.menu.pending_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//        if (id == R.id.accurate_provider) {
//            onAccurateProvider(item);
//            return true;
//        }
//        if (id == R.id.low_power_provider) {
//            onLowPowerProvider(item);
//            return true;
//        }
//        if (id == R.id.network_listener) {
//            onStartNetworkListener(item);
//            return true;
//        }
//        if (id == R.id.passive_listener) {
//            onStartPassiveListener(item);
//            return true;
//        }
//        if(id == R.id.ACTION_START_MONITORING){
//            onMenuStartTrackingService(item);
//            return true;
//        }
//        if(id== R.id.ACTION_STOP_MONITORING){
//            onMenuStopTrackingService(item);
//            return true;
//        }
        if(id == R.id.Start_Location_Service){
            onMenuStartLocationForService(item);
            return true;
        }
        if(id == R.id.Stop_Location_Service){
            onMenuStopLocationForService();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public void onMenuFromLocation(MenuItem item){
        Log.d(_logTag,"from Location Menu selected");
        clearDisplay();
        LocationManager lm = (LocationManager)getSystemService(LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location == null){
            Log.d(_logTag,"no Last Location Available");
            return;
        }

        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 5);
            int addressesReturned = addressList.size();
            Log.d(_logTag,"number of addresses returned: "+ addressesReturned);

            for(Address address: addressList){
                displayAddressLines(address);
                AddressHelper.logAddress(_logTag,address);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void onMenuFromPlaceName(MenuItem item){
       Log.d(_logTag,"From Place Name menu selected");
       clearDisplay();
       String place = "Empire State Building,NYC";

        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addressList = geocoder.getFromLocationName(place, 5);
            int addressesReturned = addressList.size();
            Log.d(_logTag,"number of addresses returned: "+ addressesReturned);

            for(Address address: addressList){
                displayAddressLines(address);
                AddressHelper.logAddress(_logTag,address);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void onMenuUseAsyncTask(MenuItem item){
        Log.d(_logTag,"Use Async Task menu selected");
        String placeName = "10 Downing Street, London";
    }

    private void clearDisplay(){
       // TextView textView = (TextView)findViewById(R.id.textView);
    //    textView.setText("");
    }

    private void displayAddressLines(Address address){
        int lastIndex = address.getMaxAddressLineIndex();
        for(int i = 0; i>=lastIndex; i++){
            String addressLine = address.getAddressLine(i);
            addLineToDisplay(addressLine);
        }
        addLineToDisplay("" );
    }

    private void addLineToDisplay(CharSequence displayLine){
//       TextView textView = findViewById(R.id.textView);
//
//      CharSequence existingText = textView.getText();
//      CharSequence newText = existingText + "\n"+ displayLine;

//        textView.setText(newText);

    }


    public void onMenuStartLocationForService(MenuItem item){
        Log.d(_logTag, "Start Location for SERVICE menu selected");
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,_locationChangeServicePendingIntent);
    }

    public void onMenuStopLocationForService(){
        Log.d(_logTag, "Stop Location for SERVICE menu selected");

        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        lm.removeUpdates(_locationChangeServicePendingIntent);
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

    public void onMenuStartTrackingService(MenuItem item){
        startService(new Intent(TrackingService.ACTION_START_MONITORING));
    }

    public void onMenuStopTrackingService(MenuItem item){
        startService(new Intent(TrackingService.ACTION_STOP_MONITORING));
    }


//    public void onMenuTrackingActivityWithFragment(MenuItem item){
//        startActivity(new Intent(this, TrackingActivityWithFragment.class));
//    }

//    public void onMenuTrackingActivityOnly(MenuItem item){
//        startActivity(this, TrackingActivity.class);
//    }
}
