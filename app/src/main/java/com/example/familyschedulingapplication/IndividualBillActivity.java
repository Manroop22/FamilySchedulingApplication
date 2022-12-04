package com.example.familyschedulingapplication;

import static android.widget.Toast.LENGTH_LONG;
import static com.example.familyschedulingapplication.ModalBottomSheet.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class IndividualBillActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    String url;
    String name;
    IndividualBillActivity view;
    EditText due;
    EditText billName;
    EditText occurrence;
    EditText note;
    ImageView delete;
    Button save;
    Button cancel;
    Button pay;
    Button done;
    String mode;
    SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
    ImageButton edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_bill);
        view=this;
        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        name= bundle.getString("name");
        setMode("view");
        pay = findViewById(R.id.button4);
        done= findViewById(R.id.button5);
        billName =(EditText) findViewById(R.id.editTextTextPersonName9);
        due=(EditText) findViewById(R.id.editTextTextPersonName6);
        occurrence=(EditText) findViewById(R.id.editTextTextPersonName7);
        note=(EditText) findViewById(R.id.editTextTextPersonName8);
        delete = findViewById(R.id.imageView4);
        save = findViewById(R.id.button);
        cancel=findViewById(R.id.button6);
        edit = findViewById(R.id.imageButton2);
        switchMode(getMode());
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMode("edit");
                switchMode(getMode());
            }
        });
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payNow();
            }
        });
    }
   public void payNow(){
        try{
        if(!url.startsWith("http"))
            url="https://"+url;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);}
        catch(Exception e){
            Toast.makeText(IndividualBillActivity.this,url+"",Toast.LENGTH_LONG).show();
            Log.d(TAG, "payNow: "+e);

        }
    }
    public void done(){
        finish();
    }
    public void switchMode(String mode){
        if(mode.equals("view")){
            done.setVisibility(View.VISIBLE);
            setView();
            due.setEnabled(false);
            billName.setEnabled(false);
            occurrence.setEnabled(false);
            note.setEnabled(false);
            save.setVisibility(View.GONE);
            cancel.setVisibility(View.GONE);
            pay.setVisibility(View.VISIBLE);
        }
        else{
            due.setEnabled(true);
            occurrence.setEnabled(true);
            note.setEnabled(true);
            billName.setEnabled(true);
            pay.setVisibility(View.GONE);
            done.setVisibility(View.GONE);
            save.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.VISIBLE);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(validateFields()){
                        try {
                            //name=billName.getText().toString();
                            update();
                            setMode("view");
                            switchMode(getMode());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setMode("view");
                    switchMode(getMode());
                }
            });
        }
    }
    public boolean validateFields(){
        if(due.getText().equals("")){
            Toast.makeText(IndividualBillActivity.this,"Please enter due date", LENGTH_LONG).show();
            return false;
        }

        else if(occurrence.getText().equals("")){
            Toast.makeText(IndividualBillActivity.this,"Please number of occurrence", LENGTH_LONG).show();
            return false;
        }
        else if(note.getText().equals("")){
            Toast.makeText(IndividualBillActivity.this,"Please enter note", LENGTH_LONG).show();
            return false;
        }
        else if(billName.getText().equals("")) {
            Toast.makeText(IndividualBillActivity.this,"Please enter Bill name", LENGTH_LONG).show();
             return false;}
        else
            return true;
    }
    void setView(){
        DocumentReference docRef = db.collection("bills").document(name);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        Date date = document.getDate("due");
                        billName.setText(data.get("name").toString().toUpperCase());
                        due.setText(new SimpleDateFormat("dd MMMM yyyy").format(date));
                        note.setText(data.get("note").toString());
                        occurrence.setText(data.get("occurrence").toString());
                        url=data.get("link").toString();
                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                db.collection("bills").document(billName.getText().toString())
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(IndividualBillActivity.this,"Bill Successfully Deleted",Toast.LENGTH_LONG).show();
                                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                Intent newIntent = new Intent(IndividualBillActivity.this,BillActivity.class);
                                                newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(newIntent);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(IndividualBillActivity.this,"Bill Deletion was unsuccessful",Toast.LENGTH_LONG).show();
                                                Log.w(TAG, "Error deleting document", e);
                                                finish();
                                            }
                                        });
                            }
                        });
                        //Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
    void update() throws ParseException {
        DocumentReference billsRef = db.collection("bills").document(name);

        billsRef
                .update("due",sd.parse(due.getText().toString()),"occurrence",occurrence.getText().toString(),"note",note.getText().toString(),"name",billName.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }
    public void setMode(String mode){
        this.mode=mode;
    }
    public String getMode(){
        return mode;
    }

}