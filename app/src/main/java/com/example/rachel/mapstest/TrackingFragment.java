package com.example.rachel.mapstest;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.annotation.Documented;
import java.util.logging.Handler;

/**
 * Created by Rachel on 9/30/14.
 */
public class TrackingFragment extends Fragment implements LocationListener {

    final static String LOGTAG = "Location Monitoring";

    View _topView;
    Looper _looper;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        doStopTracking();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances){
     _topView = inflater.inflate(R.layout.tracking_fragment, container, false);
        return _topView;
    }

   @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
       inflater.inflate(R.menu.tracking_menu, menu);
    }

  //  @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
            case R.id.start_tracking:
                onMenuStartTracking(item);
                break;
            case R.id.stop_tracking:
                onMenuStopTracking(item);
                break;
       }

        return true;
    }

    private void onMenuStartTracking(MenuItem item){
        doStartTracking();
    }

    private void onMenuStopTracking(MenuItem item){
        doStopTracking();
    }

    private void doStartTracking(){
       LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        HandlerThread thread = new HandlerThread("locationthread");
        thread.start();
        _looper = thread.getLooper();
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this, _looper);
    }

    private void doStopTracking(){
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
          lm.removeUpdates(this);

        if(_looper != null){
            _looper.quit();
            _looper = null;
        }
    }

    public void setLocation(Location location){

        Log.d(LOGTAG,"Location Monitoring Fragment - setLocation ");

        String latitudeString = String.format("%.6f", location.getLatitude());
        String longitudeString = String.format("%.6f", location.getLongitude());

        try {
            TextView latitudeView = (TextView) _topView.findViewById(R.id.latitude);
            TextView longitudeView = (TextView) _topView.findViewById(R.id.longitude);

            latitudeView.setText(latitudeString);
            longitudeView.setText(longitudeString);
        }
        catch(Exception ex){
            Log.e(LOGTAG, "Location Monitoring setLocation exception", ex);
        }
    }

    public void onLocationChanged(Location location){
        Log.d(LOGTAG, "Location Management Fragment onLocationChanged");

        final Location theLocation = location;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setLocation(theLocation);
            }
        });

    }

    public void onStatusChanged(String s, int i, Bundle bundle){

    }

    public void onProviderEnabled(String s){

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}
