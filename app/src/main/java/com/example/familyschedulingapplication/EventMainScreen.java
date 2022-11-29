package com.example.familyschedulingapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
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
    private RecyclerView RecyclerView;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_main_screen);
        RecyclerView= findViewById(R.id.recyclerView);
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
                        powerMenu.dismiss();
                        if(position==0) {
                            Intent intent=new Intent(this,AddEvent.class);
                            startActivity(intent);
                        }
                        if (position==1) {
                            Intent intent=new Intent(this,EditEvent.class);
                            startActivity(intent);
                        }
                        if(position==2){

                        }
                       // loadData();
                    });
                    powerMenu.showAsDropDown(view);
        }
        );
    }
}