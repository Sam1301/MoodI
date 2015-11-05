package org.iitb.moodi.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.iitb.moodi.R;
import org.iitb.moodi.ui.fragment.BaseFragment;
import org.iitb.moodi.ui.fragment.EventListFragment;
import org.iitb.moodi.ui.fragment.HomeFragment;
import org.iitb.moodi.ui.fragment.NavigationDrawerFragment;
import org.iitb.moodi.ui.fragment.ScheduleFragment;
import org.iitb.moodi.ui.fragment.TimelineFragment;
import org.iitb.moodi.ui.widget.ToolbarWidgetLayout;


public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
                   BaseFragment.InteractionListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;
    private ToolbarWidgetLayout mToolbarContainer;

    private BaseFragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent i = new Intent(getBaseContext(), RegistrationActivity.class);
        startActivity(i);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //collapsingToolbar = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        mToolbarContainer = (ToolbarWidgetLayout) findViewById(R.id.toolbar_container);
        mToolbar = mToolbarContainer.getToolbar();
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        switchContent(HomeFragment.newInstance());
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        if(position==1) {
            switchContent(ScheduleFragment.newInstance());
        } else if(position==2){
            startActivity(new Intent(this,MapsActivity.class));
        }
    }

    public void onSectionAttached(int id) {
        //findViewById(R.id.countdown_container).setVisibility(View.GONE);
        switch (id) {
            case R.layout.fragment_main:
                //mTitle = "MOOD INDIGO";
                //findViewById(R.id.countdown_container).setVisibility(View.VISIBLE);
                //mToolbar.setBackgroundResource(R.drawable.toolbar_shadow_gradient);
                break;
        }
    }

    private void switchContent(BaseFragment fragment){
        mCurrentFragment=fragment;
        fragment.setInteractionListener(this);

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
        switchContent(EventListFragment.newInstance());
    }

    public void gotoTimeline(View v){
        switchContent(TimelineFragment.newInstance());
    }

    @Override
    public void onFragmentLoaded(BaseFragment fragment) {
        fragment.customizeToolbarLayout(mToolbarContainer);
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }
}
