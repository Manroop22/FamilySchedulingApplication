package com.example.familyschedulingapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familyschedulingapplication.Adapters.ActivitiesAdapter;
import com.example.familyschedulingapplication.Adapters.ListsAdapter;
import com.example.familyschedulingapplication.ModalBottomSheets.MenuBottomSheet;
import com.example.familyschedulingapplication.Models.Activity;
import com.example.familyschedulingapplication.Models.DBTask;
import com.example.familyschedulingapplication.Models.Member;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import com.google.android.material.tabs.TabLayout;
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
        ImageButton menuBtn = findViewById(R.id.menuBtnActivity);
        ImageButton addBtn = findViewById(R.id.addActivityBtn);
        addBtn.setOnClickListener(v -> {
            Bundle lacBundle = new Bundle();
            lacBundle.putString("mode", "new");
            Intent intent;
            if (currentTab.equals("activities")) {
                intent = new Intent(ListAndActivityMainScreen.this, CreateActivity.class);
            } else {
                intent = new Intent(ListAndActivityMainScreen.this, List_create.class);
            }
            startActivity(intent);
        });
        MenuBottomSheet menuBottomSheet = new MenuBottomSheet();
        menuBtn.setOnClickListener(v -> menuBottomSheet.show(getSupportFragmentManager(), MenuBottomSheet.TAG));
    }

    public void updateRecycler(String tab) {
        if (tab.equals("Lists")) {
            //update recycler
            db.collection("lists").whereEqualTo("createdBy", member.getReference()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    ArrayList<DBTask> lists = new ArrayList<>();
                    listsAdapter = new ListsAdapter(lists);
                    lacRecycler.setAdapter(listsAdapter);
                    lacRecycler.setLayoutManager(new LinearLayoutManager(ListAndActivityMainScreen.this));
                    Objects.requireNonNull(lacRecycler.getLayoutManager()).onRestoreInstanceState(lacRecycler.getLayoutManager().onSaveInstanceState());
                }
            });
        } else if (tab.equals("Activities")) {
            //update recycler
            db.collection("activities").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    ArrayList<Activity> activities = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                        Activity activity = Activity.getActivity(documentSnapshot);
                        if (activity.getInvites().contains(member.getReference()) || activity.getCreatedBy().equals(member.getReference())) {
                            activities.add(activity);
                        }
                    }
                    activitiesAdapter = new ActivitiesAdapter(activities);
                    lacRecycler.setAdapter(activitiesAdapter);
                    lacRecycler.setLayoutManager(new LinearLayoutManager(ListAndActivityMainScreen.this));
                    Objects.requireNonNull(lacRecycler.getLayoutManager()).onRestoreInstanceState(lacRecycler.getLayoutManager().onSaveInstanceState());
                }
            });
        }
    }
}
