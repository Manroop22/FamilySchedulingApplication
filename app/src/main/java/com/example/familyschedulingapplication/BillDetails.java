package com.example.familyschedulingapplication;

import static java.util.UUID.randomUUID;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.familyschedulingapplication.Models.Bill;
import com.example.familyschedulingapplication.Models.Member;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class BillDetails extends AppCompatActivity {
    private FirebaseFirestore db;
    TextView billMode;
    EditText billName, billAmount, dateInput, linkInput, noteInput;
    ImageButton exitBillBtn, editBillBtn, deleteBillBtn;
    Button saveBillBtn, cancelBillBtn, payBillBtn;
    Spinner permittedSpinner;
    CheckBox notifySMS, notifyEmail, notifyPush;
    String mode, billId, dateString;
    SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
    Bill bill;
    DocumentReference memRef;
    static final String TAG = "IndividualBillActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_details);
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        memRef = db.collection(Member.collection).document(user.getUid());
        mode = getIntent().getStringExtra("mode");
        billId = getIntent().getStringExtra("billId");
        billMode = findViewById(R.id.billMode);
        billName = findViewById(R.id.billNameInput);
        billAmount = findViewById(R.id.amountInput);
        dateInput = findViewById(R.id.dateInput);
        linkInput = findViewById(R.id.linkInput);
        noteInput = findViewById(R.id.noteInput);
        exitBillBtn = findViewById(R.id.exitBillBtn);
        editBillBtn = findViewById(R.id.editBillBtn);
        deleteBillBtn = findViewById(R.id.deleteBillBtn);
        saveBillBtn = findViewById(R.id.saveBillBtn);
        cancelBillBtn = findViewById(R.id.cancelBillBtn);
        payBillBtn = findViewById(R.id.payBillBtn);
        permittedSpinner = findViewById(R.id.permittedSpinner);
        notifySMS = findViewById(R.id.notifySMS);
        notifyEmail = findViewById(R.id.notifyEmail);
        notifyPush = findViewById(R.id.notifyPush);
        if (mode == null) {
            mode = "add";
        }
        mode = mode.toLowerCase(Locale.ROOT);
        Toast.makeText(this, mode, Toast.LENGTH_SHORT).show();
        if (billId == null) {
            bill = new Bill();
            initBill();
        } else {
            Bill.getBill(billId, task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        bill = Bill.getBill(task.getResult());
                        initBill();
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Toast.makeText(getApplicationContext(), "Bill loaded successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, "No such document");
                        Toast.makeText(getApplicationContext(), "Bill not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
        }
    }

    public void initBill() {
        switchMode(mode);
        editBillBtn.setOnClickListener(v -> {
            mode = "edit";
            switchMode(mode);
        });
        deleteBillBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete Bill");
            builder.setMessage("Are you sure you want to delete this bill?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                Bill.deleteBill(bill, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Bill deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Error deleting bill", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });
        dateInput.setOnClickListener(this::showDatePickerDialog);
        saveBillBtn.setOnClickListener(v -> saveBill(mode));
        payBillBtn.setOnClickListener(v -> payNow());
        cancelBillBtn.setOnClickListener(v -> {
            if (mode.equals("add")) {
                finish();
            } else {
                mode = "view";
                switchMode(mode);
            }
        });
        exitBillBtn.setOnClickListener(v -> {
            if (!mode.equals("view")) {
                if (validateFields(false)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Exit Bill");
                    builder.setMessage("Are you sure you want to exit without saving?");
                    builder.setPositiveButton("Yes", (dialog, which) -> {
                        finish();
                    });
                    builder.setNegativeButton("No", (dialog, which) -> {
                    });
                    builder.show();
                } else {
                    finish();
                }
            } else {
                finish();
            }
        });
    }

    public void switchMode(String mode) {
        switch (mode) {
            case "add":
            case "create":
            case "new":
                billMode.setText(R.string.add_bill);
                billName.setEnabled(true);
                billAmount.setEnabled(true);
                dateInput.setEnabled(true);
                linkInput.setEnabled(true);
                noteInput.setEnabled(true);
                permittedSpinner.setEnabled(true);
                notifySMS.setEnabled(true);
                notifyEmail.setEnabled(true);
                notifyPush.setEnabled(true);
                saveBillBtn.setVisibility(View.VISIBLE);
                cancelBillBtn.setVisibility(View.VISIBLE);
                payBillBtn.setVisibility(View.GONE);
                editBillBtn.setVisibility(View.GONE);
                deleteBillBtn.setVisibility(View.GONE);
                break;
            case "edit":
                billMode.setText(R.string.edit_bill);
                billName.setEnabled(true);
                billAmount.setEnabled(true);
                dateInput.setEnabled(true);
                linkInput.setEnabled(true);
                noteInput.setEnabled(true);
                permittedSpinner.setEnabled(true);
                notifySMS.setEnabled(true);
                notifyEmail.setEnabled(true);
                notifyPush.setEnabled(true);
                saveBillBtn.setVisibility(View.VISIBLE);
                cancelBillBtn.setVisibility(View.VISIBLE);
                payBillBtn.setVisibility(View.GONE);
                editBillBtn.setVisibility(View.GONE);
                deleteBillBtn.setVisibility(View.GONE);
                break;
            case "view":
                billMode.setText(R.string.bill_details);
                billName.setEnabled(false);
                billAmount.setEnabled(false);
                dateInput.setEnabled(false);
                linkInput.setEnabled(false);
                noteInput.setEnabled(false);
                permittedSpinner.setEnabled(false);
                notifySMS.setEnabled(false);
                notifyEmail.setEnabled(false);
                notifyPush.setEnabled(false);
                saveBillBtn.setVisibility(View.GONE);
                cancelBillBtn.setVisibility(View.GONE);
                payBillBtn.setVisibility(View.VISIBLE);
                editBillBtn.setVisibility(View.VISIBLE);
                deleteBillBtn.setVisibility(View.VISIBLE);
                break;
        }
        setValues(mode);
    }

    public void payNow() {
        try{
            String url = linkInput.getText().toString();
            // if url is empty warn user
            if (url.isEmpty()) {
                Toast.makeText(this, "Please enter a valid link", Toast.LENGTH_SHORT).show();
            } else {
                // if url is not empty open it in browser
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
                // alert dialog to confirm if bill is paid
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Confirm Payment");
                builder.setMessage("Have you paid this bill?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    bill.setPaid(true);
                    bill.setPaidBy(memRef);
                    bill.setUpdatedAt(new Date());
                    bill.setPaidAt(new Date());
                    Bill.updateBill(bill, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Bill paid", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Error updating bill", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
                builder.setNegativeButton("No", (dialog, which) -> {
                    Toast.makeText(this, "Bill not paid", Toast.LENGTH_SHORT).show();
                });
            }
        } catch (Exception e){
            Toast.makeText(BillDetails.this, "Error trying to open link", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "payNow: "+e);
        }
    }

    public void showDatePickerDialog(View v) {
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select a date");
        MaterialDatePicker<Long> materialDatePicker = builder.build();
        materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            Date date = new Date((Long) selection);
            dateString = sd.format(date);
            dateInput.setText(dateString);
        });
    }

    public boolean validateFields(boolean checkErrors) {
        boolean valid = true;
        if (dateInput.getText().toString().isEmpty()) {
            if (checkErrors) {
                dateInput.setError("Date is required");
            }
            valid = false;
        } else {
            dateInput.setError(null);
        }
        if (billName.getText().toString().isEmpty()) {
            if (checkErrors) {
                billName.setError("Bill name is required");
            }
            valid = false;
        } else {
            billName.setError(null);
        }

        if (billAmount.getText().toString().isEmpty()) {
            if (checkErrors) {
                billAmount.setError("Bill amount is required");
            }
            valid = false;
        } else {
            billAmount.setError(null);
        }

        if (linkInput.getText().toString().isEmpty()) {
            if (checkErrors) {
                linkInput.setError("Link is required");
            }
            valid = false;
        } else {
            linkInput.setError(null);
        }
        return valid;
    }

    public void setValues(String  mode) {
        switch (mode) {
            case "add":
            case "create":
            case "new":
                // set views
                billName.setText("");
                billAmount.setText("");
                dateInput.setText("");
                linkInput.setText("");
                noteInput.setText("");
                notifySMS.setChecked(false);
                notifyEmail.setChecked(false);
                notifyPush.setChecked(false);
                break;
            case "edit":
            case "view":
                // set views
                billName.setText(bill.getName());
                billAmount.setText(String.format("%s", bill.getAmount()));
                dateInput.setText(bill.getDueDate().toString());
                linkInput.setText(bill.getLink());
                noteInput.setText(bill.getNote());
                for (String noti: bill.getNotificationType()) {
                    switch (noti) {
                        case "sms":
                            notifySMS.setChecked(true);
                            break;
                        case "email":
                            notifyEmail.setChecked(true);
                            break;
                        case "push":
                            notifyPush.setChecked(true);
                            break;
                    }
                }
                break;
        }
    }

    public void saveBill(String mode) {
        if (validateFields(true)) {
            switch (mode) {
                case "edit":
                    if (bill == null) {
                        bill = new Bill();
                        bill.setBillId(randomUUID().toString());
                    }
                    bill.setName(billName.getText().toString());
                    bill.setAmount(Double.parseDouble(billAmount.getText().toString()));
//                    bill.setDueDate(new Date(dateInput.getText().toString()));
                    // use simple date format to convert string to date
                    try {
                        bill.setDueDate(sd.parse(dateInput.getText().toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    bill.setLink(linkInput.getText().toString());
                    bill.setNote(noteInput.getText().toString());
                    ArrayList<String> notify2 = new ArrayList<>();
                    if (notifySMS.isChecked()) {
                        notify2.add("sms");
                    }
                    if (notifyEmail.isChecked()) {
                        notify2.add("email");
                    }
                    if (notifyPush.isChecked()) {
                        notify2.add("push");
                    }
                    bill.setNotificationType(notify2);
                    bill.setOccurrence(1);
                    bill.setNote(noteInput.getText().toString());
                    bill.setUpdatedAt(new Date());
                    Bill.updateBill(bill, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Bill updated", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                    break;
                case "add":
                case "create":
                case "new":
                    Bill bill = new Bill();
                    bill.setBillId(randomUUID().toString());
                    bill.setName(billName.getText().toString());
//                    bill.setDueDate(new Date(dateInput.getText().toString()));
                    try {
                        bill.setDueDate(sd.parse(dateInput.getText().toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    bill.setCreatedAt(new Date());
                    bill.setCreatedBy(memRef);
                    bill.setAmount(Double.parseDouble(billAmount.getText().toString()));
                    bill.setLink(linkInput.getText().toString());
                    bill.setNote(noteInput.getText().toString());
                    ArrayList<String> notify = new ArrayList<>();
                    if (notifySMS.isChecked()) {
                        notify.add("sms");
                    }
                    if (notifyEmail.isChecked()) {
                        notify.add("email");
                    }
                    if (notifyPush.isChecked()) {
                        notify.add("push");
                    }
                    bill.setNotificationType(notify);
                    bill.setOccurrence(1);
                    bill.setPaid(false);
                    bill.setPaidAt(null);
                    bill.setNote(noteInput.getText().toString());
                    Bill.addBill(bill, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Bill added", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                    break;
            }
        }
    }

}