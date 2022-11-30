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
        ImageButton eventAddBtn= findViewById(R.id.eventAddBtn);
        // This will be used to test the EventAdapter that was just created. *************************************
        eventList.add(new Event("Cosc310 project", new Date()));
        adapter=new EventAdapter(eventList);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventRecyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration( this, DividerItemDecoration. VERTICAL);
        eventRecyclerView.addItemDecoration(dividerItemDecoration);

        menuBtn.setOnClickListener(v -> modalBottomSheet.show(getSupportFragmentManager(), ModalBottomSheet.TAG));
        eventAddBtn.setOnClickListener(view -> {
            Intent intent=new Intent(this,AddEvent.class);
            intent.putExtra("eventList",eventList);
            startActivity(intent);
        });
    }
}