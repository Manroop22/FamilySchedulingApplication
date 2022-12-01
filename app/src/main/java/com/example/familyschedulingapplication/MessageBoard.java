package com.example.familyschedulingapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MessageBoard extends AppCompatActivity {
    private static final String TAG = "MessageBoard";
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
        addMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(),NewMessage.class);
                startActivity(intent);
            }
        });
    }
}