package com.example.leonsdestination2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity{

    int LOCATION_REFRESH_TIME = 10; // 10  milliseconds to update
    int LOCATION_REFRESH_DISTANCE = 0; // 0 meter to update
    int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private double latM = 50.7385400928;
    private double lngM = 7.09383968264;

    private String perm1 = "android.permission.ACCESS_COARSE_LOCATION";
    private String perm2 = "android.permission.ACCESS_FINE_LOCATION";
    private String perm3 = "android.permission.ACCESS_BACKGROUND_LOCATION";

    String[] perms = {perm1, perm2};

    private TextView myTextView;
    private ImageView myImageView;
    private TextView counterView;

    int tickCounter = -1;

    private LocationManager mLocationManager;
    private Location musiktruhe;

    private LocationListener mLocationListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
//                myTextView.setText(location.toString());
                tick(location);
            }
        };
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myTextView = (TextView)findViewById(R.id.mytextfield);
        myImageView = (ImageView) findViewById(R.id.imageViewArrow);
        counterView = (TextView) findViewById(R.id.textViewCounter);
        init();
        tick(null);
    }

    private void init(){
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        musiktruhe = createMusiktruheAsLocation();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            ActivityCompat.requestPermissions(this, perms, MY_PERMISSIONS_REQUEST_LOCATION);


            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, mLocationListener);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, mLocationListener);
    }

    public Location getLocation() {
        Location location = null;
        try {

            // getting GPS status
            boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!isGPSEnabled) {
                // no network provider is enabled

            } else {
                //get the location by gps
                if (isGPSEnabled) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        ActivityCompat.requestPermissions(this, perms, MY_PERMISSIONS_REQUEST_LOCATION);


                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                    }
                    if (mLocationListener != null){
                        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, mLocationListener);
                        if (mLocationManager != null) {
                            location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected double bearing(double startLat, double startLng, double endLat, double endLng){
        double longitude1 = startLng;
        double longitude2 = endLng;
        double latitude1 = Math.toRadians(startLat);
        double latitude2 = Math.toRadians(endLat);
        double longDiff= Math.toRadians(longitude2-longitude1);
        double y= Math.sin(longDiff) * Math.cos(latitude2);
        double x=Math.cos(latitude1)* Math.sin(latitude2)-Math.sin(latitude1) * Math.cos(latitude2) * Math.cos(longDiff);

        return (Math.toDegrees(Math.atan2(y, x))+360)%360;
    }

    private Location createMusiktruheAsLocation(){
        Location m = new Location(LocationManager.GPS_PROVIDER);
        m.setLatitude(50.7385400928);
        m.setLongitude(7.09383968264);
        return m;
    }

    private void tick(Location location){
        counterView.setText(String.valueOf(tickCounter++));
//        Location location = getLocation();
        if (location == null) {
            myTextView.setText("Standort nicht abrufbar");
        } else {
            Double winkel = bearing(location.getLatitude(), location.getLongitude(), latM, lngM);
            Float distance = location.distanceTo(musiktruhe);
            myTextView.setText(String.format("Noch %.4g km bis zum Ziel", distance/1000));
//            location.setBearing(90);
            Float actBearing = location.getBearing();
            myImageView.setRotation(winkel.floatValue() - 90 - actBearing);
        }
    }
}