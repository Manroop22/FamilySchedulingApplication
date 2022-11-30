package com.example.familyschedulingapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import java.util.Date;

public class EventMainScreen extends AppCompatActivity {
    private RecyclerView eventRecyclerView;
    private ArrayList<Event> eventList = new ArrayList<>();
    private EventAdapter adapter;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_main_screen);
        eventRecyclerView= findViewById(R.id.recyclerView);
        ModalBottomSheet modalBottomSheet = new ModalBottomSheet();
        ImageButton menuBtn = findViewById(R.id.eventsMenuBtn);
        ImageButton threeDotBtn= findViewById(R.id.threeDotBtn);
        // This will be used to test the EventAdapter that was just created. *************************************
        eventList.add(new Event("Cosc310 project", new Date()));
        adapter=new EventAdapter(eventList);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventRecyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration( this, DividerItemDecoration. VERTICAL);
        eventRecyclerView.addItemDecoration(dividerItemDecoration);

        menuBtn.setOnClickListener(v -> modalBottomSheet.show(getSupportFragmentManager(), ModalBottomSheet.TAG));
        threeDotBtn.setOnClickListener(view -> {
                    ArrayList<PowerMenuItem> list=new ArrayList<>();
                    list.add(new PowerMenuItem("ADD",false));
                    list.add(new PowerMenuItem("EDIT",false));
                    list.add(new PowerMenuItem("DELETE",false));
                    PowerMenu powerMenu = new PowerMenu.Builder(this)
                            .addItemList(list)
                            .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animationewsets the corner radius.
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
    /*
    private void setAdapter() {
        // Set adapter for uncompleted tasks
        // boiler-plate code
        RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.setItemAnimator(new DefaultItemAnimator());

        EventAdapter eventRecyclerAdapter = new EventAdapter(eventList);
        eventRecyclerView.setAdapter(eventRecyclerAdapter);
        eventRecyclerAdapter.setClickListener((EventClickListener) this);
    }

     */
}