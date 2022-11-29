package com.example.familyschedulingapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageButton;
import com.skydoves.powermenu.CustomPowerMenu;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;
import java.util.ArrayList;

public class EventMainScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_main_screen);
        ModalBottomSheet modalBottomSheet = new ModalBottomSheet();
        ImageButton menuBtn = findViewById(R.id.eventsMenuBtn);
        ImageButton threeDotBtn= findViewById(R.id.threeDotBtn);
        menuBtn.setOnClickListener(v -> modalBottomSheet.show(getSupportFragmentManager(), ModalBottomSheet.TAG));
        threeDotBtn.setOnClickListener(view -> {
                    ArrayList<PowerMenuItem> list=new ArrayList<>();
                    list.add(new PowerMenuItem("ADD",false));
                    list.add(new PowerMenuItem("EDIT",false));
                    list.add(new PowerMenuItem("DELETE",false));
                    PowerMenu powerMenu = new PowerMenu.Builder(this)
                            .addItemList(list)
                            .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animation start point (TOP | LEFT).
                            .setMenuRadius(10f) // sets the corner radius.
                            .setMenuShadow(10f) // sets the shadow.
                            .setTextColor(ContextCompat.getColor(this, R.color.black))
                            .setTextGravity(Gravity.CENTER)
                            .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                            .setSelectedTextColor(Color.WHITE)
                            .setMenuColor(Color.WHITE)
                            .setSelectedMenuColor(ContextCompat.getColor(this, R.color.purple_500)).build();
                    powerMenu.setOnMenuItemClickListener((position, item) -> {
                       /* powerMenu.dismiss();
                        if(position==0)
                            sortingType = 0;
                        if (position==1)
                            sortingType = 1;
                        if(position==2)
                            sortingType=2;
                        loadData();

                        */
                    });
                    powerMenu.showAsDropDown(view);
        }
        );
    }
}