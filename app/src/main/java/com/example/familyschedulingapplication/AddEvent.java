package com.example.familyschedulingapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddEvent extends AppCompatActivity {
    private ArrayList<Event> eventList;
    EditText nameInput;
    EditText descriptionInput;
    EditText membersInput;
    EditText notesInput;
    EditText dateInput;
    Button cancelBtn;
    Button saveBtn;
    Event event;
    DatePickerDialog.OnDateSetListener setListener;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        nameInput=findViewById(R.id.nameInputText);
        descriptionInput=findViewById(R.id.descriptionInputText);
        membersInput=findViewById(R.id.membersInputText);
        dateInput=findViewById(R.id.dateInputText);
        notesInput=findViewById(R.id.notesMultiText);
        cancelBtn=findViewById(R.id.cancelButton);
        saveBtn=findViewById(R.id.saveButton);
        event=new Event(); // This is the new event made which will be added to the eventList when saved.
        Intent intent= getIntent();
        eventList= (ArrayList<Event>) intent.getSerializableExtra("eventList");
        // The code below is used to implement the date picker for the date field.
        Calendar calendar = Calendar. getInstance ();
        final int year = calendar.get (Calendar. YEAR);
        final int month = calendar.get (Calendar. MONTH);
        final int day = calendar.get (Calendar. DAY_OF_MONTH);

        dateInput.setOnClickListener (new View.OnClickListener (){
        @Override
        public void onClick (View v) {
            DatePickerDialog datePickerDialog = new DatePickerDialog (
                    AddEvent.this, new DatePickerDialog.OnDateSetListener () {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    month = month + 1;
                    String date = day + "/" + month + "/" + year;
                    dateInput.setText(date);
                }
            },year, month, day);
            datePickerDialog.show ();
        }
        });
    }
    public void addMember(View view){
        String newMember=membersInput.getText().toString(); // This is the new member that will be added to the list of members.
        ArrayList<String> existingMembers=event.getMembers();
        if(!existingMembers.contains(newMember)){
            existingMembers.add(newMember); // This will update the list.
            event.addMember(existingMembers);
            Toast.makeText(this, newMember+" added.", Toast.LENGTH_SHORT).show();
            membersInput.setText("");
        }
        else{
            Toast.makeText(this, newMember+" already exists", Toast.LENGTH_SHORT).show();
        }

    }
    public void onCancel(View view){
        finish(); // goes back to mainEventScreen.
    }
    public void onBack(View view){
        finish(); // goes back to the mainEventScreen.
    }
    public void onSave(View view){
        event.setName(nameInput.getText().toString());
        event.setDescription(descriptionInput.getText().toString());
        event.setNotes(notesInput.getText().toString());
        event.setDate(new Date(dateInput.getText().toString()));
        eventList.add(event);
        finish();
    }
}