package com.example.familyschedulingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.familyschedulingapplication.Adapters.HomeMemberAdapter;
import com.example.familyschedulingapplication.Adapters.InviteAdapter;
import com.example.familyschedulingapplication.ModalBottomSheets.MenuBottomSheet;
import com.example.familyschedulingapplication.Models.Home;
import com.example.familyschedulingapplication.Models.Member;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public Member member;
    public Home home;
    TextView welcomeTitle;
    HomeMemberAdapter adapter;
    RecyclerView HomeMemberRV;
    ArrayList<Member> homeMemberList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HomeMemberRV=findViewById(R.id.HomeMemberRecyclerView);
        welcomeTitle = findViewById(R.id.welcomeTitle);
        MenuBottomSheet menuBottomSheet = new MenuBottomSheet();
        ImageButton menuBtn = findViewById(R.id.menuBtn);
        menuBtn.setOnClickListener(v -> menuBottomSheet.show(getSupportFragmentManager(), MenuBottomSheet.TAG));
        checkCurrentUser();
    }

    public void homeInit(Home hom) {
        // toggle Home Views
        // if member has homeId, show home views
        if (member.getHomeId() != null) {
            if (hom != null) {
                home = hom;
                // homeName to first upper
                String homeName = home.getName();
                if (homeName == null) {
                    homeName = "Your Home";
                } else {
                    String firstLetter = homeName.substring(0, 1).toUpperCase();
                    String restOfName = homeName.substring(1);
                    homeName = firstLetter + restOfName;
                }
                welcomeTitle.setText(homeName);
            } else {
                Toast.makeText(this, "Error: Home not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            welcomeTitle.setText("Welcome");
        }
    }

    public void buildHome(Home home) {
        if (home != null) {
            // adapter set.
            adapter = new HomeMemberAdapter(homeMemberList);
            HomeMemberRV.setAdapter(adapter);
            HomeMemberRV.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            if (home.getName() != null) {
                welcomeTitle.setText(String.format("%s", home.getName()));
            } else {
                welcomeTitle.setText("Welcome Home");
            }
        }
    }

    public void checkCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            Log.d(TAG, "checkCurrentUser: user is signed in");
            // user.getUid() == member.getUid()
            // if member with uid does not exist, create member
            // if member with uid exists, check if member has homeId
            // if member has no homeId, go to NoHomeActivity
            // if member has homeId, stay here
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Member.getMember(user.getUid(), task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        member = null;
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            member = document.toObject(Member.class);
                        } else {
                            Log.d(TAG, "No such document");
                        }
                        if (member == null) {
                            Log.d(TAG, "checkCurrentUser: member is null");
                            member = new Member();
                            member.setUserId(user.getUid());
                            member.setProfileUrl(user.getPhotoUrl().toString());
                            member.setName(user.getDisplayName());
                            member.setEmail(user.getEmail());
                            member.setHomeId(null);
                        }
                        if (member.getHomeId() == null) {
                            Log.d(TAG, "checkCurrentUser: member has no homeId");
                            Intent intent = new Intent(MainActivity.this, NoHomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.d(TAG, "checkCurrentUser: member has homeId");
                            Home.getHomeById(member.getHomeId(), task1 -> {
                                if (task1.isSuccessful()) {
                                    DocumentSnapshot document1 = task1.getResult();
                                    if (document1 != null) {
                                        home = Home.getHome(document1);
                                        homeInit(home);
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task1.getException());
                                }
                            });
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            });
        } else {
            // No user is signed in
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public static void signOut() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
        }
    }

    public void getUserProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();

        }
    }

    public void getProviderData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                // Id of the provider (ex: google.com)
                String providerId = profile.getProviderId();
                // UID specific to the provider
                String uid = profile.getUid();
                // Name, email address, and profile photo Url
                String name = profile.getDisplayName();
                String email = profile.getEmail();
                String photoUrl = Objects.requireNonNull(profile.getPhotoUrl()).toString();
            }
        }
    }

    public void sendEmailVerification() {
        // [START send_email_verification]
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        user.sendEmailVerification()
                .addOnCompleteListener(this, task -> {
                    // [START_EXCLUDE]
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this,
                                "Verification email sent to " + user.getEmail(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "sendEmailVerification", task.getException());
                        Toast.makeText(MainActivity.this,
                                "Failed to send verification email.",
                                Toast.LENGTH_SHORT).show();
                    }
                    // [END_EXCLUDE]
                });
    }
}