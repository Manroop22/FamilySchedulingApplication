package com.example.familyschedulingapplication;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import com.example.familyschedulingapplication.Adapters.*;
import com.example.familyschedulingapplication.ModalBottomSheets.MenuBottomSheet;
import com.example.familyschedulingapplication.Models.Bill;
import com.example.familyschedulingapplication.Models.Event;
import com.example.familyschedulingapplication.Models.Member;
import com.example.familyschedulingapplication.Models.Message;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

public class MessageBoard extends AppCompatActivity {
    private static final String TAG = "MessageBoard";
    private ArrayList<Event> eventList = new ArrayList<>();
    private ArrayList<Bill> billList=new ArrayList<>();
    private ArrayList<Message> messageList=new ArrayList<>();
    private EventAdapter eventAdapter;
    private BillAdapter billAdapter;
    private MessageAdapter messageAdapter;
    RecyclerView eventRecycler;
    RecyclerView billRecycler;
    RecyclerView messageRecycler;
    ExtendedFloatingActionButton addMsgBtn;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DividerItemDecoration dividerItemDecoration;
    ImageButton sync;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_board);
        MenuBottomSheet menuBottomSheet = new MenuBottomSheet();
        ImageButton menuBtn = findViewById(R.id.msgMenuBtn);
        menuBtn.setOnClickListener(v -> menuBottomSheet.show(getSupportFragmentManager(), MenuBottomSheet.TAG));
        eventRecycler=findViewById(R.id.eventsRecyclerView);
        billRecycler=findViewById(R.id.billsRecyclerView);
        messageRecycler=findViewById(R.id.messageRecyclerView);
        addMsgBtn=findViewById(R.id.addMsgBtn);
        sync=findViewById(R.id.refreshBoard);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        Member member = Member.getMemberByUserId(user.getUid());
        DocumentReference memberRef = db.collection("members").document(user.getUid());
        dividerItemDecoration = new DividerItemDecoration( this, DividerItemDecoration. VERTICAL);
        sync(memberRef);
        sync.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        });

        addMsgBtn.setOnClickListener(view -> {
            Intent intent=new Intent(view.getContext(), CreateMessage.class);
            startActivity(intent);
        });
    }

    public void sync(DocumentReference memberRef){
        initEvents(memberRef);
        initBills(memberRef);
        initMessages(memberRef);
    }

    private void initBills(DocumentReference memberRef) {
        billList = new ArrayList<>();
        billList.add(new Bill("Water Bill", new Date(),100)); // adds a default bill to check.
        billList.add(new Bill("Water Bill", new Date(),100)); // adds a default bill to check.
        billList.add(new Bill("Water Bill", new Date(),100)); // adds a default bill to check.
        billList.add(new Bill("Water Bill", new Date(),100)); // adds a default bill to check.
        billAdapter=new BillAdapter(billList);
        billRecycler.setLayoutManager(new LinearLayoutManager(this));
        billRecycler.setAdapter(billAdapter);
//        billRecycler.addItemDecoration(dividerItemDecoration);
    }

    private void initMessages(DocumentReference memberRef) {
        Message.getMessagesByMember(memberRef, task -> {
            if (task.isSuccessful()) {
                messageList = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult()) {
                    Message message = document.toObject(Message.class);
                    messageList.add(message);
                }
                Message.getMessagesReceived(memberRef, task1 -> {
                    if (task1.isSuccessful()) {
                        for (DocumentSnapshot document : task1.getResult()) {
                            Message message = document.toObject(Message.class);
                            messageList.add(message);
                        }
                        messageAdapter = new MessageAdapter(messageList);
                        messageRecycler.setLayoutManager(new LinearLayoutManager(this));
                        messageRecycler.setAdapter(messageAdapter);
//                        messageRecycler.addItemDecoration(dividerItemDecoration);
                    }
                });
            }
        });
    }

    public void initEvents(DocumentReference memRef) {
        db.collection("events").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                eventList = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult()) {
                    Event event = document.toObject(Event.class);
                    assert event != null;
                    if (event.getCreatedBy().equals(memRef) || event.getParticipants().contains(memRef)) {
                        eventList.add(event);
                    }
                }
                eventAdapter=new EventAdapter(eventList, R.layout.event);
                eventRecycler.setLayoutManager(new LinearLayoutManager(MessageBoard.this));
                eventRecycler.setAdapter(eventAdapter);
//                eventRecycler.addItemDecoration(dividerItemDecoration);
            }
        });
    }
}