package com.example.android.voice_manager;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.voice_manager.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements View.OnClickListener {
    final private LatLng INITIAL_LOCATION = new LatLng(23.697810, 120.960515);
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Button btn_ok;
    private Button btn_cancel;
    private final Marker[] marker = new Marker[1];
    public LatLng select_location;

    public MapsActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        btn_ok = (Button) findViewById(R.id.btn_map_ok);
        btn_ok.setOnClickListener(this);
        btn_cancel = (Button) findViewById(R.id.btn_map_cancel);
        btn_cancel.setOnClickListener(this);
        Intent intent = getIntent();
        double lat = intent.getDoubleExtra("lat", 0);
        double lng = intent.getDoubleExtra("lng", 0);
        select_location = new LatLng(lat, lng);
        setUpMapIfNeeded();


    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);

        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(INITIAL_LOCATION,25));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(select_location, 16));
        marker[0] = mMap.addMarker(new MarkerOptions().position(select_location));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (marker[0] != null)
                    marker[0].remove();

                marker[0] = mMap.addMarker(new MarkerOptions().position(latLng));
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == btn_ok) {
            Toast.makeText(getApplication(), "map btn ok clicked", Toast.LENGTH_SHORT).show();
            Intent databackIntent = new Intent();
            databackIntent.putExtra("lat", marker[0].getPosition().latitude);
            databackIntent.putExtra("lng", marker[0].getPosition().longitude);
            setResult(RESULT_OK, databackIntent);
        } else if (v == btn_cancel) {
            Toast.makeText(getApplication(), "map btn cancel clicked", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
        }
        finish();
    }
}
