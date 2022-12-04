package com.example.familyschedulingapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familyschedulingapplication.Adapters.ActivitiesAdapter;
import com.example.familyschedulingapplication.Adapters.ListsAdapter;
import com.example.familyschedulingapplication.ModalBottomSheets.MenuBottomSheet;
import com.example.familyschedulingapplication.Models.Activity;
import com.example.familyschedulingapplication.Models.List;
import com.example.familyschedulingapplication.Models.Member;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class ListAndActivityMainScreen extends AppCompatActivity {
    TabLayout tabLayout;
    TabLayout.Tab tab;
    String currentTab = "activities";
    RecyclerView lacRecycler;
    FirebaseAuth mAuth;
    FirebaseUser user;
    Member member;
    DocumentReference memberRef;
    ActivitiesAdapter activitiesAdapter;
    ListsAdapter listsAdapter;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        assert user != null;
        member = Member.getMemberByUserId(user.getUid());
        memberRef = db.collection("members").document(user.getUid());
        tabLayout = findViewById(R.id.tabLayout);
        tab = tabLayout.getTabAt(0);
        if (tab != null) {
            tab.select();
            currentTab = Objects.requireNonNull(tab.getText()).toString().toLowerCase(Locale.ROOT);
        } else {
            currentTab = "activities";
        }
        Objects.requireNonNull(tab).select();
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        lacRecycler = findViewById(R.id.lacRecycler);
        updateRecycler(currentTab.toLowerCase(Locale.ROOT));
        ImageButton menuBtn = findViewById(R.id.menuBtnActivity);
        ImageButton addBtn = findViewById(R.id.addActivityBtn);
        addBtn.setOnClickListener(v -> {
            Bundle lacBundle = new Bundle();
            lacBundle.putString("mode", "add");
            lacBundle.putString("listId", null);
            Intent intent;
            if (currentTab.equals("activities")) {
                intent = new Intent(ListAndActivityMainScreen.this, CreateActivity.class);
            } else {
                intent = new Intent(ListAndActivityMainScreen.this, ListDetails.class);
            }
            intent.putExtras(lacBundle);
            startActivity(intent);
            updateRecycler(currentTab.toLowerCase(Locale.ROOT));
        });
        MenuBottomSheet menuBottomSheet = new MenuBottomSheet();
        menuBtn.setOnClickListener(v -> menuBottomSheet.show(getSupportFragmentManager(), MenuBottomSheet.TAG));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = Objects.requireNonNull(tab.getText()).toString().toLowerCase(Locale.ROOT);
                updateRecycler(currentTab.toLowerCase(Locale.ROOT));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                currentTab = Objects.requireNonNull(tab.getText()).toString().toLowerCase(Locale.ROOT);
                updateRecycler(currentTab.toLowerCase(Locale.ROOT));
            }
        });
    }

    public void updateRecycler(String tab) {
        Log.d("updateRecycler", "updateRecycler: " + tab);
        if (tab.equals("lists")) {
            //update recycler
            db.collection("tasks").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    ArrayList<List> tasks = new ArrayList<>();
                    assert querySnapshot != null;
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        if (Objects.equals(document.getDocumentReference("createdBy"), memberRef)) {
                            tasks.add(List.getTaskByReference(document));
                        }
                    }
                    listsAdapter = new ListsAdapter(tasks);
//                    lacRecycler.swapAdapter(listsAdapter, true);
                    lacRecycler.setAdapter(listsAdapter);
                    lacRecycler.setLayoutManager(new LinearLayoutManager(ListAndActivityMainScreen.this));
                    Objects.requireNonNull(lacRecycler.getLayoutManager()).onRestoreInstanceState(lacRecycler.getLayoutManager().onSaveInstanceState());
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            });
        } else if (tab.equals("activities")) {
            //update recycler
            db.collection("activities").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    ArrayList<Activity> activities = new ArrayList<>();
                    for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Activity activity = Activity.getActivity(document);
                        Log.d("ACTIVITY", activity.getName());
                        if (activity.getName() != null) {
                            if ((activity.getCreatedBy() != null && activity.getCreatedBy().equals(memberRef)) || (activity.getInvites() != null && activity.getInvites().contains(memberRef))) {
                                activities.add(activity);
                            }
                        }
                    }
                    activitiesAdapter = new ActivitiesAdapter(activities);
//                    lacRecycler.swapAdapter(activitiesAdapter, true);
                    lacRecycler.setAdapter(activitiesAdapter);
                    lacRecycler.setLayoutManager(new LinearLayoutManager(ListAndActivityMainScreen.this));
                    Objects.requireNonNull(lacRecycler.getLayoutManager()).onRestoreInstanceState(lacRecycler.getLayoutManager().onSaveInstanceState());
                } else {
                    Log.d("ACTIVITY", "Error getting documents: ", task.getException());
                }
            });
        }
    }
}
