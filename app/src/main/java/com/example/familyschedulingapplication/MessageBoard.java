package com.example.familyschedulingapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.Date;

public class MessageBoard extends AppCompatActivity {
    private static final String TAG = "MessageBoard";
    private ArrayList<Event> eventList = new ArrayList<>();
    private ArrayList<Bill> billList=new ArrayList<>();
    private EventAdapter eventAdapter;
    private BillAdapter billAdapter;
    RecyclerView eventRecycler;
    RecyclerView billRecycler;
    RecyclerView msgRecycler;
    ImageButton addMsgBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_board);
        ModalBottomSheet modalBottomSheet = new ModalBottomSheet();
        ImageButton menuBtn = findViewById(R.id.msgMenuBtn);
        menuBtn.setOnClickListener(v -> modalBottomSheet.show(getSupportFragmentManager(), ModalBottomSheet.TAG));
        eventRecycler=findViewById(R.id.eventsRecyclerView);
        billRecycler=findViewById(R.id.billsRecyclerView);
        msgRecycler=findViewById(R.id.messageRecyclerView);
        addMsgBtn=findViewById(R.id.addMsgBtn);
        // This is the test case for the eventsRecycler view.
        eventList.add(new Event("Cosc310 project", new Date()));
        eventList.add(new Event("Cosc310 project", new Date()));
        eventList.add(new Event("Cosc310 project", new Date()));
        eventList.add(new Event("Cosc310 project", new Date()));
        eventAdapter=new EventAdapter(eventList);
        eventRecycler.setLayoutManager(new LinearLayoutManager(this));
        eventRecycler.setAdapter(eventAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration( this, DividerItemDecoration. VERTICAL);
        eventRecycler.addItemDecoration(dividerItemDecoration);

        // This the test case for the billsRecyclerView.
        billList.add(new Bill("Water Bill", new Date(),100)); // adds a default bill to check.
        billList.add(new Bill("Water Bill", new Date(),100)); // adds a default bill to check.
        billList.add(new Bill("Water Bill", new Date(),100)); // adds a default bill to check.
        billList.add(new Bill("Water Bill", new Date(),100)); // adds a default bill to check.
        billAdapter=new BillAdapter(billList);
        billRecycler.setLayoutManager(new LinearLayoutManager(this));
        billRecycler.setAdapter(billAdapter);
        billRecycler.addItemDecoration(dividerItemDecoration);

        // This is the test case for for the Message.

        addMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(),NewMessage.class);
                startActivity(intent);
            }
        });
    }
}