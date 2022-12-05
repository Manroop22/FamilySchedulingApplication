package com.example.familyschedulingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.familyschedulingapplication.Models.Member;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "LoginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        Button login = findViewById(R.id.login);
        Button register = findViewById(R.id.register);
        login.setOnClickListener(v -> {
            String emailStr = email.getText().toString();
            String passwordStr = password.getText().toString();
            if (emailStr.isEmpty() || passwordStr.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter your email and password", Toast.LENGTH_SHORT).show();
            } else {
                signIn(emailStr, passwordStr);
            }
        });
        register.setOnClickListener(v -> {
            String emailStr = email.getText().toString();
            String passwordStr = password.getText().toString();
            if (emailStr.isEmpty() || passwordStr.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter your email and password", Toast.LENGTH_SHORT).show();
            } else {
                createAccount(emailStr, passwordStr);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.reload();
        }
    }

    public void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        signIn(email, password);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    public void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            findViewById(R.id.login).setVisibility(android.view.View.GONE);
            // check if member.findMember doesn't exist, if it doesn't, create it and go to main activity
            // if it does, update the member and go to main activity
            Member.getMember(user.getUid(), task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Member member = Member.getMemberByMemberId(document);
                    if (member.getName() == null) {
//                    member.setProfileUrl(getString(R.string.default_pic));
                        member.setProfileUrl("https://google.com");
                        member.setEmail(user.getEmail());
//                        member.setPhone(user.getPhoneNumber());
                    }
                    member.setActive(true);
                    if (member.getUserId() == null) {
                        member.setUserId(user.getUid());
                        member.setActive(true);
                        member.setJoinedAt(new Date(System.currentTimeMillis()));
                        Member.addMember(member, task1 -> {
                            if (task1.isSuccessful()) {
                                Log.d(TAG, "New Member created: " + task1.getResult());
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                              Log.w(TAG, "Error adding document", task1.getException());
                            }
                        });
                    } else {
                        Member.updateMember(member, task1 -> {
                            if (task1.isSuccessful()) {
                                Log.d(TAG, "Member updated: " + task1.getResult());
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Log.w(TAG, "Error updating document", task1.getException());
                            }
                        });
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            });
        } else {
            findViewById(R.id.login).setVisibility(android.view.View.VISIBLE);
        }
    }
}