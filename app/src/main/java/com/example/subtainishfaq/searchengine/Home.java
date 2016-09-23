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

import com.ToxicBakery.viewpager.transforms.CubeInTransformer;
import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;
import com.example.subtainishfaq.searchengine.fragments.GoogleFragment;
import com.example.subtainishfaq.searchengine.fragments.BingFragment;
import com.example.subtainishfaq.searchengine.fragments.HomeFragment;
import com.example.subtainishfaq.searchengine.fragments.Settings;


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
    private Settings settingsfraf;


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
         settingsfraf= new Settings();






          tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);


         viewPager = (ViewPager) findViewById(R.id.viewpager);



         setupViewPager(viewPager);






     }
    private void setupViewPager(final ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setPageTransformer(true, new CubeInTransformer());

        adapter.addFragment(homefrag, "Home");
        adapter.addFragment(catfrag, "Google");
        adapter.addFragment(favfrag, "Bing");
        adapter.addFragment(settingsfraf, "Settings");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setupWithViewPager(viewPager);
     //   for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(0).setIcon(R.drawable.home);
            tabLayout.getTabAt(1).setIcon(R.drawable.google);
            tabLayout.getTabAt(2).setIcon(R.drawable.bing);
            tabLayout.getTabAt(2).setIcon(R.drawable.settings);
       // }



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


 public void setFragmentId(int fragmentId)
 {
     viewPager.setCurrentItem(fragmentId);
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


