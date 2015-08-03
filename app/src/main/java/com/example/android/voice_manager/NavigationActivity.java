package com.example.android.voice_manager;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.example.android.voice_manager.global.GlobalClass;
import com.example.android.voice_manager.location.DistanceOfTwoPoint;
import com.example.android.voice_manager.location.GoogleLocationServiceAPI;
import com.example.android.voice_manager.location.UserLocation;
import com.google.android.gms.maps.model.LatLng;


public class NavigationActivity extends Activity {
    public static final String PREFS_NAME = "MyPrefsFile";

    public static final int MSG_GPS_RETURN_INFO = 1;
    public static final int MSG_GPS_START = 2;
    public static final int MSG_GPS_STOP = 3;
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
        //initial variables
        globalVariable = (GlobalClass) getApplicationContext();
        mTitle = mDrawerTitle = getTitle();
        mNavigationTitles = getResources().getStringArray(R.array.navigation_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        userLocation = new UserLocation();
        googleLocationServiceAPI = new GoogleLocationServiceAPI(this, userLocation, mHandler, globalVariable);
        //get setting values
        checkPreferences();
        setupDrawer(savedInstanceState);

    }

    private void setupDrawer(Bundle savedInstanceState) {
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mNavigationTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

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
        //if (globalVariable.isAlarmActive() || savedInstanceState == null) {
        selectItem(0);
        // }
    }


    private void checkPreferences() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        globalVariable.setVibrate(settings.getBoolean(GlobalClass.SHARED_PREFERENCE_VIBRATE_SETTING, false));
        globalVariable.setAlarmTonePath(settings.getString(GlobalClass.SHARED_PREFERENCE_RIGHTTONEPATH, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString()));
        globalVariable.setGps_frequency(settings.getInt(GlobalClass.SHARED_PREFERENCE_GPS_FREQUENCY, 5));
        userLocation.setAlarm_distance(settings.getInt(UserLocation.SHARED_PREFERENCE_DISTANCE_ALARM_KEY, UserLocation.DEFAULT_DISTANCE_ALARM));

        Log.d(TAG, "setting:\nalarm distnace: " + userLocation.getAlarm_distance() + "\n" +
                "vibrate: " + globalVariable.isVibrate());

    }

    private void selectItem(int pos) {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = null;
        switch (pos) {
            case 0:
                fragment = new MainActivity();
                break;
            case 1:
                fragment = new AlarmListActivity();
                break;
            case 2:
                fragment = new SettingActivity();
                break;
            case 3:
                fragment = new HelpActivity();
                break;

        }
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
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

    //    public GoogleLocationServiceAPI getGoogleLocationServiceAPI(){
//        return googleLocationServiceAPI;
//    }
    public Handler getNavigationHandler() {
        return mHandler;
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_GPS_RETURN_INFO:
                    Double distance;
                    Location location;
                    location = (Location) msg.obj;
                    userLocation.setUser_location(new LatLng(location.getLatitude(), location.getLongitude()));
                    distance = DistanceOfTwoPoint.calculate(userLocation.getTarget_location().latitude, userLocation.getTarget_location().longitude
                            , userLocation.getUser_location().latitude, userLocation.getUser_location().longitude, 'K');
                    userLocation.setDistance(distance);
                    if (distance < userLocation.getAlarm_distance()) {
                        googleLocationServiceAPI.stop();
                        selectItem(0);
                        userLocation.clear();
                        globalVariable.setAlarmActive(true);
                        globalVariable.setAlarmMessage("You have reach your destination");
                    }
                    break;
                case MSG_GPS_START:
                    googleLocationServiceAPI.start();
                    break;
                case MSG_GPS_STOP:
                    googleLocationServiceAPI.stop();
                    break;
            }

        }
    };
}
