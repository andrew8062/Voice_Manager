package com.example.android.voice_manager.location;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.example.android.voice_manager.MainActivity;
import com.example.android.voice_manager.NavigationActivity;
import com.example.android.voice_manager.global.GlobalClass;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Andrew on 4/23/2015.
 */
public class GoogleLocationServiceAPI  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static int REQUEST_CODE_RECOVER_PLAY_SERVICES = 200;
    private  int update_interval = 5 * 60 * 1000; // 5 mins
    private  int fastest_update_interval = 5 * 60 * 1000; // 5 mins
    private Activity mActivity;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private Handler mHandler;
    private UserLocation userLocation;
    private GlobalClass globalVariable;
    public GoogleLocationServiceAPI(Activity activity, UserLocation userLocation, Handler handler, GlobalClass globalVariable) {
        this.globalVariable = globalVariable;
        update_interval = globalVariable.getGps_frequency() * 60 * 1000;
        fastest_update_interval = update_interval/2;
        mActivity = activity;
        mHandler = handler;
        this.userLocation = userLocation;
    }

    public void start(){
        if(checkGooglePlayService()){
            buildGoogleApiClient();
        }
        createLocationRequest();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();

        }

    }

    public void stop(){
        if (mGoogleApiClient != null){
            stopLocationUpdates();
            mGoogleApiClient.disconnect();
        }
    }

    private boolean checkGooglePlayService() {
        int checkGooglePlayServices = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity);
        if (checkGooglePlayServices != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(checkGooglePlayServices, mActivity, REQUEST_CODE_RECOVER_PLAY_SERVICES)
                    .show();
            return false;
        }
        return true;
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_CODE_RECOVER_PLAY_SERVICES) {
//            if (resultCode == Activity.RESULT_OK) {
//                // Make sure the app is not already connected or attempting to connect
//                if (!mGoogleApiClient.isConnecting() &&
//                        !mGoogleApiClient.isConnected()) {
//                    mGoogleApiClient.connect();
//                }
//            } else if (resultCode == Activity.RESULT_CANCELED) {
//                Toast.makeText(mActivity, "Google Play Services must be installed.",
//                        Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    protected void createLocationRequest(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(update_interval);
        mLocationRequest.setFastestInterval(fastest_update_interval);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }
    protected void stopLocationUpdates(){
        if (mGoogleApiClient != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            //Toast.makeText(mActivity, "Lat: " + mLastLocation.getLatitude() + " Long: " + mLastLocation.getLongitude(), Toast.LENGTH_SHORT).show();
        }
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        userLocation.setUser_location(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        mHandler.obtainMessage(NavigationActivity.MSG_GPS_RETURN_INFO, location).sendToTarget();
        Toast.makeText(mActivity, "Latitude:" + mLastLocation.getLatitude()+", Longitude:"+mLastLocation.getLongitude(),Toast.LENGTH_LONG).show();
    }
}
