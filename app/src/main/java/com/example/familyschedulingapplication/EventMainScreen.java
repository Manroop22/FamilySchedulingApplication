package com.example.familyschedulingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familyschedulingapplication.Adapters.EventAdapter;
import com.example.familyschedulingapplication.ModalBottomSheets.MenuBottomSheet;
import com.example.familyschedulingapplication.Models.Event;
import com.example.familyschedulingapplication.Models.Member;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public class EventMainScreen extends AppCompatActivity {
    private RecyclerView eventRecyclerView;
    static final String TAG="EventMainScreen";
    public String currentTab = "upcoming";
    EventAdapter adapter;
    TabLayout tabLayout;
    TabLayout.Tab tab;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Member member;
    static ArrayList<Event> events = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_main_screen);
        eventRecyclerView = findViewById(R.id.recyclerView);
        tabLayout = findViewById(R.id.tabLayout);
        tab = tabLayout.getTabAt(0);
        if (tab != null) {
            tab.select();
            currentTab = Objects.requireNonNull(tab.getText()).toString().toLowerCase(Locale.ROOT);
        } else {
            currentTab = "upcoming";
        }
        Member.getMember(user.getUid(), task -> {
            member = task.getResult().toObject(Member.class);
            init();
        });
    }

    void init() {
        Objects.requireNonNull(tab).select();
        updateEventList(Objects.requireNonNull(tab.getText()).toString());
        MenuBottomSheet menuBottomSheet = new MenuBottomSheet();
        ImageButton menuBtn = findViewById(R.id.eventsMenuBtn);
        FloatingActionButton eventAddBtn = findViewById(R.id.eventAddBtn);
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
            }
        });
        menuBtn.setOnClickListener(v -> menuBottomSheet.show(getSupportFragmentManager(), MenuBottomSheet.TAG));
        eventAddBtn.setOnClickListener(view -> {
            Intent intent = new Intent(EventMainScreen.this, CreateEvent.class);
            startActivity(intent);
            finish();
        });
    }

    public void updateEventList(String tab) {
        // list events, sort by upcoming, past, and all, return list of events
        // update event list and notify adapter
        // get current user
        EventMainScreen.events = new ArrayList<>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // get user's events
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        assert user != null;
        DocumentReference memRef = db.collection("members").document(user.getUid());
        Event.getEventByHomeId(member.getHomeId(), task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Event event = document.toObject(Event.class);
                    if (tab.equals("upcoming")) {
                        if (event.getEventDate().after(new Date())) {
                            EventMainScreen.events.add(event);
                        }
                    } else if (tab.equals("past")) {
                        if (event.getEventDate().before(new Date())) {
                            EventMainScreen.events.add(event);
                        }
                    } else {
                        EventMainScreen.events.add(event);
                    }
                }
                adapter = new EventAdapter(EventMainScreen.events, R.layout.event_item);
                eventRecyclerView.setAdapter(adapter);
                eventRecyclerView.setLayoutManager(new LinearLayoutManager(EventMainScreen.this));
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
//        Event.getEventByCreatedBy(memRef, task -> {
//            if (task.isSuccessful()) {
////                ArrayList<Event> eventList = new ArrayList<>();
//                for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
//                    Event event = document.toObject(Event.class);
//                    if (event != null && !EventMainScreen.events.contains(event)) {
//                        EventMainScreen.events.add(event);
//                    }
//                }
//                if (tab.equals("upcoming")) {
//                    EventMainScreen.events.removeIf(event -> event.getEventDate().before(new Date()));
//                } else if (tab.equals("past")) {
//                    EventMainScreen.events.removeIf(event -> event.getEventDate().after(new Date()));
//                }
//                adapter = new EventAdapter(EventMainScreen.events, R.layout.event_item);
//                eventRecyclerView.setAdapter(adapter);
//                eventRecyclerView.setLayoutManager(new LinearLayoutManager(EventMainScreen.this));
//            } else {
//                Log.d(TAG, "Error getting documents: ", task.getException());
//            }
//        });
    }

//    public void updateEventRecyclerView(){
//        updateEventList(currentTab);
//    }
}