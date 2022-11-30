package com.example.familyschedulingapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Adapter;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class activity_home extends AppCompatActivity {
    TabLayout tb1;
    TabItem t1,t2;
    ViewPager vp;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        vp=(ViewPager)findViewById(R.id.view);
        tb1=(TabLayout) findViewById(R.id.tabLayout);
        tb1.addTab(tb1.newTab().setText("ACTIVITIES"));
        tb1.addTab(tb1.newTab().setText("LISTS"));
        tb1.setTabGravity(tb1.GRAVITY_FILL);

        final myadapter adap = new myadapter(this,getSupportFragmentManager(), tb1.getTabCount());
        vp.setAdapter(adap);
        vp.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tb1));
        tb1.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
}