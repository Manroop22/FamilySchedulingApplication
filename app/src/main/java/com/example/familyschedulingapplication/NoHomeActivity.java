package com.example.familyschedulingapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.familyschedulingapplication.Models.Home;
import com.example.familyschedulingapplication.Models.Member;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Date;
import java.util.ArrayList;

public class NoHomeActivity extends AppCompatActivity {
    private String mode = ""; // create or join
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_home);
        ImageButton backBtn = findViewById(R.id.backBtn);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user == null) {
//            Intent intent = new Intent(this, LoginActivity.class);
//            startActivity(intent);
//        }
        assert user != null;
        Member member = new Member(user.getUid());
        Button createHomeBtn = findViewById(R.id.createHomeBtn);
        Button joinHomebtn = findViewById(R.id.joinHomeBtn);
        EditText accessCode = findViewById(R.id.editTextAccessCode);
        EditText createHomeName = findViewById(R.id.editTextCreateHome);
        ImageButton newHomeBtn = findViewById(R.id.newHomeBtn);
        ImageButton accessCodeBtn = findViewById(R.id.accessCodeBtn);
        LinearLayout joinHomeLayout = findViewById(R.id.joinHomeLayout);
        LinearLayout createHomeLayout = findViewById(R.id.createHomeLayout);
        backBtn.setOnClickListener(v -> {
            finish();
        });
        joinHomeLayout.setVisibility(LinearLayout.GONE);
        createHomeLayout.setVisibility(LinearLayout.GONE);
        joinHomebtn.setOnClickListener(v -> {
            mode = "join";
            joinHomeLayout.setVisibility(LinearLayout.VISIBLE);
            createHomeLayout.setVisibility(LinearLayout.GONE);
            if (mode.equals("join")) {
                accessCodeBtn.setOnClickListener(v1 -> {
                    String code = accessCode.getText().toString();
                    // if home exists, find home that has accessCode = code, then set homeInvites's accepted to true where invitedMember = member and homeId = homeId
                    // then set member's homeId to home document reference
                    // if home doesn't exist, show error message
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("homes").whereEqualTo("accessCode", code).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Home home = document.toObject(Home.class);
                                assert home != null;
                                member.setHomeId(home.getReference());
                                member.Save();
                                finish();
                            }
                        } else {
                            Toast.makeText(this, "Home not found", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }
        });
        createHomeBtn.setOnClickListener(v -> {
            mode = "create";
            joinHomeLayout.setVisibility(LinearLayout.GONE);
            createHomeLayout.setVisibility(LinearLayout.VISIBLE);
            if (mode.equals("create")) {
                newHomeBtn.setOnClickListener(v1 -> {
                    String homeName = createHomeName.getText().toString();
                    // create home with name = homeName, accessCode = random 8 digit alphanumeric string
                    // then set member's homeId to home document reference
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    // random 8 digit alphanumeric string, search through all homes, if access code exists then regenerate
                    StringBuilder accCode = new StringBuilder();
                    ArrayList<Home> homes = Home.getHomes();
                    boolean exists = true;
                    while (exists) {
                        for (int i = 0; i < 8; i++) {
                            int rand = (int) (Math.random() * 36);
                            if (rand < 10) {
                                accCode.append(rand);
                            } else {
                                accCode.append((char) (rand + 55));
                            }
                        }
                        exists = false;
                        for (Home home : homes) {
                            if (home.getAccessCode().equals(accCode.toString())) {
                                exists = true;
                                break;
                            }
                        }
                    }
                    Log.d("accCode", accCode.toString());
                    Home home = new Home(homeName, accCode.toString());
                    home.setActive(true);
                    home.setCreatedAt(new Date(System.currentTimeMillis()));
                    home.setCreatedBy(member.getReference());
                    home.Save();
                    member.setHomeId(home.getReference());
                    member.Save();
                    Toast.makeText(this, "Home created successfully", Toast.LENGTH_SHORT).show();
                    // TODO: go to main activity
                    finish();
                });
            }
        });
//        String uid = user.getUid();
        // find if a user is in the member collection, if not, create a new member document
        // if they are, check if they are in a home, if not, display the no home activity
        // if they are, navigate to the main activity
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("members").document(uid).get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                DocumentSnapshot document = task.getResult();
//                if (document.exists()) {
//                    member.setHomeId(document.getString("homeId"));
//                    if (member.getHomeId() != null || !member.getHomeId().equals("")) {
//                        finish();
//                    }
//                } else {
//                    member.Save();
//                }
//            } else {
//                member.Save();
//            }
//        });
    }
}
