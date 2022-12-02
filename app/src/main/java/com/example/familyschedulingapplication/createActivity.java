package com.example.familyschedulingapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;

public class createActivity extends AppCompatActivity {
    EditText e1,e2,e3,e4;
    Button b1,b2;
    Spinner s1;
    CheckBox c1,c2,c3;
    ImageButton i1,i2;    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        e1=(EditText)findViewById(R.id.editTextTextPersonName10);
        e2=(EditText)findViewById(R.id.editTextTextMultiLine10);
        e3=(EditText)findViewById(R.id.editTextTextPersonName40);
        e4=(EditText)findViewById(R.id.editTextTextMultiLine30);
        b1=(Button) findViewById(R.id.button10);
        b2=(Button) findViewById(R.id.button20);
        s1=(Spinner) findViewById(R.id.spinner2);
        c1=(CheckBox)findViewById(R.id.checkBox10);
        c2=(CheckBox)findViewById(R.id.checkBox20);
        c3=(CheckBox)findViewById(R.id.checkBox30);
        i1=(ImageButton)findViewById(R.id.imageButton20);
        ArrayList<String> spin=new ArrayList<String>();
        spin.add("URGENT");
        spin.add("FAMILY");
        spin.add("CASUAL");
        ArrayAdapter<String> a1=new ArrayAdapter<String>(createActivity.this,android.R.layout.simple_spinner_item,spin);
        a1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s1.setAdapter(a1);
        Calendar calendar = Calendar. getInstance ();
        final int year = calendar.get (Calendar. YEAR);
        final int month = calendar.get (Calendar. MONTH);
        final int day = calendar.get (Calendar. DAY_OF_MONTH);
        e3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpg=new DatePickerDialog(createActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month=month+1;
                        String date = day + "/" + month + "/" + year;
                        e3.setText(date);
                    }
                },year, month, day);
                dpg.show ();

            }
                });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i;
                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setTitle("Delete");
                alert.setMessage("Are you sure you do not want to create the activity?");
                alert.setPositiveButton("Yes",
                        (dialog, which) -> {
                        });
                alert.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                alert.show();
                i= new Intent(v.getContext(),activity_home.class);
                startActivity(i);
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}