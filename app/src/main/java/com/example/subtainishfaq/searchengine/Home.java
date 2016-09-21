package com.example.subtainishfaq.searchengine;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import com.example.subtainishfaq.searchengine.fragments.GoogleFragment;
import com.example.subtainishfaq.searchengine.fragments.BingFragment;
import com.example.subtainishfaq.searchengine.fragments.HomeFragment;


import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Home extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private HomeFragment homefrag;
    private GoogleFragment catfrag;
    private BingFragment favfrag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        connectAccordingly();
//my new flicker categgory

//flicker new code end
    }





     private  void connectAccordingly()
     {
         homefrag=  new HomeFragment();
         catfrag= new GoogleFragment();
         favfrag= new BingFragment();



         toolbar = (Toolbar) findViewById(R.id.toolbarhomeid);

         setSupportActionBar(toolbar);

         ActionBar ab =getSupportActionBar();
         ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
         ab.setDisplayHomeAsUpEnabled(true);
         ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
         ab.setDisplayShowTitleEnabled(false);
         ab.setHideOnContentScrollEnabled(false);//

          tabLayout = (TabLayout) findViewById(R.id.tab_layout);
         tabLayout.addTab(tabLayout.newTab().setText("Home"));
         tabLayout.addTab(tabLayout.newTab().setText("Google"));
         tabLayout.addTab(tabLayout.newTab().setText("Bing"));
         tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


         viewPager = (ViewPager) findViewById(R.id.viewpager);


         setupViewPager(viewPager);






     }
    private void setupViewPager(final ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(homefrag, "Home");
        adapter.addFragment(catfrag, "Google");
        adapter.addFragment(favfrag, "Bing");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setupWithViewPager(viewPager);



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


 public void setCurrentHome()
 {
     viewPager.setCurrentItem(0);
 }



    @Override
    protected void onDestroy() {
        super.onDestroy();

    }




    @Override
    public void setTitle(CharSequence title) {
       getSupportActionBar().setTitle(title);
    }


}


