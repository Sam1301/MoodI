package org.iitb.moodi.ui.activity;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.LinearLayout;

import org.iitb.moodi.ui.fragment.BaseFragment;
import org.iitb.moodi.ui.fragment.EventListFragment;
import org.iitb.moodi.ui.fragment.HomeFragment;
import org.iitb.moodi.ui.fragment.NavigationDrawerFragment;
import org.iitb.moodi.R;
import org.iitb.moodi.ui.fragment.ScheduleFragment;
import org.iitb.moodi.ui.fragment.TimelineFragment;


public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
                   BaseFragment.InteractionListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    public Toolbar toolbar;
    public LinearLayout toolbarContainer;

    private BaseFragment mCurrentFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Intent i = new Intent(getBaseContext(), RegistrationActivity.class);
        //startActivity(i);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //collapsingToolbar = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        toolbarContainer = (LinearLayout) findViewById(R.id.toolbar_container);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, HomeFragment.newInstance())
                .commit();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        if(position==1) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, ScheduleFragment.newInstance())
                    .commit();
        }
    }

    public void onSectionAttached(int id) {
        //findViewById(R.id.countdown_container).setVisibility(View.GONE);
        switch (id) {
            case R.layout.fragment_main:
                //mTitle = "MOOD INDIGO";
                //findViewById(R.id.countdown_container).setVisibility(View.VISIBLE);
                //toolbar.setBackgroundResource(R.drawable.toolbar_shadow_gradient);
                break;
        }
    }

    public void restoreActionBar() {
        toolbar.setTitle(mTitle);
    }

    private void switchContent(BaseFragment fragment){
        mCurrentFragment=fragment;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
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

    public void customizeToolbar(int resId, String title, View widget){
        if(toolbarContainer.getChildCount()>1)
            toolbarContainer.removeViewAt(toolbarContainer.getChildCount()-1);
        if(widget!=null)
            toolbarContainer.addView(widget);

        mTitle = title;
        restoreActionBar();
    }

    public void gotoEventList(View v){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, EventListFragment.newInstance())
                .commit();
    }

    public void gotoTimeline(View v){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, TimelineFragment.newInstance())
                .commit();
    }

    @Override
    public void onFragmentLoaded(BaseFragment fragment) {
        fragment.customizeToolbarLayout(toolbarContainer);
    }
}
