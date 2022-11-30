package com.example.familyschedulingapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Calendar;

public class AddEvent extends AppCompatActivity {
    private ArrayList<Event> eventList;
    EditText nameInput;
    EditText descriptionInput;
    EditText membersInput;
    EditText notesInput;
    EditText dateInput;
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
        Intent intent= getIntent();
        eventList= (ArrayList<Event>) intent.getSerializableExtra("eventList");
        Event event=new Event();
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
}