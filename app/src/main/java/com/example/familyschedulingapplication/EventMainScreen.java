package com.example.familyschedulingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familyschedulingapplication.Adapters.EventAdapter;
import com.example.familyschedulingapplication.ModalBottomSheets.MenuBottomSheet;
import com.example.familyschedulingapplication.Models.Event;
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
            Intent intent = new Intent(EventMainScreen.this, AddEvent.class);
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
        DocumentReference memRef = db.collection("members").document(user.getUid());
        db.collection("events").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot document = task.getResult();
                ArrayList<Event> eventList = new ArrayList<>();
                for(DocumentSnapshot doc : Objects.requireNonNull(document).getDocuments()) {
                    // if doc's createdBy is current user, add to list
                    // if doc's partcipants contains memRef, add to list
                    ArrayList<DocumentReference> people = new ArrayList<>();
                    for (String s : Objects.requireNonNull(doc.get("participants")).toString().split(",")) {
                        people.add(db.collection("participants").document(s));
                    }
                    if (Objects.equals(doc.get("createdBy"), memRef) || Objects.requireNonNull(people).contains(memRef)) {
                        Event currEvent = Event.getEvent(doc);
                        switch(tab) {
                            case "upcoming":
                                if (Objects.requireNonNull(doc.getDate("eventDate")).after(new Date())) {
                                    eventList.add(currEvent);
                                }
                                break;
                            case "past":
                                if (Objects.requireNonNull(doc.getDate("eventDate")).before(new Date())) {
                                    eventList.add(currEvent);
                                }
                                break;
                            case "all":
                                eventList.add(currEvent);
                                break;
                        }
                    }
                }
                Log.d(TAG, String.valueOf(eventList.size()));
                adapter = new EventAdapter(eventList);
                eventRecyclerView.setAdapter(adapter);
                eventRecyclerView.setLayoutManager(new LinearLayoutManager(EventMainScreen.this));
//                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration( this, DividerItemDecoration. VERTICAL);
//                eventRecyclerView.addItemDecoration(dividerItemDecoration);
                Objects.requireNonNull(eventRecyclerView.getLayoutManager()).onRestoreInstanceState(eventRecyclerView.getLayoutManager().onSaveInstanceState());
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

//    public void updateEventRecyclerView(){
//        updateEventList(currentTab);
//    }
}