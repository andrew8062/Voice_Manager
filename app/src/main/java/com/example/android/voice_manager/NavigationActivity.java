package com.example.android.voice_manager;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import com.example.android.voice_manager.alarm.AlarmDialog;
import com.example.android.voice_manager.global.GlobalClass;
import com.example.android.voice_manager.location.DistanceOfTwoPoint;
import com.example.android.voice_manager.location.GoogleLocationServiceAPI;
import com.example.android.voice_manager.location.UserLocation;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;


public class NavigationActivity extends Activity implements MainActivity.OnDataPass {
    public static final String PREFS_NAME = "MyPrefsFile";

    public static final String DESTINATION_CODE = "destination_code";

    public static final int MSG_GPS = 1;
    private static final String TAG = "vm:navigation";
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mNavigationTitles;
    private GlobalClass globalVariable = null;
    protected UserLocation userLocation;
    private GoogleLocationServiceAPI googleLocationServiceAPI;

//    public NavigationActivity() {
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        globalVariable = (GlobalClass) getApplicationContext();
        mTitle = mDrawerTitle = getTitle();
        mNavigationTitles = getResources().getStringArray(R.array.navigation_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        userLocation = new UserLocation();
        googleLocationServiceAPI = new GoogleLocationServiceAPI(this, userLocation, mHandler);
        checkPreferences();

        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mNavigationTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // Calling Application class (see application tag in AndroidManifest.xml)
        //Set name and email in global/application context


        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.drawable.ic_drawer,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View view) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        if (savedInstanceState == null || globalVariable.isAlarmActive()) {
            selectItem(0);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("destination_alarm_distance", globalVariable.getDestionaion_alarm_distance());
        editor.commit();
    }

    private void checkPreferences() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        globalVariable.setDestionaion_alarm_distance(settings.getInt("destination_alarm_distance", getResources().getInteger(R.integer.destination_alarm_distance)));
    }

    private void selectItem(int pos) {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment;
        switch (pos) {
            case 0:
                fragment = new MainActivity();
                fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
                break;
            case 1:
                fragment = new AlarmListActivity();
                fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
                break;
            case 2:
                break;
        }

        mDrawerList.setItemChecked(pos, true);
        setTitle(mNavigationTitles[pos]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onDataPass(UserLocation userLocation) {
        this.userLocation = userLocation;
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public UserLocation getUserLocation() {
        return userLocation;
    }
    public GoogleLocationServiceAPI getGoogleLocationServiceAPI(){
        return googleLocationServiceAPI;
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case MSG_GPS:
                    Double distance;
                    Location location;
                    location = (Location) msg.obj;
                    userLocation.setUser_location(new LatLng(location.getLatitude(), location.getLongitude()));
                    distance = DistanceOfTwoPoint.calculate(userLocation.getTarget_location().latitude, userLocation.getTarget_location().longitude
                            , userLocation.getUser_location().latitude, userLocation.getUser_location().longitude, 'K');
                    //updateUserLocationDistance(distance);
                    userLocation.setDistance(distance);
                    if(distance < globalVariable.getDestionaion_alarm_distance()){
                        googleLocationServiceAPI.stop();
                        selectItem(0);
                        globalVariable.setAlarmActive(true);
                    }

                    break;
            }

        }
    };
}
