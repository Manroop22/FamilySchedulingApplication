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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    //ArrayList<String> list=new ArrayList<String>();
    TextView noOfBills;
    String currentTab="upcoming";
    BillActivity billingContext;
    SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        billingContext = this;
        setContentView(R.layout.activity_bill);
        rv = (RecyclerView) findViewById(R.id.recycleView);
        db = FirebaseFirestore.getInstance();
        TabLayout tabLayout = findViewById(R.id.tab);
        TabLayout.Tab tab = tabLayout.getTabAt(0);
       if (tab != null) {
            tab.select();
            currentTab = Objects.requireNonNull(tab.getText()).toString().toLowerCase(Locale.ROOT);
        } else {
            currentTab = "upcoming";
        }
        Objects.requireNonNull(tab).select();
        updateBillList(Objects.requireNonNull(tab.getText()).toString());
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab= Objects.requireNonNull(tab.getText()).toString().toLowerCase(Locale.ROOT);
                Log.d("Current Tab", currentTab);
                updateBillList(currentTab.toLowerCase(Locale.ROOT));

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                currentTab = Objects.requireNonNull(tab.getText()).toString().toLowerCase(Locale.ROOT);
                Log.d("Current Tab", currentTab);
                updateBillList(currentTab.toLowerCase(Locale.ROOT));

            }
        });
        ModalBottomSheet modalBottomSheet = new ModalBottomSheet();
        ImageButton menuBtn = findViewById(R.id.imageButton);
        noOfBills = findViewById(R.id.textView14);
        menuBtn.setOnClickListener(v -> modalBottomSheet.show(getSupportFragmentManager(), ModalBottomSheet.TAG));

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
        //bundle.putString("billId",name);
        intent.putExtras(bundle);
        startActivity(intent);


    }
    public void updateBillList(String tab) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //DocumentReference memRef = db.collection("members").document(user.getUid());
        db.collection("bills").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot document = task.getResult();
                ArrayList<Object> billList = new ArrayList<Object>();
                for(DocumentSnapshot doc : Objects.requireNonNull(document).getDocuments()) {
                    // if doc's createdBy is current user, add to list
                    // if doc's partcipants contains memRef, add to list
                    Map<String, Object> data = doc.getData();
                    Date date = doc.getDate("due");
                        switch(tab) {
                            case "upcoming":
                                if (Objects.requireNonNull(doc.getDate("due")).after(new Date())) {
                                    billList.add("Bill Name: "+data.get("name").toString()+"  " +"Due Date: " +new SimpleDateFormat("dd MMMM yyyy").format(date));
                                }
                                break;
                            case "past":
                                if (Objects.requireNonNull(doc.getDate("due")).before(new Date())) {
                                    billList.add("Bill Name: "+data.get("name").toString()+"  " +"Due Date: " +new SimpleDateFormat("dd MMMM yyyy").format(date));
                                }
                                break;
                            case "all":
                                billList.add("Bill Name: "+data.get("name").toString()+"  " +"Due Date: " +new SimpleDateFormat("dd MMMM yyyy").format(date));
                                break;
                        }
                    }
                Log.d(TAG, String.valueOf(billList.size()));
                if(currentTab.equals("upcoming"))
                    noOfBills.setText("You have "+billList.size()+" Upcoming Bills");
                else if(currentTab.equals("past"))
                    noOfBills.setText("You have "+billList.size()+" Past Bills");
                else
                    noOfBills.setText("You have "+billList.size()+" Bills");
                data = new String[billList.size()];
                for(int i=0;i<data.length;i++)
                {
                    data[i]= (String) billList.get(i);
                }
                rva = new BillAdapter(billingContext,data);
                rv.setAdapter(rva);
                rv.setLayoutManager(new LinearLayoutManager(billingContext));
                rv.addItemDecoration(new DividerItemDecoration(billingContext,
                        DividerItemDecoration.VERTICAL));
                rva.setClickListener(billingContext);
                Objects.requireNonNull(rv.getLayoutManager()).onRestoreInstanceState(rv.getLayoutManager().onSaveInstanceState());
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
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