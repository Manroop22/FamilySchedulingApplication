package com.example.familyschedulingapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class EventMainScreen extends AppCompatActivity {
    private RecyclerView eventRecyclerView;
    static String TAG="Number of events:";
    public String currentTab = "Upcoming";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_main_screen);
        eventRecyclerView = findViewById(R.id.recyclerView);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ModalBottomSheet modalBottomSheet = new ModalBottomSheet();
        ImageButton menuBtn = findViewById(R.id.eventsMenuBtn);
        ImageButton eventAddBtn= findViewById(R.id.eventAddBtn);
        updateEventList(currentTab);
//        adapter = new EventAdapter(eventList);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration( this, DividerItemDecoration. VERTICAL);
        eventRecyclerView.addItemDecoration(dividerItemDecoration);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = Objects.requireNonNull(tab.getText()).toString().toLowerCase(Locale.ROOT);
                Log.d("Current Tab", currentTab);
                updateEventRecyclerView();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                currentTab = Objects.requireNonNull(tab.getText()).toString().toLowerCase(Locale.ROOT);
                Log.d("Current Tab", currentTab);
                updateEventRecyclerView();
            }
        });
        menuBtn.setOnClickListener(v -> modalBottomSheet.show(getSupportFragmentManager(), ModalBottomSheet.TAG));
        eventAddBtn.setOnClickListener(view -> {
            Intent intent = new Intent(EventMainScreen.this, AddEvent.class);
            startActivity(intent);
            // I don't know how to handle stuff coming back from the other activity and then use it to change the eventRecyclerView.
            updateEventRecyclerView();
        });
    }

    public void updateEventList(final String tab) {
        // This will be used to test the EventAdapter that was just created. *************************************
//        eventList = Event.getEventsByDate(new Date());
        // past events
        // upcoming events
        ArrayList<Event> tempEvents = new ArrayList<>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference memRef = db.collection("members").document(user.getUid());
        ArrayList<DocumentSnapshot> allEvents = new ArrayList<>();
        db.collection("events").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // populate all events
                for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
//                    Log.d(TAG, document.getId() + " => " + document.getData());
                    allEvents.add(document);
                }

                Log.d(TAG, "All Events: " + allEvents);
                ArrayList<Event> eventList;
                switch (tab) {
                    case "upcoming":
                        for(DocumentSnapshot event : allEvents) {
                            if (event.get("eventDate") != null) {
                                Date eventDate = event.getDate("eventDate");
                                assert eventDate != null;
                                if (eventDate.after(new Date())) {
                                    tempEvents.add(Event.getEvent(event));
                                }
                            }
                        }
                        break;
                    case "past":
                        for(DocumentSnapshot event : allEvents) {
                            if (event.get("eventDate") != null) {
                                Date eventDate = event.getDate("eventDate");
                                assert eventDate != null;
                                if (eventDate.before(new Date())) {
                                    tempEvents.add(Event.getEvent(event));
                                }
                            }
                        }
                        break;
                    case "all":
                        for(DocumentSnapshot event : allEvents) {
                            tempEvents.add(Event.getEvent(event));
                        }
                        break;
                }
                eventList = tempEvents;
                EventAdapter adapter = new EventAdapter(eventList);
//        adapter.notifyDataSetChanged();
//        eventRecyclerView.setAdapter(adapter);
                eventRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                eventRecyclerView.setAdapter(adapter);
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    public void updateEventRecyclerView(){
        updateEventList(currentTab);
        Objects.requireNonNull(eventRecyclerView.getLayoutManager()).onRestoreInstanceState(eventRecyclerView.getLayoutManager().onSaveInstanceState());
    }
}