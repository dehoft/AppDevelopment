package edu.ktu.lab3;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class GPS implements LocationListener {

    private final String TAG = "GEO_DEBUG";

    @Override
    public void onLocationChanged(Location location) {
        String provider = location.getProvider();
        float acc = location.getAccuracy();
        double lat =location.getLatitude();
        double lon=location.getLongitude();
        long time =location.getTime();
        String pattern = "dd/MM/yyyy HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(TimeZone.getTimeZone(pattern));
        String stime =sdf.format(time);

        Log.d(TAG, String.format("[%s] Location by %s; (%f,%f) +/- %f meters", stime, provider, lat,lon, acc));

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        int stat= status;
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "Provider enabled "+provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "Provider disabled "+provider);
    }
}
