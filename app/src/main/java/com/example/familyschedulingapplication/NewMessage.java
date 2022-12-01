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
    String title;
    String msgText;
    String notificationType;
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
                title=titleInput.getText().toString();
                msgText=msgInput.getText().toString();
                if(notificationType.equals(""))
                    notificationType="PUSH"; // The default type is no checkBox is selected.
                Message msg=new Message(title,msgText,notificationType);

            }
        });
        ModalBottomSheet modalBottomSheet = new ModalBottomSheet();
        menuBtn.setOnClickListener(v -> modalBottomSheet.show(getSupportFragmentManager(), ModalBottomSheet.TAG));
    }
    public void onCheckBoxClicked(View view){
        // If the user does not select any of the the notificationTypes then the default notification type shall be the default.
        boolean checked=((CheckBox)view).isChecked();
        switch (view.getId()) {
            case R.id.smsCheckBox:
                if (checked)
                    notificationType="SMS";
                break;
            case R.id.emailCheckBox:
                if (checked)
                    notificationType="EMAIL";
                break;
            case R.id.pushCheckBox:
                if(checked)
                    notificationType="PUSH";
                break;
            default:
                notificationType="PUSH";
        }
    }
}