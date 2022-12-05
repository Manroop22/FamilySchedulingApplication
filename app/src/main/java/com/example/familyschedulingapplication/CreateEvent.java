package com.example.familyschedulingapplication;

import static java.util.UUID.randomUUID;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.familyschedulingapplication.Adapters.MemberAdapter;
import com.example.familyschedulingapplication.Models.Event;
import com.example.familyschedulingapplication.Models.Member;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;

public class CreateEvent extends AppCompatActivity {
    EditText nameInput;
    EditText descriptionInput;
    EditText notesInput;
    TextView dateInput;
    Button cancelBtn, saveBtn, conflictBtn;
    ImageButton backBtn;
    Event event = new Event();
    Member member;
    Spinner membersSpinner;
    Date dateRes;
    ArrayList<Member> members;
    MemberAdapter adapter;
    ArrayList<DocumentReference> memberList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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
        conflictBtn=findViewById(R.id.checkConflictBtnNE);
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
            builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());
            MaterialDatePicker<Long> materialDatePicker = builder.build();
            materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
            materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                Date date = new Date(selection);
                // add 1 day to date, because it is 1 day behind and set time to currentMillis()
                dateRes = new Date(date.getTime() + 86400000);
                // set just the current time to the selected date
                dateRes.setHours(new Date(System.currentTimeMillis()).getHours());
                dateRes.setMinutes(new Date(System.currentTimeMillis()).getMinutes());
                dateRes.setSeconds(new Date(System.currentTimeMillis()).getSeconds());
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
        conflictBtn.setOnClickListener(v -> {
            if (dateInput.getText().toString().isEmpty()) {
                Toast.makeText(CreateEvent.this, "Please select a date", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(CreateEvent.this, ConflictsActivity.class);
                Bundle eventBundle = new Bundle();
                eventBundle.putLong("eventDate", dateRes.getTime());
                eventBundle.putString("homeId", member.getReference().toString());
                eventBundle.putString("userId", member.getUserId());
                intent.putExtras(eventBundle);
                startActivity(intent);
            }
        });
    }

    public void spinnerAdapter() {
        Member.getMembersByHome(member.getHomeId(), task -> {
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
        goBack(); // goes back to mainEventScreen.
    }
    public void onBack(View view){
        goBack(); // goes back to the mainEventScreen.
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
            event.setHomeId(member.getHomeId());
            Event.addEvent(event, task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(CreateEvent.this, "Event created successfully", Toast.LENGTH_SHORT).show();
                    goBack();
                } else {
                    Toast.makeText(CreateEvent.this, "Event creation failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    void goBack() {
        Intent intent = new Intent(CreateEvent.this, EventMainScreen.class);
        startActivity(intent);
        finish();
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