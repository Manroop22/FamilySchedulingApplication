package com.example.familyschedulingapplication;

import static java.util.UUID.randomUUID;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.familyschedulingapplication.Models.Member;
import com.example.familyschedulingapplication.Models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

public class CreateMessage extends AppCompatActivity {
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
    ArrayList<String> notificationType = new ArrayList<>();
    FirebaseUser user;
    Member member;
    DocumentReference memberRef;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        user = FirebaseAuth.getInstance().getCurrentUser();
        memberRef = db.collection("members").document(user.getUid());
//        member = Member.getMemberByUserId(user.getUid());
        cancelMsgBtn=findViewById(R.id.cancelMsgBtn);
        saveMsgBtn=findViewById(R.id.saveMsgBtn);
        menuBtn=findViewById(R.id.exitBillBtn);
        titleInput=findViewById(R.id.billNameInput);
        msgInput=findViewById(R.id.noteInput);
        smsCheckBox=findViewById(R.id.notifySMS);
        emailCheckBox=findViewById(R.id.notifyEmail);
        pushCheckBox=findViewById(R.id.notifyPush);
        cancelMsgBtn.setOnClickListener(view -> {
            if (validateFields(false)) {
                // dialog asking if they are sure they want to cancel
                // if yes, go back to previous screen
                // if no, stay on this screen
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Cancel Message");
                builder.setMessage("Are you sure you want to cancel this message?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    finish();
                });
                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                });
            } else {
                finish();
            }
        });
        saveMsgBtn.setOnClickListener(view -> saveMessage());
        emailCheckBox.setOnClickListener(view -> {
            if (emailCheckBox.isChecked()) {
                notificationType.add("email");
            } else {
                notificationType.remove("email");
            }
        });
        smsCheckBox.setOnClickListener(view -> {
            if (smsCheckBox.isChecked()) {
                notificationType.add("sms");
            } else {
                notificationType.remove("sms");
            }
        });

        pushCheckBox.setOnClickListener(view -> {
            if (pushCheckBox.isChecked()) {
                notificationType.add("push");
            } else {
                notificationType.remove("push");
            }
        });
        menuBtn.setOnClickListener(view -> finish());
    }

    private void saveMessage() {
        title=titleInput.getText().toString();
        msgText=msgInput.getText().toString();
        if (notificationType.size() == 0) {
            notificationType.add("Push");
        }
        Message msg = new Message();
        msg.setTitle(title);
        msg.setMessage(msgText);
        msg.setNotificationType(notificationType);
        msg.setMessageId(randomUUID().toString());
        msg.setCreatedAt(new Date());
        msg.setPublished(true);
        msg.setCreatedBy(memberRef);
        Message.addMessage(msg, task -> finish());
    }

    public boolean validateFields(boolean checkErrors) {
        boolean valid = true;
        if (titleInput.getText().toString().isEmpty()) {
            if (checkErrors) {
                titleInput.setError("Title is required");
            }
            valid = false;
        }
        if (msgInput.getText().toString().isEmpty()) {
            if (checkErrors) {
                msgInput.setError("Message is required");
            }
            valid = false;
        }
        return valid;
    }
}