package com.example.familyschedulingapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

public class NewMessage extends AppCompatActivity {
    Button cancelMsgBtn;
    Button saveMsgBtn;
    ImageButton menuBtn;
    EditText titleInput;
    EditText msgInput;
    CheckBox smsCheckBox;
    CheckBox emailCheckBox;
    CheckBox pushCheckBox;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        cancelMsgBtn=findViewById(R.id.cancelMsgBtn);
        saveMsgBtn=findViewById(R.id.saveMsgBtn);
        menuBtn=findViewById(R.id.createMsgMenuBtn);
        titleInput=findViewById(R.id.msgTitleInput);
        msgInput=findViewById(R.id.msgMultiInput);
        smsCheckBox=findViewById(R.id.smsCheckBox);
        emailCheckBox=findViewById(R.id.emailCheckBox);
        pushCheckBox=findViewById(R.id.pushCheckBox);
        cancelMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // goes back to the MessageBoard activity.
            }
        });
        saveMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title=titleInput.getText().toString();
                String msgText=msgInput.getText().toString();

            }
        });
        ModalBottomSheet modalBottomSheet = new ModalBottomSheet();
        menuBtn.setOnClickListener(v -> modalBottomSheet.show(getSupportFragmentManager(), ModalBottomSheet.TAG));
    }

}