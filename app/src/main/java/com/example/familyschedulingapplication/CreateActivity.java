package com.example.familyschedulingapplication;

import static java.util.UUID.randomUUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

import java.net.HttpCookie;
import java.text.ParseException;
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
    ArrayList<DocumentReference> invites= new ArrayList<>();
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
    SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
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
        saveBtn = findViewById(R.id.saveMsgBtn);
        cancelBtn = findViewById(R.id.cancelMsgBtn);
        smsCheckbox = findViewById(R.id.notifySMS);
        pushCheckbox = findViewById(R.id.notifyPush);
        emailCheckbox = findViewById(R.id.notifyEmail);
        Member.getMember(user.getUid(), task -> {
            member = Member.getMemberByMemberId(task.getResult());
            init();
        });
    }

    public void init() {
        updateCategoryAdapter();
        spinnerAdapter();
        backBtn.setOnClickListener(v -> {
            if (validateInputs(false)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateActivity.this);
                builder.setTitle("Discard Changes?");
                builder.setMessage("Are you sure you want to discard your changes?");
                builder.setPositiveButton("Yes", (dialog, which) -> goBack());
                builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                builder.show();
            } else {
                goBack();
            }
        });
        invitesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Member.getMember(invitesList.get(position).getUserId(), task -> {
                    if (task.isSuccessful()) {
                        invites.add(task.getResult().getReference());
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        newCategoryBtn.setOnClickListener(v -> {
            Log.d("CreateActivity", CategoryAdapter.categories.toString());
            CategoryBottomSheet.newInstance("add", null, "activity").show(getSupportFragmentManager(), "CreateCategory");
        });
        dateInput.setOnClickListener(this::showDatePickerDialog);
        saveBtn.setOnClickListener(v -> saveActivity());
        cancelBtn.setOnClickListener(v -> finish());
    }

    public void goBack() {
        Intent intent = new Intent(CreateActivity.this, ListAndActivityMainScreen.class);
        startActivity(intent);
        finish();
    }

    public void spinnerAdapter() {
        Member.getMembersByHome(member.getHomeId(), (OnCompleteListener<QuerySnapshot>) task -> {
            if (task.isSuccessful()) {
                invitesList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    invitesList.add(document.toObject(Member.class));
                }
                invitesAdapter = new MemberAdapter(CreateActivity.this, R.layout.member_item, invitesList);
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
        db.collection(Category.collection).whereEqualTo("createdBy", memberRef).get().addOnCompleteListener(task -> {
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

    public void saveActivity() {
        if (validateInputs(true)) {
            Activity activity = new Activity();
            activity.setName(nameInput.getText().toString());
            try {
                activity.setActivityDate(sd.parse(dateInput.getText().toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // get spinner selected item
            Log.d("CreateActivity", categorySpinner.getSelectedItem().toString());
            DocumentReference catRef = db.collection(Category.collection).document(categoryAdapter.getItem(categorySpinner.getSelectedItemPosition()).getCategoryId());
            activity.setCategory(catRef);
            activity.setNotes(notesInput.getText().toString());
            activity.setCreatedBy(memberRef);
            activity.setCreatedAt(new Date(System.currentTimeMillis()));
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
            Activity.addActivity(activity, task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(CreateActivity.this, "Activity created successfully", Toast.LENGTH_SHORT).show();
                    goBack();
                } else {
                    Toast.makeText(CreateActivity.this, "Error creating activity", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}