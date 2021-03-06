package org.iitb.moodi.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import org.iitb.moodi.MoodIndigoClient;
import org.iitb.moodi.R;
import org.iitb.moodi.Util;
import org.iitb.moodi.api.Event;
import org.iitb.moodi.api.EventsResponse;
import org.iitb.moodi.api.Genre;
import org.iitb.moodi.api.TimelineResponse;
import org.iitb.moodi.ui.fragment.NavigationDrawerFragment;
import org.iitb.moodi.ui.widget.EventListAdapter;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by udiboy on 30/11/15.
 */
public class TimelineActivity extends BaseActivity {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private TimelineResponse timelineData;
    private ArrayList<TimelineAdapter> eventLists;

    private int day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);


        mTabLayout = (TabLayout) findViewById(R.id.timeline_tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.timeline_pager);

        mTabLayout.setBackgroundResource(mColorPrimary);

        day=getIntent().getIntExtra("day",0);
        loadTimelineData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mToolbar.setTitle("Day "+day);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    public String getDept(int pos){
        switch(pos){
            case 0:
                return "competitions";
            case 1:
                return "informals";
            case 2:
                return "proshows";
            default:
                return "udirox";
        }
    }

    public void loadTimelineData(){
        final ProgressDialog dialog = ProgressDialog.show(this, "",
                "Fetching data. Please wait...", true);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(m_API_URL)
                .build();
        MoodIndigoClient methods = restAdapter.create(MoodIndigoClient.class);
        Callback callback = new Callback() {
            @Override
            public void success(Object o, Response response) {
                timelineData = (TimelineResponse) o;

                eventLists = new ArrayList<>();

                ArrayList<TimelineResponse.EventTime> sortedEvents = Util.mergeSort(new ArrayList<>(Arrays.asList(timelineData.events)));

                for(int i=0; i<3; i++){
                    String dept = getDept(i);
                    TimelineAdapter ta = new TimelineAdapter(TimelineActivity.this, R.layout.list_item_timeline);
                    for(TimelineResponse.EventTime e : sortedEvents){
                        if(e.dept.equals(dept) && e.day.equals(day+"")) {
                            Log.d("EventDebug","Start hr:"+e.getStartHrs());
                            Log.d("EventDebug",e.name+" Start time:"+e.getStart().getHours()+":"+e.getStart().getMinutes());
                            ta.add(e);
                        }
                    }
                    eventLists.add(ta);
                }

                mViewPager.setAdapter(new SamplePagerAdapter());
                mTabLayout.setupWithViewPager(mViewPager);

                dialog.dismiss();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace();
                dialog.dismiss();
                showErrorDialog("Error fetching data. Check your internet connection.");
            }
        };
        methods.getTimeline(callback);
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    class TimelineAdapter extends ArrayAdapter<TimelineResponse.EventTime> implements SectionIndexer{

        public TimelineAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            TimelineResponse.EventTime e = getItem(position);
            if(v==null) {
                LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item_timeline, parent, false);
            }


            int sec=getSectionForPosition(position);
            if(position == getPositionForSection(sec)){
                Log.d("TimelineDebug",position+" : section position");
                TextView timehrs = (TextView) v.findViewById(R.id.event_list_item_time_hrs);
                TextView ampmtv = (TextView) v.findViewById(R.id.event_list_item_time_ampm);
                int hrs=e.getStartHrs();
                int ampmtime = hrs>12?hrs-12:hrs;
                String ampm = hrs>12?"pm":"am";
                timehrs.setText(String.format("%02d",ampmtime));
                ampmtv.setText(ampm);
                v.findViewById(R.id.event_list_item_section_label).setVisibility(View.VISIBLE);
            } else {
                Log.d("TimelineDebug",position+" : normal position");
                v.findViewById(R.id.event_list_item_section_label).setVisibility(View.INVISIBLE);
            }


            TextView time = (TextView) v.findViewById(R.id.event_list_item_time);
            if(time!=null) time.setText(
                    String.format("%02d:%02d - %02d:%02d",
                            e.getStart().getHours(),
                            e.getStart().getMinutes(),
                            e.getEnd().getHours(),
                            e.getEnd().getMinutes()));
            else Log.d("TimelineDebug","time is null");

            TextView name = (TextView) v.findViewById(R.id.event_list_item_name);
            if(name != null) name.setText(e.name);
            else Log.d("TimelineDebug","name is null");

            TextView venue = (TextView) v.findViewById(R.id.event_list_item_venue);
            if(venue != null) venue.setText(e.venue_name);
            else Log.d("TimelineDebug","venue is null");

            TextView description = (TextView) v.findViewById(R.id.event_list_item_description);
            if(description != null) description.setText(e.intro);
            else Log.d("TimelineDebug","description is null");

            return v;
        }

        @Override
        public String[] getSections() {
            return new String[]{
                    "08",
                    "09",
                    "10",
                    "11",
                    "12",
                    "13",
                    "14",
                    "15",
                    "16",
                    "17",
                    "18",
                    "19",
                    "20",
                    "21",
                    "22",
                    "23",
                    "00"};
        }

        @Override
        public int getPositionForSection(int sectionIndex) {
            if(sectionIndex>=getSections().length)
                return getCount()-1;

            Log.d("getPositionForSection","secIndex:"+sectionIndex);
            int section = hrFromSec(getSections()[sectionIndex]);
            Log.d("getPositionForSection","sec:"+section);
            for (int i = 0; i < getCount(); i++) {
                if(section<=getItem(i).getStart().getHours())
                    return i;
            }
            return getCount()-1;
        }

        @Override
        public int getSectionForPosition(int position) {
            String[] secs = getSections();
            int hr = getItem(position).getStartHrs();
            Log.d("getSectionForPosition","hr:"+hr);
            for(int i=0; i<secs.length; i++){
                if(hrFromSec(secs[i])==hr)
                    return i;
            }

            return secs.length-1;
        }

        public int hrFromSec(String sec){
            int hr=Integer.valueOf(sec);
            return hr;
        }
    }

    class SamplePagerAdapter extends PagerAdapter {
        public static final String TAG = "EventListPagerAdapter";

        /**
         * @return the number of pages to display
         */
        @Override
        public int getCount() {
            return 3;
        }

        /**
         * @return true if the value returned from {@link #instantiateItem(ViewGroup, int)} is the
         * same object as the {@link View} added to the {@link ViewPager}.
         */
        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        /**
         * Return the title of the item at {@code position}. This is important as what this method
         * returns is what is displayed in the {@link TabLayout}.
         * <p>
         * Here we construct one using the position value, but for real application the title should
         * refer to the item's contents.
         */
        @Override
        public CharSequence getPageTitle(int position) {
            switch(position){
                case 0:
                    return "Competitions";
                case 1:
                    return "Informals";
                case 2:
                    return "Horizons";
                default:
                    return "udirox";
            }
        }

        /**
         * Instantiate the {@link View} which should be displayed at {@code position}. Here we
         * inflate a layout from the apps resources and then change the text view to signify the position.
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // Inflate a new layout from our resources
            ListView view = new ListView(container.getContext(),null,R.style.EventListView);
            view.setAdapter(eventLists.get(position));
            view.setFastScrollEnabled(true);
            view.setDividerHeight(0);
            if( Build.VERSION.SDK_INT>=21) view.setFastScrollStyle(R.style.FastScroll);

            // Add the newly created View to the ViewPager
            container.addView(view);

            Log.i(TAG, "instantiateItem() [position: " + position + "]");

            // Return the View
            return view;
        }

        /**
         * Destroy the item from the {@link ViewPager}. In our case this is simply removing the
         * {@link View}.
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            Log.i(TAG, "destroyItem() [position: " + position + "]");
        }
    }
}
