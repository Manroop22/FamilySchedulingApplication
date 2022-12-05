package com.example.familyschedulingapplication;

import static java.util.UUID.randomUUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.familyschedulingapplication.Adapters.EventAdapter;
import com.example.familyschedulingapplication.Adapters.InviteAdapter;
import com.example.familyschedulingapplication.Models.Home;
import com.example.familyschedulingapplication.Models.HomeInvite;
import com.example.familyschedulingapplication.Models.Member;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Date;
import java.util.ArrayList;

public class NoHomeActivity extends AppCompatActivity {
    private ArrayList<HomeInvite> inviteList;
    private String mode = ""; // create or join
    private RecyclerView inviteRecyclerView;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    InviteAdapter adapter;
    ImageButton backBtn;
    Member member;
    Button createHomeBtn;
    Button joinHomebtn;
    EditText accessCode;
    EditText createHomeName;
    ImageButton newHomeBtn;
    ImageButton accessCodeBtn;
    LinearLayout joinHomeLayout;
    LinearLayout createHomeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_home);
        assert user != null;
        createHomeBtn = findViewById(R.id.createHomeBtn);
        joinHomebtn = findViewById(R.id.joinHomeBtn);
        accessCode = findViewById(R.id.editTextAccessCode);
        createHomeName = findViewById(R.id.editTextCreateHome);
        newHomeBtn = findViewById(R.id.newHomeBtn);
        accessCodeBtn = findViewById(R.id.accessCodeBtn);
        joinHomeLayout = findViewById(R.id.joinHomeLayout);
        createHomeLayout = findViewById(R.id.createHomeLayout);
        inviteRecyclerView=findViewById(R.id.rvHomeInvites);
        backBtn = findViewById(R.id.backBtn);
        Member.getMember(user.getUid(), task -> {
            if (task.isSuccessful()) {
                member = task.getResult().toObject(Member.class);
                if (member == null) {
                    member = new Member();
                    member.setUserId(user.getUid());
                    // use email before @ as name
                    member.setName(user.getEmail().split("@")[0]);
                    member.setEmail(user.getEmail());
                    member.setProfileUrl(getString(R.string.default_pic));
                    member.setJoinedAt(new Date(System.currentTimeMillis()));
                    member.setActive(true);
                    member.updateMember();
                }
                if (member.getName() == null || member.getName().equals("")) {
                    member.setName(user.getEmail().split("@")[0]);
                    member.updateMember();
                }
                init();
            }
        });
    }

    public void init() {
        inviteRecycler();
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
                    Home.getHomeByAccessCode(code, task -> {
                        QuerySnapshot querySnapshot = task.getResult();
                        Home home = null;
                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                            home = documentSnapshot.toObject(Home.class);
                            break;
                        }
                        if (home != null) {
                            if (member.getUserId() == null) {
                                Intent intent = new Intent(NoHomeActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Log.d("NoHomeActivity", "homeId: " + home);
                                Member.joinHome(home.getReference(), member, code, task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(NoHomeActivity.this, "Successfully joined home", Toast.LENGTH_SHORT).show();
                                        goHome();
                                    } else {
                                        Toast.makeText(NoHomeActivity.this, "Error joining home", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
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
                    // random 8 digit alphanumeric string, search through all homes, if access code exists then regenerate
                    Home.getHomes(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot homeSnap = task.getResult();
                            ArrayList<Home> homes = new ArrayList<>();
                            for (DocumentSnapshot document : homeSnap) {
                                Home home = document.toObject(Home.class);
                                assert home != null;
                                homes.add(home);
                            }
                            boolean notExists = true;
                            String accCode = "";
                            while (notExists) {
                                accCode = Home.createAccessCode();
                                for (Home home : homes) {
                                    if (!home.getAccessCode().equals(accCode)) {
                                        notExists = false;
                                        break;
                                    }
                                }
                            }
                            Log.d("accCode", accCode);
                            Home home = new Home(homeName, accCode);
                            home.setHomeId(randomUUID().toString());
                            home.setActive(true);
                            home.setCreatedAt(new Date(System.currentTimeMillis()));
                            home.setCreatedBy(member.getReference());
                            Home.addHome(home, task1 -> {
                                if (task1.isSuccessful()) {
                                    member.setHomeId(home.getReference());
                                    Member.updateMember(member, task2 -> {
                                        if (task2.isSuccessful()) {
                                            Toast.makeText(NoHomeActivity.this, "Successfully created home", Toast.LENGTH_SHORT).show();
                                            goHome();
                                        } else {
                                            Toast.makeText(NoHomeActivity.this, "Error creating home", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    Toast.makeText(NoHomeActivity.this, "Error creating home", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(NoHomeActivity.this, "Error creating home", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }
        });
    }

    public void goHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void inviteRecycler() {
        inviteRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new InviteAdapter(inviteList);
        inviteRecyclerView.setAdapter(adapter);
    }
}
