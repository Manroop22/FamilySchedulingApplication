package com.example.familyschedulingapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.familyschedulingapplication.ModalBottomSheets.MenuBottomSheet;
import com.example.familyschedulingapplication.Models.Home;
import com.example.familyschedulingapplication.Models.Member;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public Member member;
    public Home home;
    TextView welcomeTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        welcomeTitle = findViewById(R.id.welcomeTitle);
        MenuBottomSheet menuBottomSheet = new MenuBottomSheet();
        ImageButton menuBtn = findViewById(R.id.menuBtn);
        menuBtn.setOnClickListener(v -> menuBottomSheet.show(getSupportFragmentManager(), MenuBottomSheet.TAG));
        checkCurrentUser();
    }

    public void homeInit() {
        // toggle Home Views
        // if member has homeId, show home views
        if (member.getHomeId() != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("homes").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null) {
                        // for items in querySnapshot, check if homeId matches member's homeId
                        for (DocumentSnapshot dSnap : querySnapshot.getDocuments()) {
                            if (member.getHomeId().equals(dSnap.getId())) {
                                // if homeId matches, set home to that home
                                home = Home.getHomeById(dSnap);
                                buildHome(home);
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            });
        } else {
            welcomeTitle.setText("Welcome");
        }
    }

    public void buildHome(Home home) {
        if (home != null) {
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
            db.collection("members").document(user.getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        Log.d(TAG, "checkCurrentUser: member exists");
                        member = task.getResult().toObject(Member.class);
                    } else {
                        Log.d(TAG, "checkCurrentUser: member does not exist");
                        member = new Member(user.getUid());
                        member.Save();
                    }
                } else {
                    Log.d(TAG, "checkCurrentUser: member does not exist");
                    member = new Member(user.getUid());
                    member.Save();
                    homeInit();
                }
                if (member.getHomeId() == null) {
                    Log.d(TAG, "checkCurrentUser: member has no home");
                    Intent intent = new Intent(this, NoHomeActivity.class);
                    startActivity(intent);
                } else {
                    Log.d(TAG, "checkCurrentUser: member has home");
                    home = Home.getHomeById(member.getHomeId());
                }
                homeInit();
                getUserProfile();
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