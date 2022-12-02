package com.example.familyschedulingapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.familyschedulingapplication.Models.Event;
import com.example.familyschedulingapplication.Models.Member;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

public class EventDetailsActivity extends AppCompatActivity {
    ImageButton backBtn;
    ImageButton editBtn;
    ImageButton deleteBtn;
    Button cancelBtn;
    Button saveBtn;
    EditText nameInput;
    EditText descriptionInput;
    EditText notesInput;
    EditText dateInput;
    Spinner membersSpinner;
    Event event;
    Member member;
    DocumentReference eventRef;
    String dateString;
    ArrayList<Member> members;
    ArrayAdapter<String> adapter;
    String eventId;
    FirebaseFirestore db;
    ArrayList<DocumentReference> participants = new ArrayList<>();
    public String mode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        eventId = getIntent().getExtras().getString("eventId");
        mode = getIntent().getExtras().getString("mode");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        assert user != null;
        DocumentReference memberRef = db.collection("members").document(user.getUid());
        member = Member.getMemberByMemberId(memberRef);
        backBtn=findViewById(R.id.backBtnDetails);
        editBtn=findViewById(R.id.editBtn);
        deleteBtn=findViewById(R.id.deleteBtn);
        cancelBtn=findViewById(R.id.cancelButtonDetails);
        saveBtn=findViewById(R.id.saveButtonDetails);
        nameInput=findViewById(R.id.nameInputText);
        descriptionInput=findViewById(R.id.descriptionInputText);
        notesInput=findViewById(R.id.notesMultiText);
        dateInput=findViewById(R.id.dateInputText);
        membersSpinner=findViewById(R.id.membersSpinner);
        switchMode(mode);
        Log.d("EventDetailsActivity", "onCreate: " + eventId);
        // if eventRef from intent is not null get the event
        String ref = getIntent().getExtras().getString("eventRef");
        if (ref != null) {
            eventRef = db.document(ref);
        } else {
            eventRef = db.collection("events").document(eventId);
        }
        dateInput.setOnClickListener(v -> {
            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
            builder.setTitleText("Select a date");
            MaterialDatePicker<Long> materialDatePicker = builder.build();
            materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
            materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                Date date = new Date((Long) selection);
                dateString = date.toString();
                dateInput.setText(dateString);
            });
        });
        backBtn.setOnClickListener(v -> finish());
        editBtn.setOnClickListener(v -> switchMode("edit"));
        deleteBtn.setOnClickListener(v -> db.collection("events").document(eventId).delete().addOnSuccessListener(aVoid -> {
            Toast.makeText(EventDetailsActivity.this, "Event deleted", Toast.LENGTH_SHORT).show();
            finish();
        }));
        cancelBtn.setOnClickListener(v -> switchMode("view"));
        saveBtn.setOnClickListener(v -> saveDetails());
        spinnerAdapter(null);
        membersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String memberName = membersSpinner.getSelectedItem().toString();
                DocumentReference memberRef1 = Member.getMemberByUserId(memberName).getReference();
                participants.add(memberRef1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setValues(){
        db.collection("events").document(eventId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
//                    event = Event.getEventByEventId(eventId);
                    Event.getEventByEventId(eventId).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            event = (task1.getResult().toObject(Event.class));
                            nameInput.setText(event.getName());
                            descriptionInput.setText(event.getDescription());
                            notesInput.setText(event.getNotes());
                            dateInput.setText(event.getEventDate().toString());
                            spinnerAdapter(event.getParticipants());
                        }
                    });
//                    nameInput.setText(event.getName());
//                    descriptionInput.setText(event.getDescription());
//                    notesInput.setText(event.getNotes());
//                    dateInput.setText(event.getEventDate().toString());
//                    spinnerAdapter(event.getParticipants());
                } else {
                    Log.d("EventDetailsActivity", "No such document");
                }
            } else {
                Log.d("EventDetailsActivity", "get failed with ", task.getException());
            }
        });
    }

    public void switchMode(String mode) {
        if (mode.equals("view")) {
            nameInput.setEnabled(false);
            descriptionInput.setEnabled(false);
            notesInput.setEnabled(false);
            dateInput.setEnabled(false);
            membersSpinner.setEnabled(false);
            cancelBtn.setVisibility(View.GONE);
            saveBtn.setVisibility(View.GONE);
        } else {
            nameInput.setEnabled(true);
            descriptionInput.setEnabled(true);
            notesInput.setEnabled(true);
            dateInput.setEnabled(true);
            membersSpinner.setEnabled(true);
            cancelBtn.setVisibility(View.VISIBLE);
            saveBtn.setVisibility(View.VISIBLE);
        }
        setValues();
    }

    public void spinnerAdapter(ArrayList<DocumentReference> participants) {
        members = null;
        if (participants == null) {
            members = Member.getMembersByHome(member.getHomeId());
        } else {
            if (participants.size() > 0) {
                for (DocumentReference participant : participants) {
                    Member member = Member.getMemberByMemberId(participant);
                    members.add(member);
                }
            } else {
                members = Member.getMembersByHome(member.getHomeId());
            }
        }
        ArrayList<String> memberNames = new ArrayList<>();
        for (Member member : members) {
            memberNames.add(member.getName());
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, memberNames);
//        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, members);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        membersSpinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void saveDetails() {
        if (validateFields()) {
            Event.getEvent(eventRef, task -> {
                event.setName(nameInput.getText().toString());
                event.setDescription(descriptionInput.getText().toString());
                event.setNotes(notesInput.getText().toString());
                event.setEventDate(new Date(dateInput.getText().toString()));
                event.setParticipants(participants);
                event.setUpdatedAt(new Date());
                Event.updateEvent(event);
                switchMode("view");
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
        if(dateInput.getText().toString().isEmpty() || dateInput.getText().toString().equals("Select a date") || dateString == null || dateString.isEmpty()) {
            Toast.makeText(this, "Please enter a date for the event", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}