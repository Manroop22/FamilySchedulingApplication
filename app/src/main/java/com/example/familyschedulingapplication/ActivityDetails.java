package com.example.familyschedulingapplication;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;

public class ActivityDetails extends AppCompatActivity {
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
//    DocumentSnapshot activityRef;
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
        memberRef = db.collection("members").document(user.getUid());
        nameInput = findViewById(R.id.nameInput);
        dateInput = findViewById(R.id.dateInput);
        categorySpinner = findViewById(R.id.categorySpinner);
        notesInput = findViewById(R.id.notesInput);
        invitesSpinner = findViewById(R.id.inviteSpinner);
        backBtn = findViewById(R.id.exitList);
        newCategoryBtn = findViewById(R.id.newCategoryButton);
        editBtnActivity = findViewById(R.id.editBtnActivity);
        deleteBtnActivity = findViewById(R.id.deleteBtnActivity);
        saveBtn = findViewById(R.id.saveBtnNL);
        cancelBtn = findViewById(R.id.cancelBtnNL);
        smsCheckbox = findViewById(R.id.smsCheckBox);
        pushCheckbox = findViewById(R.id.pushCheckBox);
        emailCheckbox = findViewById(R.id.emailCheckBox);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        mode = getIntent().getStringExtra("mode");
        if (mode == null) {
            mode = "view";
        }
//        activity = Activity.getActivityById(getIntent().getStringExtra("activityId"));
        activity = Activity.getActivityById(getIntent().getStringExtra("activityId"), new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        activity = document.toObject(Activity.class);
                        init();
                        Log.d("ActivityDetails", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("ActivityDetails", "No such document");
                        finish();
                    }
                } else {
                    Log.d("ActivityDetails", "get failed with ", task.getException());
                    finish();
                }
            }
        });
    }

    public void init() {
        switchMode(mode);
        backBtn.setOnClickListener(v -> {
            if (validateInputs(false) && mode.equals("edit")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityDetails.this);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityDetails.this);
            builder.setTitle("Delete Activity?");
            builder.setMessage("Are you sure you want to delete this activity?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                db.collection("activities").document(activityId).delete().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Toast.makeText(ActivityDetails.this, "Activity Deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ActivityDetails.this, "Error Deleting Activity", Toast.LENGTH_SHORT).show();
                    }
                });
            });
            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            builder.show();
        });
        dateInput.setOnClickListener(this::showDatePickerDialog);
        saveBtn.setOnClickListener(v -> saveActivity());
        cancelBtn.setOnClickListener(v -> finish());
        updateCategoryAdapter();
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
                // remove duplicates
                CategoryAdapter.categories = new ArrayList<>(new HashSet<>(CategoryAdapter.categories));
                categoryAdapter = new CategoryAdapter(ActivityDetails.this, R.layout.category_array_item, CategoryAdapter.categories);
                categoryAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
                categorySpinner.setAdapter(categoryAdapter);
            } else {
                Log.d("CreateActivity", "Error getting categories", task.getException());
            }
        });
        Log.d("CreateActivity", CategoryAdapter.categories.toString());
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
        if (activity.getActivityDate() != null) {
            dateInput.setText(activity.getActivityDate().toString());
        }
        notesInput.setText(activity.getNotes());
        if (activity.getNotificationMethod() != null) {
            for (String method: activity.getNotificationMethod()) {
                if (method.equals("sms")) {
                    smsCheckbox.setChecked(true);
                } else if (method.equals("push")) {
                    pushCheckbox.setChecked(true);
                } else if (method.equals("email")) {
                    emailCheckbox.setChecked(true);
                }
            }
        }
        Category.getCategoryByReference(activity.getCategory(), task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Category cat = document.toObject(Category.class);
//                    categorySpinner.setSelection(categoryAdapter.getPosition(cat));
                    Log.d("ActivityDetails", "Position: " + categoryAdapter.getPosition(cat));
                    categorySpinner.setSelection(categoryAdapter.getPosition(cat));
                    Log.d("ActivityDetails", "DocumentSnapshot data: " + document.getData());
                } else {
                    Log.d("ActivityDetails", "No such document");
                }
            } else {
                Log.d("ActivityDetails", "get failed with ", task.getException());
            }
        });
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
                Toast.makeText(ActivityDetails.this, "Activity saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            }).addOnFailureListener(e -> {
                Toast.makeText(ActivityDetails.this, "Error saving activity", Toast.LENGTH_SHORT).show();
            });
            Activity.updateActivity(activityId, activity);
        }
    }

}