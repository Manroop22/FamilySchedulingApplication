package com.example.familyschedulingapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class NewMessage extends AppCompatActivity {
    Button cancelMsgBtn;
    Button saveMsgBtn;
    ImageButton menuBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        cancelMsgBtn=findViewById(R.id.cancelMsgBtn);
        saveMsgBtn=findViewById(R.id.saveMsgBtn);
        menuBtn=findViewById(R.id.createMsgMenuBtn);
        cancelMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // goes back to the MessageBoard activity.
            }
        });
        saveMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // to be Implemented.
            }
        });
        ModalBottomSheet modalBottomSheet = new ModalBottomSheet();
        menuBtn.setOnClickListener(v -> modalBottomSheet.show(getSupportFragmentManager(), ModalBottomSheet.TAG));
    }

}