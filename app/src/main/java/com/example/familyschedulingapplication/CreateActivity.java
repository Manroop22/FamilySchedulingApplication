package com.example.familyschedulingapplication;

import static java.util.UUID.randomUUID;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.familyschedulingapplication.Adapters.CategoryAdapter;
import com.example.familyschedulingapplication.Adapters.MemberAdapter;
import com.example.familyschedulingapplication.ModalBottomSheets.CategoryBottomSheet;
import com.example.familyschedulingapplication.Models.Activity;
import com.example.familyschedulingapplication.Models.Category;
import com.example.familyschedulingapplication.Models.Member;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

public class CreateActivity extends AppCompatActivity {
    EditText nameInput, dateInput, notesInput;
    Spinner categorySpinner, invitesSpinner;
    ImageButton backBtn, newCategoryBtn;
    Button saveBtn, cancelBtn;
    CheckBox smsCheckbox, pushCheckbox, emailCheckbox;
    CategoryAdapter categoryAdapter;
    MemberAdapter invitesAdapter;
    ArrayList<Category> categoryList;
    ArrayList<Member> invitesList;
    DatePickerDialog datePickerDialog;
    Calendar calendar = Calendar.getInstance();
    String dateString;
    FirebaseUser user;
    DocumentReference memberRef;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Member member;
    int year;
    int month;
    int day;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        member = Member.getMemberByUserId(user.getUid());
        memberRef = db.collection("members").document(user.getUid());
        nameInput = findViewById(R.id.billNameInput);
        dateInput = findViewById(R.id.dateInput);
        categorySpinner = findViewById(R.id.categorySpinner);
        notesInput = findViewById(R.id.noteInput);
        invitesSpinner = findViewById(R.id.permittedSpinner);
        backBtn = findViewById(R.id.exitBillBtn);
        newCategoryBtn = findViewById(R.id.newCategoryButton);
        saveBtn = findViewById(R.id.saveMsgBtn);
        cancelBtn = findViewById(R.id.cancelMsgBtn);
        smsCheckbox = findViewById(R.id.notifySMS);
        pushCheckbox = findViewById(R.id.notifyPush);
        emailCheckbox = findViewById(R.id.notifyEmail);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        backBtn.setOnClickListener(v -> {
            if (validateInputs(false)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateActivity.this);
                builder.setTitle("Discard Changes?");
                builder.setMessage("Are you sure you want to discard your changes?");
                builder.setPositiveButton("Yes", (dialog, which) -> finish());
                builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                builder.show();
            } else {
                finish();
            }
        });
        updateCategoryAdapter();
        newCategoryBtn.setOnClickListener(v -> {
            Log.d("CreateActivity", CategoryAdapter.categories.toString());
            CategoryBottomSheet.newInstance("add", null).show(getSupportFragmentManager(), "CreateCategory");
        });
        dateInput.setOnClickListener(this::showDatePickerDialog);
        saveBtn.setOnClickListener(v -> saveActivity());
        cancelBtn.setOnClickListener(v -> finish());
//        updateInvitesAdapter();
    }

    public void updateInvitesAdapter() {
        invitesList = new ArrayList<>();
        ArrayList<Member> members = Member.getMembersByHomeId(member.getHomeId().getId());
        for(Member m : members) {
            if(!m.getReference().equals(memberRef)) {
                invitesList.add(Member.getMemberByMemberId(m.getReference()));
            }
        }
        invitesAdapter = new MemberAdapter(this, invitesList);
        invitesAdapter.setDropDownViewResource(R.layout.member_item);
        invitesSpinner.setAdapter(invitesAdapter);
    }

    public void updateCategoryAdapter() {
        db.collection("categories").whereEqualTo("createdBy", memberRef).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot doc : task.getResult()) {
                    Category cat = doc.toObject(Category.class);
                    if (!CategoryAdapter.categories.contains(cat)) {
                        CategoryAdapter.categories.add(cat);
                    }
                }
                CategoryAdapter.categories = new ArrayList<>(new HashSet<>(CategoryAdapter.categories));
                categoryAdapter = new CategoryAdapter(CreateActivity.this, R.layout.category_array_item, CategoryAdapter.categories);
                categoryAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
                categorySpinner.setAdapter(categoryAdapter);
            } else {
                Log.d("CreateActivity", "Error getting categories", task.getException());
            }
        });
        Log.d("CreateActivity", CategoryAdapter.categories.toString());
    }

    public boolean validateInputs(boolean required) {
        if (nameInput.getText().toString().isEmpty()) {
            if (required) {
                Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
                nameInput.setError("Name is required");
            }
            return false;
        }

        if (dateInput.getText().toString().isEmpty()) {
            if (required) {
                Toast.makeText(this, "Please enter a date", Toast.LENGTH_SHORT).show();
                dateInput.setError("Date is required");
            }
            return false;
        }
        return true;
    }

    public void showDatePickerDialog(View v) {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().build();
        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        datePicker.addOnPositiveButtonClickListener(selection -> {
            dateString = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selection);
            dateInput.setText(dateString);
        });
    }

    public void saveActivity() {
        if (validateInputs(true)) {
            Activity activity = new Activity();
            activity.setName(nameInput.getText().toString());
            activity.setActivityDate(new Date(dateInput.getText().toString()));
            // get spinner selected item
            Log.d("CreateActivity", categorySpinner.getSelectedItem().toString());
            DocumentReference catRef = db.collection("categories").document(categoryAdapter.getItem(categorySpinner.getSelectedItemPosition()).getCategoryId());
            activity.setCategory(catRef);
            activity.setNotes(notesInput.getText().toString());
            activity.setCreatedBy(memberRef);
            activity.setCreatedAt(new Date());
            // get checkbox values
            ArrayList<String> notificationTypes = new ArrayList<>();
            if (smsCheckbox.isChecked()) {
                notificationTypes.add("sms");
            }
            if (pushCheckbox.isChecked()) {
                notificationTypes.add("push");
            }
            if (emailCheckbox.isChecked()) {
                notificationTypes.add("email");
            }
            activity.setNotificationMethod(notificationTypes);
            activity.setActivityId(randomUUID().toString());
            db.collection("activities").document(activity.getActivityId()).set(activity).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Activity created successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.d("CreateActivity", "Error creating activity", task.getException());
                }
            });
        }
    }
}