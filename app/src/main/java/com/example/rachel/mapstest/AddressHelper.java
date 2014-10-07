package com.example.rachel.mapstest;


import android.location.Address;
import android.util.Log;

/**
 * Created by Rachel on 10/6/14.
 */
public class AddressHelper {

    public AddressHelper(){

    }

    public static void logAddress(String logTag, Address address){

        int maxAddressLineIndex = address.getMaxAddressLineIndex();
        for(int i = 0; i<= maxAddressLineIndex; i++){
            String addressLine = address.getAddressLine(i);
            Log.d(logTag,i+":  "+ addressLine);
        }

        double latitude = 0.0d;
        double longitude = 0.0d;

        boolean hasLatitude = address.hasLatitude();
        if(hasLatitude){
            latitude = address.getLatitude();
        }
        boolean hasLongitude = address.hasLongitude();
        if(hasLongitude){
            longitude = address.getLongitude();
        }

        String adminArea = address.getAdminArea();
        String featureName = address.getFeatureName();
        String locality = address.getLocality();
        String postalCode = address.getPostalCode();
        String premises = address.getPremises();
        String subAdminArea = address.getSubAdminArea();
        String subLocality = address.getSubLocality();
        String subThoroughfare = address.getSubThoroughfare();
        String thoroughfare = address.getThoroughfare();
        String phone = address.getPhone();
        String url = address.getUrl();

        //logValue(logTag, "latitude", hasLatitude, latitude);
        //logValue(logTag, "longitude", hasLongitude, longitude);
//        logValue(logTag,"adminArea", adminArea);
//        logValue(logTag,"featureName", featureName);
//        logValue(logTag,"locality", locality);
//        logValue(logTag,"postalCode", postalCode);
//        logValue(logTag,"premises", premises);
//        logValue(logTag,"subThoroughfare", subThoroughfare);
//        logValue(logTag,"subAdminArea",subAdminArea);
//        logValue(logTag,"subLocality", subLocality);
//        logValue(logTag,"thoroughfare", thoroughfare);
//        logValue(logTag,"phone", phone);
//        logValue(logTag,"url", url);
    }
}
