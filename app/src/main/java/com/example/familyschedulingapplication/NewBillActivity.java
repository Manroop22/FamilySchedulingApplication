package com.example.familyschedulingapplication;

import static android.widget.Toast.LENGTH_LONG;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class NewBillActivity extends AppCompatActivity {
    EditText dateText;
    EditText dateText2;
    ImageView calendar;
    ImageView calendar2;
    private int date,month,year;
    private int date2,month2,year2;
    private static final String TAG = "MyActivity";
    private FirebaseFirestore db;
    SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
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
                    datePickerDialog.getDatePicker(); //setMaxDate(System.currentTimeMillis()-1000);
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
                    datePickerDialog.getDatePicker();//setMinDate(System.currentTimeMillis()-1000);
                    datePickerDialog.show();
                }

            }
        });
    }
    public void save(View view) throws ParseException {

        EditText name = findViewById(R.id.editTextTextPassword);
        EditText occurrence = findViewById(R.id.editTextTextPersonName3);
        EditText note=findViewById(R.id.editTextTextPersonName4);
        EditText link=findViewById(R.id.editTextTextPersonName5);
        CheckBox email = findViewById(R.id.checkBox);
        CheckBox sms = findViewById(R.id.checkBox2);
        CheckBox push = findViewById(R.id.checkBox3);

        if(name.getText().equals(""))
            Toast.makeText(NewBillActivity.this,"Please enter name of Bill", LENGTH_LONG).show();
        else if(dateText.getText().equals(""))
            Toast.makeText(NewBillActivity.this,"Please enter date", LENGTH_LONG).show();
        else if(dateText2.getText().equals(""))
            Toast.makeText(NewBillActivity.this,"Please enter due date", LENGTH_LONG).show();
        else if(occurrence.getText().equals(""))
            Toast.makeText(NewBillActivity.this,"Please number of occurrence", LENGTH_LONG).show();
        else if(note.getText().equals(""))
            Toast.makeText(NewBillActivity.this,"Please enter note", LENGTH_LONG).show();
        else if(link.getText().equals("")&&!URLUtil.isValidUrl(link.getText().toString()))
            Toast.makeText(NewBillActivity.this,"Please enter a valid link to payment website", LENGTH_LONG).show();
        else if(email.isChecked()==false&sms.isChecked()==false&push.isChecked()==false)
            Toast.makeText(NewBillActivity.this,"Please check atleast one notification option", LENGTH_LONG).show();
        else {
            //Create a new bill
            Map<String, Object> bill = new HashMap<>();
            bill.put("name", name.getText().toString());
            bill.put("date",sd.parse(dateText.getText().toString()));
            bill.put("due", sd.parse(dateText2.getText().toString()));
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
                            Intent newIntent = new Intent(NewBillActivity.this,BillActivity.class);
                            newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(newIntent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NewBillActivity.this,e+"", LENGTH_LONG).show();
                        }
                    });
        }



    }
    public void cancel(){
        finish();
    }
}