package com.example.familyschedulingapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import java.util.Map;
import java.util.Scanner;

public class BillActivity  extends AppCompatActivity implements ItemClickListener {
    private static final String TAG = BillActivity.class.getName();
    private FirebaseFirestore db;
    RecyclerView rv;
    CustomAdapter rva;
    String[]data;
    ArrayList<String> list=new ArrayList<String>();

    BillActivity billingContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        billingContext = this;
        setContentView(R.layout.activity_bill);
        db = FirebaseFirestore.getInstance();
        ModalBottomSheet modalBottomSheet = new ModalBottomSheet();
        ImageButton menuBtn = findViewById(R.id.imageButton);
        menuBtn.setOnClickListener(v -> modalBottomSheet.show(getSupportFragmentManager(), ModalBottomSheet.TAG));



        db.collection("/bills")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Map<String, Object> data = document.getData();
                                list.add(data.get("name").toString()+":" + " " + data.get("due").toString());
//                                Log.d("test",list.toString());
                            }

                            Log.d("testList",list.toString());
                            data = new String[list.size()];
                            for(int i=0;i<data.length;i++)
                            {
                                data[i]=list.get(i);
                            }

                            rv = (RecyclerView) findViewById(R.id.recycleView);
                            rv.setLayoutManager(new LinearLayoutManager(billingContext));
                            rva = new CustomAdapter(billingContext,data);
                            rv.setAdapter(rva);
                            rva.setClickListener(billingContext);
                        } else {
                            Toast.makeText(BillActivity.this,"Failure reading documents",Toast.LENGTH_LONG).show();
                        }


                    }
                });


    }


    public void next(View view){
        Intent intent=new Intent(this, NewBillActivity.class);
        startActivity(intent);
    }
    public void individualBill(){

    }

    @Override
    public void onClick(View view, TextView views) {
        TextView rst= (TextView) views;
        Scanner read = new Scanner(rst.getText().toString());
        read.useDelimiter(":");
        String name = read.next().trim();
        Intent intent=new Intent(this, IndividualBillActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("name",name);
        intent.putExtras(bundle);
        startActivity(intent);


    }
   /* public String[] getData(){
        db.collection("/bills")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
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