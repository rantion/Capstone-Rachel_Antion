package com.example.rachel.mapstest;

import android.app.Service;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.os.Handler;



/**
 * Created by Rachel on 10/1/14.
 */
public class TrackingService extends Service implements Handler.Callback {
    final static String LOGTAG = "Location Monitoring";
    public static final String ACTION_START_MONITORING = "ACTION_START_MONITORING";
    public static final String ACTION_STOP_MONITORING = "ACTION_STOP_MONITORING";
    public final String HANDLER_THREAD_NAME = "MyLocationThread";

    LocationListener _listener;
    Looper _looper;
    Handler _handler; //is a way to associate work to that looper

    @Override
    public void onCreate() {
        super.onCreate();

        HandlerThread handlerThread = new HandlerThread(HANDLER_THREAD_NAME);
        handlerThread.start();
        _looper = handlerThread.getLooper();
        _handler = new Handler(_looper, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        doStopTracking();
        Log.d(LOGTAG,"Hey Stopped Tracking Services");
        if(_looper != null){
            _looper.quit();
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId){
        int threadId = android.os.Process.getThreadPriority(android.os.Process.myTid());
        Log.d(LOGTAG,"Location Monitoring Service onStartCommand "+threadId );

        _handler.sendMessage(_handler.obtainMessage(0,intent));

       return START_STICKY; //says keep me running all the time

    }

    @Override
    public boolean handleMessage(Message msg) {
        int threadId = android.os.Process.getThreadPriority(android.os.Process.myTid());
        Log.d(LOGTAG,"Location Monitoring Service onStartCommand "+threadId );
        Intent intent = (Intent) msg.obj;
        String action = intent.getAction();
        Log.d(LOGTAG, "Location Service onStartCommand Action: "+action);

        if(action.equals(ACTION_START_MONITORING)){
            doStartTracking();
        }
        else if(action.equals(ACTION_STOP_MONITORING)){
           doStopTracking();
           stopSelf();
        }
        return true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void doStartTracking(){
        Log.d(LOGTAG,"Hey Started Tracking Services");
        doStopTracking();
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        _listener = new MyServiceLocationListener();
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0, _listener,_looper);
    }

    public void doStopTracking(){
        if(_listener != null){
            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            lm.removeUpdates(_listener);
            _listener = null;
        }
    }

}
