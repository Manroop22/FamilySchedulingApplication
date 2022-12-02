package com.example.familyschedulingapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.familyschedulingapplication.Adapters.CategoryAdapter;
import com.example.familyschedulingapplication.Adapters.MemberAdapter;
import com.example.familyschedulingapplication.ModalBottomSheets.CategoryBottomSheet;
import com.example.familyschedulingapplication.ModalBottomSheets.MenuBottomSheet;
import com.example.familyschedulingapplication.Models.Activity;
import com.example.familyschedulingapplication.Models.Category;
import com.example.familyschedulingapplication.Models.Member;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class activity_desc extends AppCompatActivity {
    EditText nameInput, dateInput, notesInput;
    Spinner categorySpinner, invitesSpinner;
    ImageButton backBtn, newCategoryBtn, editBtnActivity, deleteBtnActivity;
    Button saveBtn, cancelBtn;
    CheckBox smsCheckbox, pushCheckbox, emailCheckbox;
    CategoryAdapter categoryAdapter;
    MemberAdapter invitesAdapter;
    ArrayList<Category> categoryList;
    ArrayList<Member> invitesList;
    DatePickerDialog datePickerDialog;
    Calendar calendar = Calendar.getInstance();
    String dateString, mode = "view", activityId;
    FirebaseUser user;
    DocumentReference memberRef;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Member member;
    Activity activity;
    int year;
    int month;
    int day;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desc);
        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        member = Member.getMemberByUserId(user.getUid());
        memberRef = member.getReference();
        nameInput = findViewById(R.id.nameInput);
        dateInput = findViewById(R.id.dateInput);
        categorySpinner = findViewById(R.id.categorySpinner);
        notesInput = findViewById(R.id.notesInput);
        invitesSpinner = findViewById(R.id.inviteSpinner);
        backBtn = findViewById(R.id.backBtnNewActivity);
        newCategoryBtn = findViewById(R.id.newCategoryButton);
        editBtnActivity = findViewById(R.id.editBtnActivity);
        deleteBtnActivity = findViewById(R.id.deleteBtnActivity);
        saveBtn = findViewById(R.id.saveBtnNewActivity);
        cancelBtn = findViewById(R.id.cancelBtnNewActivity);
        smsCheckbox = findViewById(R.id.smsCheckBox);
        pushCheckbox = findViewById(R.id.pushCheckBox);
        emailCheckbox = findViewById(R.id.emailCheckBox);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        activityId = getIntent().getStringExtra("activityId");
        activity = Activity.getActivityById(activityId);
        mode = getIntent().getStringExtra("mode");
        if (mode == null) {
            mode = "view";
        }
        switchMode(mode);
        backBtn.setOnClickListener(v -> {
            if (validateInputs(false)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity_desc.this);
                builder.setTitle("Discard Changes?");
                builder.setMessage("Are you sure you want to discard your changes?");
                builder.setPositiveButton("Yes", (dialog, which) -> finish());
                builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                builder.show();
            } else {
                finish();
            }
        });
        newCategoryBtn.setOnClickListener(v -> {
            CategoryBottomSheet categoryBottomSheet = new CategoryBottomSheet();
            categoryBottomSheet.show(getSupportFragmentManager(), "categoryBottomSheet");
        });
        editBtnActivity.setOnClickListener(v -> {
            mode = "edit";
            switchMode(mode);
        });
        deleteBtnActivity.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity_desc.this);
            builder.setTitle("Delete Activity?");
            builder.setMessage("Are you sure you want to delete this activity?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                db.collection("activities").document(activityId).delete().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(activity_desc.this, "Activity Deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(activity_desc.this, "Error Deleting Activity", Toast.LENGTH_SHORT).show();
                    }
                });
            });
            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            builder.show();
        });
        dateInput.setOnClickListener(this::showDatePickerDialog);
        saveBtn.setOnClickListener(v -> saveActivity());
        cancelBtn.setOnClickListener(v -> finish());
    }

    public void showDatePickerDialog(View v) {
        datePickerDialog = new DatePickerDialog(this, (DatePickerDialog.OnDateSetListener) (view, year, month, dayOfMonth) -> {
            month = month + 1;
            dateString = dayOfMonth + "/" + month + "/" + year;
            dateInput.setText(dateString);
        }, year, month, day);
        datePickerDialog.show();
    }

    public void setValues() {
        nameInput.setText(activity.getName());
        dateInput.setText(activity.getActivityDate().toString());
        notesInput.setText(activity.getNotes());
        smsCheckbox.setChecked(activity.getNotificationMethod().contains("sms"));
        pushCheckbox.setChecked(activity.getNotificationMethod().contains("push"));
        emailCheckbox.setChecked(activity.getNotificationMethod().contains("email"));
//        categoryList = Category.getCategoriesByCreatedBy(memberRef);
//        categoryAdapter = new CategoryAdapter(this, categoryList);
//        categorySpinner.setAdapter(categoryAdapter);
//        categorySpinner.setSelection(categoryAdapter.getPosition(activity.getCategory()));
//        invitesList = Member.getMembersByHomeId(member.getHomeId().getId());
//        invitesAdapter = new MemberAdapter(this, invitesList);
//        invitesSpinner.setAdapter(invitesAdapter);
//        invitesSpinner.setSelection(invitesAdapter.getPosition(activity.getInvites()));
    }

    public void switchMode(String mode) {
        if (mode.equals("view")) {
            nameInput.setEnabled(false);
            dateInput.setEnabled(false);
            categorySpinner.setEnabled(false);
            notesInput.setEnabled(false);
            invitesSpinner.setEnabled(false);
            smsCheckbox.setEnabled(false);
            pushCheckbox.setEnabled(false);
            emailCheckbox.setEnabled(false);
            newCategoryBtn.setVisibility(View.GONE);
            editBtnActivity.setVisibility(View.VISIBLE);
            deleteBtnActivity.setVisibility(View.VISIBLE);
            saveBtn.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.GONE);
        } else if (mode.equals("edit")) {
            nameInput.setEnabled(true);
            dateInput.setEnabled(true);
            categorySpinner.setEnabled(true);
            notesInput.setEnabled(true);
            invitesSpinner.setEnabled(true);
            smsCheckbox.setEnabled(true);
            pushCheckbox.setEnabled(true);
            emailCheckbox.setEnabled(true);
            newCategoryBtn.setVisibility(View.VISIBLE);
            editBtnActivity.setVisibility(View.GONE);
            deleteBtnActivity.setVisibility(View.GONE);
            saveBtn.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.VISIBLE);
        }
        setValues();
    }

    public boolean validateInputs(boolean showErrors) {
        boolean valid = true;
        if (nameInput.getText().toString().isEmpty()) {
            if (showErrors) {
                nameInput.setError("Please enter a name");
            }
            valid = false;
        }
        if (dateInput.getText().toString().isEmpty()) {
            if (showErrors) {
                dateInput.setError("Please enter a date");
            }
            valid = false;
        }
        return valid;
    }

    public void saveActivity() {
        if (validateInputs(true)) {
            Activity activity = new Activity();
            activity.setName(nameInput.getText().toString());
            activity.setActivityDate(new Date(dateInput.getText().toString()));
            // get spinner selected item
            activity.setCategory(categoryList.get(categorySpinner.getSelectedItemPosition()).getReference());
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
            activity.setInvites(invitesAdapter.selectedMembers);
            db.collection("activities").add(activity).addOnSuccessListener(documentReference -> {
                Toast.makeText(activity_desc.this, "Activity saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            }).addOnFailureListener(e -> {
                Toast.makeText(activity_desc.this, "Error saving activity", Toast.LENGTH_SHORT).show();
            });
            Activity.updateActivity(activityId, activity);
        }
    }

}