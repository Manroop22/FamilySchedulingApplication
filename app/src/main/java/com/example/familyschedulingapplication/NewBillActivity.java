package com.example.familyschedulingapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NewBillActivity extends AppCompatActivity {
    EditText dateText;
    EditText dateText2;
    ImageView calendar;
    ImageView calendar2;
    private int date,month,year;
    private int date2,month2,year2;
    private static final String TAG = "MyActivity";
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bill);
        db=FirebaseFirestore.getInstance();
        dateText= findViewById(R.id.editTextTextPersonName2);
        calendar = findViewById(R.id.imageView4);
        dateText2 = findViewById(R.id.editTextTextPersonName);
        calendar2 = findViewById(R.id.imageView5);
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cal=Calendar.getInstance();
                date=cal.get(Calendar.DATE);
                month=cal.get(Calendar.MONTH);
                year=cal.get(Calendar.YEAR);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(NewBillActivity.this, android.R.style.Theme_DeviceDefault_Dialog, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                            dateText.setText(year+"-"+month+"-"+date);
                        }
                    },year,month,date);
                    datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()-1000);
                    datePickerDialog.show();
                }

            }
        });
        calendar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cal=Calendar.getInstance();
                date2=cal.get(Calendar.DATE);
                month2=cal.get(Calendar.MONTH);
                year2=cal.get(Calendar.YEAR);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(NewBillActivity.this, android.R.style.Theme_DeviceDefault_Dialog, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                            dateText2.setText(year+"-"+month+"-"+date);
                        }
                    },year2,month2,date2);
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
                    datePickerDialog.show();
                }

            }
        });
    }
    public void save(View view){

        EditText name = findViewById(R.id.editTextTextPassword);
        EditText occurrence = findViewById(R.id.editTextTextPersonName3);
        EditText note=findViewById(R.id.editTextTextPersonName4);
        EditText link=findViewById(R.id.editTextTextPersonName5);
        CheckBox email = findViewById(R.id.checkBox);
        CheckBox sms = findViewById(R.id.checkBox2);
        CheckBox push = findViewById(R.id.checkBox3);
        Boolean save = false;

        if(name.getText().equals(""))
            Toast.makeText(NewBillActivity.this,"Please enter name of Bill",Toast.LENGTH_LONG).show();
        else if(dateText.getText().equals(""))
            Toast.makeText(NewBillActivity.this,"Please enter date",Toast.LENGTH_LONG).show();
        else if(dateText2.getText().equals(""))
            Toast.makeText(NewBillActivity.this,"Please enter due date",Toast.LENGTH_LONG).show();
        else if(occurrence.getText().equals(""))
            Toast.makeText(NewBillActivity.this,"Please number of occurrence",Toast.LENGTH_LONG).show();
        else if(note.getText().equals(""))
            Toast.makeText(NewBillActivity.this,"Please enter note",Toast.LENGTH_LONG).show();
        else if(link.getText().equals(""))
            Toast.makeText(NewBillActivity.this,"Please enter link to payment website",Toast.LENGTH_LONG).show();
        else if(email.isChecked()==false&sms.isChecked()==false&push.isChecked()==false)
            Toast.makeText(NewBillActivity.this,"Please check atleast one notification option",Toast.LENGTH_LONG).show();
        else {
            //Create a new bill
            Map<String, Object> bill = new HashMap<>();
            bill.put("name", name.getText().toString());
            bill.put("date", dateText.getText().toString());
            bill.put("due", dateText2.getText().toString());
            bill.put("occurrence", occurrence.getText().toString());
            bill.put("note", note.getText().toString());
            bill.put("link", link.getText().toString());
            bill.put("email", email.isChecked());
            bill.put("sms", sms.isChecked());
            bill.put("push", push.isChecked());

            db.collection("bills").document(name.getText().toString())
                    .set(bill)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(NewBillActivity.this,"Data Successfuly added",Toast.LENGTH_LONG).show();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NewBillActivity.this,e+"",Toast.LENGTH_LONG).show();
                        }
                    });
        }



    }
    public void cancel(){
        finish();
    }
}