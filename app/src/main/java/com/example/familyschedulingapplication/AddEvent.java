package com.example.familyschedulingapplication;

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

import com.example.familyschedulingapplication.Model.Event;
import com.example.familyschedulingapplication.Model.Member;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddEvent extends AppCompatActivity {
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
    ArrayAdapter<Member> adapter;
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
        DocumentReference memberRef = db.collection("members").document(user.getUid());
        member = Member.getMemberByMemberId(memberRef);
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
        spinnerAdapter();
        // The code below is used to implement the date picker for the date field.
        Calendar calendar = Calendar.getInstance ();
        int year = calendar.get (Calendar.YEAR);
        int month = calendar.get (Calendar.MONTH);
        int day = calendar.get (Calendar.DAY_OF_MONTH);
        dateInput.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().build();
            datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
            datePicker.addOnPositiveButtonClickListener(selection -> {
                dateInput.setText(datePicker.getHeaderText());
                dateRes = new Date(selection);
            });
        });
        membersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                memberList.add(Member.getMembersByHomeId(member.getHomeId().getId()).get(position).getReference());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    public void spinnerAdapter() {
        members = Member.getMembersByHome(member.getHomeId());
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, members);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        membersSpinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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
            Event.addEvent(event, task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Event added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Event failed to add", Toast.LENGTH_SHORT).show();
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