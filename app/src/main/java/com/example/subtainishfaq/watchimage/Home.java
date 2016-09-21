package com.example.subtainishfaq.watchimage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.subtainishfaq.watchimage.adapters.CustomListAdapter;
import com.example.subtainishfaq.watchimage.adapters.GridViewAdapter;
import com.example.subtainishfaq.watchimage.fragments.CategoryFragment;
import com.example.subtainishfaq.watchimage.fragments.FavouriteFragment;
import com.example.subtainishfaq.watchimage.fragments.HomeFragment;
import com.example.subtainishfaq.watchimage.utility.CustomViewPager;
import com.example.subtainishfaq.watchimage.utility.GlobalSettings;
import com.example.subtainishfaq.watchimage.utility.TinyDB;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.groups.Group;
import com.googlecode.flickrjandroid.photosets.Photoset;
import com.googlecode.flickrjandroid.photosets.Photosets;
import com.onesignal.OneSignal;


import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Home extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private CustomViewPager viewPager;
    private HomeFragment homefrag;
    private CategoryFragment catfrag;
    private FavouriteFragment favfrag;
    private boolean isSetUP=false;
    private ImageButton refreshButton;
    private ImageButton settings;
    private ImageButton settingsActual;
    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    ArrayList <String> grouids;
    Collection<Group> groupList=null;
    List<Photoset> groupLista=null;
    private TinyDB db;
    GridView gridList;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        this.registerReceiver(mWifiStateChangedReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        db=new TinyDB(this);


        if(isNetworkConnected()){

            connectAccordingly();
            GlobalSettings.isConnected=true;

        }
        else
        {

            GlobalSettings.isConnected=false;
            connectAccordingly();
        }

//my new flicker categgory
        mPlanetTitles = getResources().getStringArray(R.array.media_names);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,

                R.string.app_name,
                R.string.app_name);
        mDrawerToggle.syncState();
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        // Set the adapter for the list view
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());



        if(GlobalSettings.isConnected )
        {
            RetrieveFeedTask  task1=  new RetrieveFeedTask();
            task1.execute();
           }
        else
        {
            mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, mPlanetTitles));

         }


//flicker new code end
    }





     private  void connectAccordingly()
     {OneSignal.startInit(this).init();
         homefrag=  new HomeFragment();
         catfrag= new CategoryFragment();
         favfrag= new FavouriteFragment();



         toolbar = (Toolbar) findViewById(R.id.toolbarhomeid);
         refreshButton= (ImageButton) toolbar.findViewById(R.id.refreshbutton);
         settings= (ImageButton) toolbar.findViewById(R.id.settingsButton);
         settingsActual= (ImageButton) toolbar.findViewById(R.id.settingsButtonactual);
         setSupportActionBar(toolbar);

         ActionBar ab =getSupportActionBar();
         ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
         ab.setDisplayHomeAsUpEnabled(true);
         ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
         ab.setDisplayShowTitleEnabled(false);
         ab.setHideOnContentScrollEnabled(false);//

         viewPager = (CustomViewPager) findViewById(R.id.viewpager);
         viewPager.setPagingEnabled(false);
         setupViewPager(viewPager);

      /*   tabLayout = (TabLayout) findViewById(R.id.tabs);
         tabLayout.setupWithViewPager(viewPager);*/



         refreshButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 viewPager.setCurrentItem(2);

             }
         });
         settings.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v)
             {

                 viewPager.setCurrentItem(2);


             }
         });

     }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(homefrag, getString(R.string.tab_home));
        adapter.addFragment(catfrag, getString(R.string.tab_categories));
        adapter.addFragment(favfrag, getString(R.string.tab_favourites));
        viewPager.setAdapter(adapter);

        isSetUP=true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }




    @Override
    public void onBackPressed() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Do you want to exit!")
                .setConfirmText("Yes!")
                .setCancelText("No!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        Home.super.onBackPressed();
                    }
                })
                .show();
    }

    private void refresh()
    { final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                pDialog.dismiss();
            }}, 3000);

        if(isSetUP && GlobalSettings.isConnected)
        {

            catfrag.refresh();
            homefrag.refresh();

        }
        else
             if(!GlobalSettings.isConnected)
             {

                 new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                         .setTitleText("Internet Not Connected")
                         .setContentText("Connect to internet to refresh")
                         .setConfirmText("ok!")
                         .show();
             }
    }

 public void setCurrentHome()
 {
     viewPager.setCurrentItem(0);
 }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }


    private BroadcastReceiver mWifiStateChangedReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent) {

            final ConnectivityManager connMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            final android.net.NetworkInfo wifi = connMgr
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            final android.net.NetworkInfo mobile = connMgr
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (wifi.isAvailable() || mobile.isAvailable()) {
                // Do something


                    GlobalSettings.isConnected=true;

                Log.d("Network Available ", "Flag No 1");
            }
            else
            {
                Log.d("WifiReceiver", "Don't have Internet Connection");
                GlobalSettings.isConnected=false;
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Internet Not Connected")
                        .setContentText("Won't be able to perform effectively")
                        .setConfirmText("ok!")
                        .show();

            }

        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(this.mWifiStateChangedReceiver);
    }



    class RetrieveFeedTask extends AsyncTask<Void, Void, ArrayList<String> > {

        private Exception exception;


        @Override
        protected ArrayList<String> doInBackground(Void... params) {

            Flickr f=new Flickr(Cons.FlickerApi);
            Photosets groupList=null;

            try {
                groupList=  f.getPhotosetsInterface().getList(Cons.UserFlicker);
                groupLista=new ArrayList<Photoset>(groupList.getPhotosets());
                grouids=new ArrayList<String>();

                //get pages
                //get perpage

            } catch (IOException e) {
                e.printStackTrace();
            } catch (FlickrException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ArrayList <String> urls=new ArrayList<String>();
            // names=new ArrayList<String>();

            for (Photoset photo:groupLista)
            {
                urls.add(photo.getTitle());
                Log.d("group", photo.getTitle());
                grouids.add(photo.getId());
                // names.add(photo.getTitle());
            }


            return   urls;
        }

        protected void onPostExecute(ArrayList<String> feed) {
            // TODO: check this.exception
            // TODO: do something with the feed
           /* urlPublic=feed;
            gridList.setAdapter(new GridViewAdapter(getContext(),feed));*/
            mPlanetTitles=  feed.toArray((new String[feed.size()]));

            mDrawerList.setAdapter(new ArrayAdapter<String>(Home.this,
                    android.R.layout.simple_list_item_1, mPlanetTitles));
            db.putListString("GROUPS", feed);
            db.putListString("GROUPIDS", grouids);



        }
    }
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        // Create a new fragment and specify the planet to show based on position
        // Highlight the selected item, update the title, and close the drawer

        mDrawerList.setItemChecked(position, true);
        setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
        homefrag.openCategory(groupLista.get(position).getId());
        viewPager.setCurrentItem(0);
        Log.d("title",mPlanetTitles[position]);
    }

    @Override
    public void setTitle(CharSequence title) {
       getSupportActionBar().setTitle(title);
    }


}


