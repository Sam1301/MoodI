package org.iitb.moodi.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import org.iitb.moodi.MoodIndigoClient;
import org.iitb.moodi.R;
import org.iitb.moodi.api.EventsResponse;
import org.iitb.moodi.ui.fragment.NavigationDrawerFragment;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;


public class MainActivity extends BaseActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private Runnable timerUpdate = new Runnable() {
        @Override
        public void run() {
            long left = 1450396800000L-System.currentTimeMillis(); // Long is MI TIME!
            long days = left/(24*60*60000);
            long hrs = (left/(60*60000)) % 24;
            long mins = (left/(60000)) % 60;

            ((TextView)findViewById(R.id.countdown_days)).setText(String.format("%02d",days));
            ((TextView)findViewById(R.id.countdown_hrs)).setText(String.format("%02d",hrs));
            ((TextView)findViewById(R.id.countdown_min)).setText(String.format("%02d",mins));

            long delay = 60000 - System.currentTimeMillis()%60000;
            timerHandle.postDelayed(timerUpdate,delay);
        }
    };

    private Runnable tickTock = new Runnable() {
        @Override
        public void run() {
            if(findViewById(R.id.countdown_tick).getVisibility() == View.VISIBLE) {
                findViewById(R.id.countdown_tick).setVisibility(View.INVISIBLE);
                findViewById(R.id.countdown_tock).setVisibility(View.INVISIBLE);
            } else {
                findViewById(R.id.countdown_tick).setVisibility(View.VISIBLE);
                findViewById(R.id.countdown_tock).setVisibility(View.VISIBLE);
            }

            long delay = 500 - System.currentTimeMillis()%500;
            timerHandle.postDelayed(tickTock,delay);
        }
    };
    private Handler timerHandle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //collapsingToolbar = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));


        mToolbar.setTitle("Home");

        timerHandle = new Handler(getMainLooper());

        timerHandle.post(timerUpdate);
        timerHandle.post(tickTock);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        navigateTo(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*public void customizeToolbar(int resId, String title, View widget){
        if(mToolbarContainer.getChildCount()>1)
            mToolbarContainer.removeViewAt(mToolbarContainer.getChildCount()-1);
        if(widget!=null)
            mToolbarContainer.addView(widget);

        mTitle = title;
        restoreActionBar();
    }*/

    public void gotoEventList(View v){
        int id = Integer.valueOf((String)v.getTag());
        Intent eventlist = new Intent();
        eventlist.setClass(MainActivity.this, EventsActivity.class);
        eventlist.putExtra("id",id);
        startActivity(eventlist);

        final ProgressDialog dialog = ProgressDialog.show(this, "",
                "Fetching data. Please wait...", true);

        /*File httpCacheDirectory = new File(getBaseContext().getCacheDir(), "responses");

        Cache cache = null;
        try {
            cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);
        } catch (Exception e) {
            Log.e("OKHttp", "Could not create http cache", e);
        }

        OkHttpClient okHttpClient = new OkHttpClient();
        if (cache != null) {
            okHttpClient.setCache(cache);
        }*/
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .build();
        MoodIndigoClient methods = restAdapter.create(MoodIndigoClient.class);
        Callback callback = new Callback() {
            @Override
            public void success(Object o, Response response) {
                EventsResponse c = (EventsResponse)o;
                dialog.dismiss();
                Log.d("EventFetchResponse", c.id+" "+c.genres.length);


            }
            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace();
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "Error fetching data", Toast.LENGTH_LONG).show();
            }
        };
        methods.getEvents(id, callback);
    }
}
