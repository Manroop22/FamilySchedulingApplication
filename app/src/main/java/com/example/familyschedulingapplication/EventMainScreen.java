package com.example.familyschedulingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familyschedulingapplication.Adapters.EventAdapter;
import com.example.familyschedulingapplication.ModalBottomSheets.MenuBottomSheet;
import com.example.familyschedulingapplication.Models.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class EventMainScreen extends AppCompatActivity {
    private RecyclerView eventRecyclerView;
    static final String TAG="EventMainScreen";
    public String currentTab = "upcoming";
    EventAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_main_screen);
        eventRecyclerView = findViewById(R.id.recyclerView);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        TabLayout.Tab tab = tabLayout.getTabAt(0);
        if (tab != null) {
            tab.select();
            currentTab = Objects.requireNonNull(tab.getText()).toString().toLowerCase(Locale.ROOT);
        } else {
            currentTab = "upcoming";
        }
        Objects.requireNonNull(tab).select();
        updateEventList(Objects.requireNonNull(tab.getText()).toString());
        MenuBottomSheet menuBottomSheet = new MenuBottomSheet();
        ImageButton menuBtn = findViewById(R.id.eventsMenuBtn);
//        ImageButton eventAddBtn= findViewById(R.id.eventAddBtn);
        FloatingActionButton eventAddBtn = findViewById(R.id.eventAddBtn);
        updateEventList(currentTab);
//        adapter = new EventAdapter(eventList);
//        tabLayout.selectTab(tabLayout.getTabAt(0));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = Objects.requireNonNull(tab.getText()).toString().toLowerCase(Locale.ROOT);
                Log.d("Current Tab", currentTab);
                updateEventList(currentTab.toLowerCase(Locale.ROOT));
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                currentTab = Objects.requireNonNull(tab.getText()).toString().toLowerCase(Locale.ROOT);
                Log.d("Current Tab", currentTab);
                updateEventList(currentTab.toLowerCase(Locale.ROOT));
            }
        });
        menuBtn.setOnClickListener(v -> menuBottomSheet.show(getSupportFragmentManager(), MenuBottomSheet.TAG));
        eventAddBtn.setOnClickListener(view -> {
            Intent intent = new Intent(EventMainScreen.this, CreateEvent.class);
            startActivity(intent);
            updateEventList(currentTab.toLowerCase(Locale.ROOT));
        });
    }

    public void updateEventList(String tab) {
        // list events, sort by upcoming, past, and all, return list of events
        // update event list and notify adapter
        // get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // get user's events
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        assert user != null;
        DocumentReference memRef = db.collection("members").document(user.getUid());
        Event.getEventByCreatedBy(memRef, task -> {
            if (task.isSuccessful()) {
                ArrayList<Event> eventList = new ArrayList<>();
                for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    Event event = document.toObject(Event.class);
                    if (event != null && !eventList.contains(event)) {
                        eventList.add(event);
                    }
                }
                Event.getEventByParticipant(memRef, task1 -> {
                    if (task1.isSuccessful()) {
                        for (DocumentSnapshot document : Objects.requireNonNull(task1.getResult())) {
                            Event event = document.toObject(Event.class);
                            if (event != null && !eventList.contains(event)) {
                                eventList.add(event);
                            }
                        }
                        if (tab.equals("upcoming")) {
                            eventList.removeIf(event -> event.getEventDate().before(new Date()));
                        } else if (tab.equals("past")) {
                            eventList.removeIf(event -> event.getEventDate().after(new Date()));
                        }
                        adapter = new EventAdapter(eventList, R.layout.event_item);
                        eventRecyclerView.setAdapter(adapter);
                        eventRecyclerView.setLayoutManager(new LinearLayoutManager(EventMainScreen.this));
                    }
                });
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

//    public void updateEventRecyclerView(){
//        updateEventList(currentTab);
//    }
}