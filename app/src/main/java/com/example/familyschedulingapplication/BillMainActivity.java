package com.example.familyschedulingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familyschedulingapplication.Adapters.BillAdapter;
import com.example.familyschedulingapplication.ModalBottomSheets.MenuBottomSheet;
import com.example.familyschedulingapplication.Models.Bill;
import com.example.familyschedulingapplication.Models.Member;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class BillMainActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView rv;
    TextView noOfBills;
    String currentTab = "upcoming";
    FirebaseUser user;
    Member member;
    DocumentReference memberRef;
    FloatingActionButton addBillBtn;
    BillAdapter billAdapter;
    ImageButton sync;
    ArrayList<Bill> billsList;
    private static final String TAG = "BillMainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_main);
        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
//        member = Member.getMemberByUserId(user.getUid());
        memberRef = db.collection(Member.collection).document(user.getUid());
        rv = (RecyclerView) findViewById(R.id.recyclerView);
        noOfBills = (TextView) findViewById(R.id.billAlert);
        addBillBtn = findViewById(R.id.billAddBtn);
        sync = findViewById(R.id.syncBills);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        MenuBottomSheet menuBottomSheet = new MenuBottomSheet();
        ImageButton menuBtn = findViewById(R.id.billMenuBtn);
        TabLayout.Tab tab = tabLayout.getTabAt(0);
        if (tab != null) {
            tab.select();
            currentTab = Objects.requireNonNull(tab.getText()).toString().toLowerCase(Locale.ROOT);
        } else {
            currentTab = "upcoming";
        }
        Objects.requireNonNull(tab).select();
        updateBillList(currentTab);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab= Objects.requireNonNull(tab.getText()).toString().toLowerCase(Locale.ROOT);
                updateBillList(currentTab.toLowerCase(Locale.ROOT));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                currentTab = Objects.requireNonNull(tab.getText()).toString().toLowerCase(Locale.ROOT);
                updateBillList(currentTab.toLowerCase(Locale.ROOT));

            }
        });
        noOfBills = findViewById(R.id.billAlert);
        addBillBtn.setOnClickListener(v -> {
            Bundle newBillBundle = new Bundle();
            newBillBundle.putString("mode", "add");
            Intent intent = new Intent(BillMainActivity.this, BillDetails.class);
            intent.putExtras(newBillBundle);
            startActivity(intent);
        });
        menuBtn.setOnClickListener(v -> menuBottomSheet.show(getSupportFragmentManager(), MenuBottomSheet.TAG));
        sync.setOnClickListener(v -> updateBillList(currentTab));
    }
    public void updateBillList(String tab) {
        Bill.getBillsByMember(memberRef, task -> {
            if (task.isSuccessful()) {
                billsList = new ArrayList<>();
                final int[] overdueCount = {0};
                for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    Bill bill = document.toObject(Bill.class);
                    assert bill != null;
                    if (tab.equals("upcoming")) {
                        if (bill.getDueDate().after(new Date(System.currentTimeMillis())) && !bill.getPaid()) {
                            billsList.add(bill);
                        }
                    } else if (tab.equals("past")) {
                        if (bill.getDueDate().before(new Date(System.currentTimeMillis()))) {
                            billsList.add(bill);
                        }
                    } else if (tab.equals("all")) {
                        billsList.add(bill);
                    } else if (tab.equals("overdue")) {
                        if (bill.getDueDate().before(new Date(System.currentTimeMillis())) && !bill.getPaid()) {
                            billsList.add(bill);
                            overdueCount[0]++;
                        }
                    }
                }
                Bill.getBillsIfPermited(memberRef, task1 -> {
                    if (task1.isSuccessful()) {
                        for (DocumentSnapshot document : Objects.requireNonNull(task1.getResult())) {
                            Bill bill = document.toObject(Bill.class);
                            assert bill != null;
                            if (tab.equals("upcoming")) {
                                if (bill.getDueDate().after(new Date()) && !bill.getPaid()) {
                                    billsList.add(bill);
                                }
                            } else if (tab.equals("past")) {
                                if (bill.getDueDate().before(new Date())) {
                                    billsList.add(bill);
                                }
                            } else if (tab.equals("all")) {
                                billsList.add(bill);
                            } else if (tab.equals("overdue")) {
                                if (bill.getDueDate().before(new Date()) && !bill.getPaid()) {
                                    billsList.add(bill);
                                    overdueCount[0]++;
                                }
                            }
                        }
                        if (billsList.size() > 0 && !Objects.equals(tab, "all")) {
                            noOfBills.setVisibility(View.VISIBLE);
                        } else {
                            noOfBills.setVisibility(View.GONE);
                        }
                        noOfBills.setText(String.format("You have %d %s bills", tab == "overdue" ? overdueCount[0] : billsList.size(), tab));
                        billAdapter = new BillAdapter(billsList);
                        rv.setAdapter(billAdapter);
                        rv.setLayoutManager(new LinearLayoutManager(this));
//                        rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
                        Objects.requireNonNull(rv.getLayoutManager()).onRestoreInstanceState(rv.getLayoutManager().onSaveInstanceState());
                    }
                });
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }
}