package com.example.familyschedulingapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.familyschedulingapplication.Adapters.MemberAdapter;
import com.example.familyschedulingapplication.Models.Event;
import com.example.familyschedulingapplication.Models.Member;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class EventDetails extends AppCompatActivity {
    ImageButton backBtn, editBtn, deleteBtn;
    Button cancelBtn, saveBtn;
    EditText nameInput, descriptionInput, notesInput, dateInput;
    Spinner membersSpinner;
    Event event;
    Member member;
    DocumentReference eventRef;
    String dateString, eventId;
    ArrayList<Member> members;
    MemberAdapter adapter;
    Date dateRes;
    FirebaseFirestore db;
    ArrayList<DocumentReference> participants = new ArrayList<>();
    public String mode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        eventId = getIntent().getExtras().getString("eventId");
        Log.d("EventDetailsActivity", "eventId: " + eventId);
        mode = getIntent().getExtras().getString("mode");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        assert user != null;
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
        Member.getMember(user.getUid(), task -> {
            if (task.isSuccessful()) {
                member = Member.getMemberByMemberId(task.getResult());
                Event.getEventByEventId(eventId, task1 -> {
                    event = task1.getResult().toObject(Event.class);
                    assert event != null;
                    if (!event.getCreatedBy().equals(member.getReference())) {
                        mode = "view";
                        editBtn.setVisibility(View.GONE);
                        deleteBtn.setVisibility(View.GONE);
                    }
                    Log.d("EventDetailsActivity", "onCreate: " + eventId);
                    init();
                });
            } else {
                Log.d("EventDetailsActivity", "onCreate: " + task.getException());
                goBack();
            }
        });
    }

    public void init() {
        switchMode(mode);
        dateInput.setOnClickListener(v -> {
            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
            builder.setTitleText("Select a date");
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
        backBtn.setOnClickListener(v -> finish());
        editBtn.setOnClickListener(v -> switchMode("edit"));
        deleteBtn.setOnClickListener(v -> db.collection("events").document(eventId).delete().addOnSuccessListener(aVoid -> {
            Toast.makeText(EventDetails.this, "Event deleted", Toast.LENGTH_SHORT).show();
            goBack();
        }));
        cancelBtn.setOnClickListener(v -> switchMode("view"));
        saveBtn.setOnClickListener(v -> saveDetails());
        if (event.getParticipants().size() > 0) {
            spinnerAdapter(event.getParticipants());
        } else {
            spinnerAdapter(new ArrayList<>());
        }
        membersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Member memb = adapter.getItem(position);
                if (memb != null && memb.getReference() != null && adapter.selectedMembers.size() > 0) {
                    if (adapter.inSelect(memb.getReference())) {
                        adapter.selectedMembers.remove(memb.getReference());
                    } else {
                        adapter.selectedMembers.add(memb.getReference());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void goBack() {
        Intent intent = new Intent(EventDetails.this, EventMainScreen.class);
        startActivity(intent);
        finish();
    }

    public void setValues(){
        db.collection(Event.collection).document(eventId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    event = (task.getResult().toObject(Event.class));
                    assert event != null;
                    nameInput.setText(event.getName());
                    descriptionInput.setText(event.getDescription());
                    notesInput.setText(event.getNotes());
                    dateInput.setText(event.getEventDate().toString());
                    spinnerAdapter(event.getParticipants());
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
        Member.getMembersByHome(member.getHomeId(), task -> {
            if (task.isSuccessful()) {
                members = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    members.add(document.toObject(Member.class));
                }
                adapter = new MemberAdapter(EventDetails.this, R.layout.member_item, members);
                adapter.selectedMembers = participants;
                adapter.notifyDataSetChanged();
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                membersSpinner.setAdapter(adapter);
            } else {
                Log.d("EventDetailsActivity", "Error getting documents: ", task.getException());
            }
        });
    }

    public void saveDetails() {
        if (validateFields()) {
            Event.getEvent(eventRef, task -> {
                event.setName(nameInput.getText().toString());
                event.setDescription(descriptionInput.getText().toString());
                event.setNotes(notesInput.getText().toString());
                event.setEventDate(dateRes);
                event.setParticipants(participants);
                event.setUpdatedAt(new Date());
                if (event.getHomeId() == null) {
                    event.setHomeId(member.getHomeId());
                }
                Event.updateEvent(event, task1 -> {
                    if (task1.isSuccessful()) {
                        Toast.makeText(EventDetails.this, "Event updated", Toast.LENGTH_SHORT).show();
                        switchMode("view");
                    } else {
                        Toast.makeText(EventDetails.this, "Error updating event", Toast.LENGTH_SHORT).show();
                    }
                });
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