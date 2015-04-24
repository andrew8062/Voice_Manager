package com.example.android.voice_manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Andrew on 4/23/2015.
 */
public class GoogleLocationServiceAPI  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private Activity mActivity;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;


    private static int REQUEST_CODE_RECOVER_PLAY_SERVICES = 200;

    public GoogleLocationServiceAPI(Activity activity) {
        mActivity = activity;
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

    protected void stop(){
        if (mGoogleApiClient != null){
            startLocationUpdates();
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
        mLocationRequest.setInterval(20 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
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
            Toast.makeText(mActivity, "Lat: " + mLastLocation.getLatitude() + " Long: " + mLastLocation.getLongitude(), Toast.LENGTH_SHORT).show();
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
        Toast.makeText(mActivity, "Latitude:" + mLastLocation.getLatitude()+", Longitude:"+mLastLocation.getLongitude(),Toast.LENGTH_LONG).show();

    }


}