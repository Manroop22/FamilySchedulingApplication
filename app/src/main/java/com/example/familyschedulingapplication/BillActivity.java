package com.example.familyschedulingapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class BillActivity  extends AppCompatActivity implements ItemClickListener, PopupMenu.OnMenuItemClickListener {
    private static final String TAG = BillActivity.class.getName();
    private FirebaseFirestore db;
    RecyclerView rv;
    BillAdapter rva;
    String[]data;
    ArrayList<String> list=new ArrayList<String>();
    TextView noOfBills;
    String currentTab="upcoming";

    BillActivity billingContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        billingContext = this;
        setContentView(R.layout.activity_bill);
        db = FirebaseFirestore.getInstance();
        TabLayout tabLayout = findViewById(R.id.tab);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab= Objects.requireNonNull(tab.getText()).toString().toLowerCase(Locale.ROOT);


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        ModalBottomSheet modalBottomSheet = new ModalBottomSheet();
        ImageButton menuBtn = findViewById(R.id.imageButton);
        noOfBills = findViewById(R.id.textView14);
        TextView upcoming = findViewById(R.id.textView17);
        TextView past= findViewById(R.id.textView18);
        menuBtn.setOnClickListener(v -> modalBottomSheet.show(getSupportFragmentManager(), ModalBottomSheet.TAG));
        getData();

    }


    public void next(View view){
        Intent intent=new Intent(this, NewBillActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view, TextView views) {
        TextView rst= (TextView) views;
        Scanner read = new Scanner(rst.getText().toString());
        read.next();
        read.next();
        String name = read.next().trim();
        Intent intent=new Intent(this, IndividualBillActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("name",name);
        intent.putExtras(bundle);
        startActivity(intent);


    }
    public void getData(){
        db.collection("/bills")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Map<String, Object> data = document.getData();
                                list.add("Bill Name: "+data.get("name").toString()+"  " +"Due Date: " +data.get("due").toString());
//                                Log.d("test",list.toString());
                            }

                            Log.d("testList",list.toString());
                            noOfBills.setText("You have "+list.size()+" Upcoming Bills");
                            data = new String[list.size()];
                            for(int i=0;i<data.length;i++)
                            {
                                data[i]=list.get(i);
                            }

                            rv = (RecyclerView) findViewById(R.id.recycleView);
                            rv.setLayoutManager(new LinearLayoutManager(billingContext));
                            rv.addItemDecoration(new DividerItemDecoration(billingContext,
                                    DividerItemDecoration.VERTICAL));
                            rva = new BillAdapter(billingContext,data);
                            rv.setAdapter(rva);
                            rva.setClickListener(billingContext);
                        } else {
                            Toast.makeText(BillActivity.this,"Failure reading documents",Toast.LENGTH_LONG).show();
                        }


                    }
                });
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) this);
        popup.inflate(R.menu.bill_menu);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_bill:
                Intent intent=new Intent(this, NewBillActivity.class);
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }
}