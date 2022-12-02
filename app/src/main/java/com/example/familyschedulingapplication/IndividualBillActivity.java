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
import java.util.Map;

public class IndividualBillActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    String url;
    String name;
    IndividualBillActivity view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_bill);
        db = FirebaseFirestore.getInstance();
        view=this;
        Intent intent = getIntent();
        Button pay = findViewById(R.id.button4);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payNow();
            }
        });
        Bundle bundle = intent.getExtras();
        name= bundle.getString("name");
        TextView billName =(TextView) findViewById(R.id.textView9);
        TextView due=(TextView) findViewById(R.id.textView15);
        TextView occurrence=(TextView) findViewById(R.id.textView16);
        TextView note=(TextView) findViewById(R.id.textView19);
        ImageView delete = findViewById(R.id.imageView4);
        DocumentReference docRef = db.collection("bills").document(name);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        billName.setText(data.get("name").toString().toUpperCase()+" BILL");
                        due.setText(data.get("due").toString());
                        note.setText(data.get("note").toString());
                        occurrence.setText(data.get("occurrence").toString());
                        String url=data.get("link").toString();
                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                db.collection("bills").document(name)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //Snackbar mySnackbar = Snackbar.make(,"Bill Successfully Created",LENGTH_LONG);
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
   public void payNow(){
        try{
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);}
        catch(Exception e){
            Toast.makeText(IndividualBillActivity.this,"uNsuccessful",Toast.LENGTH_LONG).show();
            Log.d(TAG, "payNow: "+e);

        }
    }
    public void done(){
        finish();
    }

}