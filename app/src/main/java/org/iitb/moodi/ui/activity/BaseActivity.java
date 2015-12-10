package org.iitb.moodi.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.facebook.login.LoginManager;

import org.iitb.moodi.BaseApplication;
import org.iitb.moodi.DatabaseHandler;
import org.iitb.moodi.R;
import org.iitb.moodi.api.CityResponse;
import org.iitb.moodi.api.User;
import org.json.JSONObject;

import java.util.ArrayList;

public class BaseActivity extends AppCompatActivity {

    public static ArrayList<BaseActivity> activityStack=new ArrayList<>();

    public User me;
    public CityResponse.City[] cities;
    public final String API_URL = "https://moodi.org";
    public final String m_API_URL = "http://m.moodi.org";
    SharedPreferences prefs;
    DatabaseHandler db;
    private static final String TAG = "BaseActivity";
    Toolbar mToolbar;
    View mSystemBarBG;

    @DrawableRes
    protected int mColorPrimary=R.color.colorPrimary;
    @ColorRes
    protected int mColorPrimaryDark=R.color.colorSecondary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityStack.add(this);
        me = new User();
        prefs=getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE);
        db=BaseApplication.getDB();
        try {
            me=new User(new JSONObject(prefs.getString("user_json","")));
            Log.d("BaseActivity", me.getJSON());
        }
        catch (Exception e) {
            e.printStackTrace();
            me = new User();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityStack.remove(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSystemBarBG=findViewById(R.id.system_bar_bg);

        if(Build.VERSION.SDK_INT >= 21 ) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(mColorPrimaryDark));
        } else if(Build.VERSION.SDK_INT >= 19 ){
            int result = 0;
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = getResources().getDimensionPixelSize(resourceId);
            }
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            if(mSystemBarBG!=null) {
                mSystemBarBG.getLayoutParams().height=result;
                mSystemBarBG.setBackgroundResource(mColorPrimaryDark);
            }
        }

        if(mToolbar!=null) mToolbar.setBackgroundResource(mColorPrimary);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.action_faq:
                String url = "http://faq.moodi.org";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return true;
            case R.id.action_share:
                startActivity(new Intent(this, ShareActivity.class));
                return true;
            case R.id.action_logout:
                LoginManager.getInstance().logOut();
                prefs.edit().remove("user_json").remove("user_exists").apply();
                BaseApplication.getDB().clear();
                for(BaseActivity a : activityStack)
                    a.finish();
                startActivity(new Intent(this, SplashActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    public int getColorResource(int id){
        switch(id){
            case 1:
                return R.color.color_compi;
            case 2:
                return R.color.color_informals;
            case 3:
                return R.color.color_concerts;
            case 4:
                return R.color.color_arts;
            case 5:
                return R.color.color_proshows;
        }
        return R.color.color_compi;
    }

    public int getColorFromGenre(String genre){
        if(genre.equals("competitions"))
            return R.color.color_compi;
        else if(genre.equals("informals"))
            return R.color.color_informals;
        else if(genre.equals("concerts"))
            return R.color.color_concerts;
        else if(genre.equals("proshows"))
            return R.color.color_proshows;
        else if(genre.equals("arts"))
            return R.color.color_arts;
        else
            return R.color.color_compi;
    }

    public int getColorDarkFromGenre(String genre){
        if(genre.equals("competitions"))
            return R.color.color_compiDark;
        else if(genre.equals("informals"))
            return R.color.color_informalsDark;
        else if(genre.equals("concerts"))
            return R.color.color_concertsDark;
        else if(genre.equals("proshows"))
            return R.color.color_proshowsDark;
        else if(genre.equals("arts"))
            return R.color.color_artsDark;
        else
            return R.color.color_compiDark;
    }

    public int getBackgroundResource(int id){
        switch(id){
            case 1:
                return R.drawable.bg_compi;
            case 2:
                return R.drawable.bg_informals;
            case 3:
                return R.drawable.bg_concerts;
            case 4:
                return R.drawable.bg_arts;
            case 5:
                return R.drawable.bg_proshows;
        }
        return R.drawable.bg_compi;
    }

    public int getIconResource(int id){
        switch(id){
            case 1:
                return R.drawable.list_icon_compi;
            case 2:
                return R.drawable.list_icon_informals;
            case 3:
                return R.drawable.list_icon_concerts;
            case 4:
                return R.drawable.list_icon_arts;
            case 5:
                return R.drawable.list_icon_proshows;
        }
        return R.drawable.list_icon_compi;
    }

    public void navigateTo(int p){
        if(p==-1){
            if(!(this instanceof ProfileActivity))
                startActivity(new Intent(this,ProfileActivity.class));
        } else if(p==0){
            if(!(this instanceof MainActivity)) {
                //startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        } else if(p==1){
            if(!(this instanceof MapsActivity))
                startActivity(new Intent(this,MapsActivity.class));
        } else if(p==2){
            if(!(this instanceof ScheduleActivity))
                startActivity(new Intent(this,ScheduleActivity.class));
        } else if(p==3){
           if(!(this instanceof ResultActivity))
                startActivity(new Intent(this,ResultActivity.class));
        } else if(p==4){
            if(!(this instanceof QRActivity))
                startActivity(new Intent(this,QRActivity.class));
        } else if(p==5){
            if(!(this instanceof ProfileActivity))
                startActivity(new Intent(this,ProfileActivity.class));
        } else if(p==6){
            if(!(this instanceof ContactActivity))
                startActivity(new Intent(this,ContactActivity.class));
        } else if(p==7){
            String url = "http://moodi.org/accommodation";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } else if(p==8){
            if(!(this instanceof AboutActivity))
                startActivity(new Intent(this,AboutActivity.class));
        }
    }

    public void showErrorDialog(String content){
        new AlertDialog.Builder(this).setTitle("Internet Error").setMessage(content)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
