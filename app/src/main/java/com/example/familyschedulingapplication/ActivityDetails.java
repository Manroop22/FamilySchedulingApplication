package com.example.familyschedulingapplication;

import static java.util.UUID.randomUUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
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
import com.example.familyschedulingapplication.Models.Home;
import com.example.familyschedulingapplication.Models.Member;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.UUID;

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
    String dateString, mode = "view", activityId;
    FirebaseUser user;
    DocumentReference memberRef;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Member member;
    Activity activity;
    SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desc);
        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        memberRef = db.collection("members").document(user.getUid());
        nameInput = findViewById(R.id.billNameInput);
        dateInput = findViewById(R.id.dateInput);
        categorySpinner = findViewById(R.id.categorySpinner);
        notesInput = findViewById(R.id.noteInput);
        invitesSpinner = findViewById(R.id.permittedSpinner);
        backBtn = findViewById(R.id.exitBillBtn);
        newCategoryBtn = findViewById(R.id.newCategoryButton);
        editBtnActivity = findViewById(R.id.editBillBtn);
        deleteBtnActivity = findViewById(R.id.deleteBillBtn);
        saveBtn = findViewById(R.id.saveMsgBtn);
        cancelBtn = findViewById(R.id.cancelMsgBtn);
        smsCheckbox = findViewById(R.id.notifySMS);
        pushCheckbox = findViewById(R.id.notifyPush);
        emailCheckbox = findViewById(R.id.notifyEmail);
        mode = getIntent().getStringExtra("mode");
        if (mode == null) {
            mode = "add";
        }
        activityId = getIntent().getStringExtra("activityId");
        Activity.getActivity(activityId, task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d("ActivityDetails", "DocumentSnapshot data: " + document.getData());
                    Member.getMember(user.getUid(), task1 -> {
                        member = task1.getResult().toObject(Member.class);
                        activity = document.toObject(Activity.class);
                        init();
                    });
                } else {
                    Log.d("ActivityDetails", "No such document");
                    finish();
                }
            } else {
                Log.d("ActivityDetails", "get failed with ", task.getException());
                finish();
            }
        });
    }

    public void init() {
        updateCategoryAdapter();
        updateInvitesAdapter();
        switchMode(mode);
        backBtn.setOnClickListener(v -> {
            if (validateInputs(false) && !mode.equals("view")) {
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
            categoryBottomSheet.show(getSupportFragmentManager(), CategoryBottomSheet.TAG);
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
                Activity.deleteActivity(activity, task -> {
                    if (task.isSuccessful()) {
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
    }

    public void updateInvitesAdapter() {
        Member.getMembersByHome(member.getHomeId(), (OnCompleteListener<QuerySnapshot>) task -> {
            if (task.isSuccessful()) {
                invitesList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    invitesList.add(document.toObject(Member.class));
                }
                invitesAdapter = new MemberAdapter(ActivityDetails.this, R.layout.member_item, invitesList);
                invitesAdapter.selectedMembers = new ArrayList<>();
                invitesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                invitesAdapter.notifyDataSetChanged();
                invitesSpinner.setAdapter(invitesAdapter);
                invitesSpinner.setSelection(0);
            } else {
                Log.d("CreateEvent", "Error getting documents: ", task.getException());
            }
        });
    }

    public void updateCategoryAdapter() {
        Category.getCategoryCreatedByMeByType(memberRef, "activity", task -> {
            if (task.isSuccessful()) {
                categoryList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    categoryList.add(document.toObject(Category.class));
                }
//              CategoryAdapter.categories = new ArrayList<>(new HashSet<>(CategoryAdapter.categories));
                categoryAdapter = new CategoryAdapter(ActivityDetails.this, R.layout.category_array_item, categoryList);
                categoryAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
                categorySpinner.setAdapter(categoryAdapter);
                if (mode.equals("add")) {
                    categorySpinner.setSelection(0);
                } else {
                    for (int i = 0; i < categoryList.size(); i++) {
                        if (categoryList.get(i).getReference().equals(activity.getCategory())) {
                            categorySpinner.setSelection(i);
                            break;
                        }
                    }
                }
            } else {
                Log.d("ActivityDetails", "Error getting categories: ", task.getException());
            }
        });
    }

    public void showDatePickerDialog(View v) {
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select a date");
        MaterialDatePicker<Long> materialDatePicker = builder.build();
        materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            Date date = new Date((Long) selection);
            dateString = date.toString();
            dateInput.setText(dateString);
        });
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
            if (activity.getActivityId() == null) {
                activity.setActivityId(randomUUID().toString());
            }
            activity.setName(nameInput.getText().toString());
            try {
                activity.setActivityDate(sd.parse(dateInput.getText().toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // get spinner selected item
            if (categorySpinner.getSelectedItem() != null && categoryAdapter.getItem(categorySpinner.getSelectedItemPosition()) != null) {
                Log.d("ActivityDetails", categorySpinner.getSelectedItem().toString());
                DocumentReference catRef = db.collection(Category.collection).document(categoryAdapter.getItem(categorySpinner.getSelectedItemPosition()).getCategoryId());
                activity.setCategory(catRef);
            }
//            activity.setCategory(categoryList.get(categorySpinner.getSelectedItemPosition()).getReference());
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
            if (invitesAdapter != null && invitesAdapter.selectedMembers != null) {
                activity.setInvites(invitesAdapter.selectedMembers);
            }
            Activity.updateActivity(activity, task -> {
                if (task.isSuccessful()) {
                    Log.d("CreateActivity", "Activity updated");
                    Toast.makeText(ActivityDetails.this, "Activity updated", Toast.LENGTH_SHORT).show();
                    switchMode("view");
                } else {
                    Log.d("CreateActivity", "Error updating activity", task.getException());
                }
            });
        }
    }

}