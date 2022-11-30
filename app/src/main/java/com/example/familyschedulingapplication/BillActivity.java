package com.example.familyschedulingapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

public class BillActivity extends AppCompatActivity {
    private static final String TAG = BillActivity.class.getName();
    private FirebaseFirestore db;
    RecyclerView rv;
    CustomAdapter rva;
    String[]data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        data=new String[]{"TSN BILL","PRIME BILL","NETFLIX BILL"};
        db=FirebaseFirestore.getInstance();
        ModalBottomSheet modalBottomSheet = new ModalBottomSheet();
        ImageButton menuBtn = findViewById(R.id.imageButton);
        menuBtn.setOnClickListener(v -> modalBottomSheet.show(getSupportFragmentManager(), ModalBottomSheet.TAG));
        rv=(RecyclerView) findViewById(R.id.recycleView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rva=new CustomAdapter(this,data);
        rv.setAdapter(rva);

    }

    public void next(View view){
        Intent intent=new Intent(this, NewBillActivity.class);
        startActivity(intent);
    }
  /*  public String[] getData(){
        db.collection("/bills")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                list.add(document.getData());
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Toast.makeText(BillActivity.this,"Failure reading documents",Toast.LENGTH_LONG).show();
                        }
                    }
                });
        int i=0;
        data=new String[list.size()];
        for(Object o:list)
        {
            data[i]=o.toString();
            i++;
        }
        if(data.equals(""))
            Toast.makeText(BillActivity.this,"Failure reading documents",Toast.LENGTH_LONG).show();
        return data;
    }*/
}