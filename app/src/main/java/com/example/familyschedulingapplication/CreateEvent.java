package com.example.familyschedulingapplication;

import static java.util.UUID.randomUUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.familyschedulingapplication.Adapters.MemberAdapter;
import com.example.familyschedulingapplication.Models.Event;
import com.example.familyschedulingapplication.Models.Member;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CreateEvent extends AppCompatActivity {
    EditText nameInput;
    EditText descriptionInput;
    EditText notesInput;
    TextView dateInput;
    Button cancelBtn;
    Button saveBtn;
    ImageButton backBtn;
    Event event = new Event();
    Member member;
    Spinner membersSpinner;
    Date dateRes;
    ArrayList<Member> members;
    MemberAdapter adapter;
    ArrayList<DocumentReference> memberList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // if member is in members collection get the reference
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        assert user != null;
        nameInput=findViewById(R.id.nameInputText);
        descriptionInput=findViewById(R.id.descriptionInputText);
        dateInput=findViewById(R.id.dateInputText);
        notesInput=findViewById(R.id.notesMultiText);
        cancelBtn=findViewById(R.id.cancelButton);
        saveBtn=findViewById(R.id.saveButton);
        saveBtn.setOnClickListener(this::onSave);
        cancelBtn.setOnClickListener(this::onCancel);
        backBtn=findViewById(R.id.backBtnDetails);
        backBtn.setOnClickListener(this::onBack);
        membersSpinner = findViewById(R.id.membersSpinner);
        Member.getMember(user.getUid(), task -> {
            member = Member.getMemberByMemberId(task.getResult());
            initEvent();
        });

    }

    public void initEvent() {
        spinnerAdapter();
        // The code below is used to implement the date picker for the date field.
        dateInput.setOnClickListener(v -> {
            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
            builder.setTitleText("Select a date");
            MaterialDatePicker<Long> materialDatePicker = builder.build();
            materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
            materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                dateRes = new Date((Long) selection);
                dateInput.setText(dateRes.toString());
            });
        });
        membersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Member.getMember(members.get(position).getUserId(), task -> {
                    if (task.isSuccessful()) {
                        memberList.add(task.getResult().getReference());
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void spinnerAdapter() {
        Member.getMembersByHome(member.getHomeId(), (OnCompleteListener<QuerySnapshot>) task -> {
            if (task.isSuccessful()) {
                members = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    members.add(document.toObject(Member.class));
                }
                adapter = new MemberAdapter(CreateEvent.this, R.layout.member_item, members);
                adapter.selectedMembers = new ArrayList<>();
                adapter.notifyDataSetChanged();
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                membersSpinner.setAdapter(adapter);
            } else {
                Log.d("CreateEvent", "Error getting documents: ", task.getException());
            }
        });
    }

    public void onCancel(View view){
        finish(); // goes back to mainEventScreen.
    }
    public void onBack(View view){
        finish(); // goes back to the mainEventScreen.
    }
    public void onSave(View view){
        if (validateFields()) {
            event.setName(nameInput.getText().toString());
            event.setDescription(descriptionInput.getText().toString());
            event.setNotes(notesInput.getText().toString());
            event.setEventDate(dateRes);
            event.setCreatedAt(new Date());
            Log.d("Member", member.toString());
            event.setCreatedBy(member.getReference());
            event.setParticipants(memberList);
            event.setCreatedBy(member.getReference());
            event.setEventId(randomUUID().toString());
            Event.addEvent(event, task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(CreateEvent.this, "Event created successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CreateEvent.this, "Event creation failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public boolean validateFields() {
        if(nameInput.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter a name for the event", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(descriptionInput.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter a description for the event", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(notesInput.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter notes for the event", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(dateRes == null || dateRes.toString().isEmpty()) {
            Toast.makeText(this, "Please enter a date for the event", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}