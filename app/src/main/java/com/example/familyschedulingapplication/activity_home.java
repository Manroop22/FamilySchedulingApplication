package com.example.familyschedulingapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Adapter;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

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

    public void onClick(View view) {
        ArrayList<PowerMenuItem> list=new ArrayList<>();
        list.add(new PowerMenuItem("List",false));
        list.add(new PowerMenuItem("Activities",false));
        PowerMenu powerMenu = new PowerMenu.Builder(itemView.getContext())
                .addItemList(list) // list has "Novel", "Poetry", "Art"
                .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animation start point (TOP | LEFT).
                .setMenuRadius(10f) // sets the corner radius.
                .setMenuShadow(10f) // sets the shadow.
                .setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.black))
                .setTextGravity(Gravity.CENTER)
                .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                .setSelectedTextColor(Color.WHITE)
                .setMenuColor(Color.WHITE)
                .setSelectedMenuColor(ContextCompat.getColor(itemView.getContext(), R.color.purple_500)).build();
        powerMenu.setOnMenuItemClickListener((position, item) -> {
            powerMenu.dismiss();
            if(position==0) {

            }
            if (position==1){

        });
        powerMenu.showAsDropDown(view);
    }
    }
}
