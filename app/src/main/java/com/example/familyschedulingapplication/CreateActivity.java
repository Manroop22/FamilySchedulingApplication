package com.example.familyschedulingapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
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
import com.example.familyschedulingapplication.Models.Activity;
import com.example.familyschedulingapplication.Models.Category;
import com.example.familyschedulingapplication.Models.Member;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
        memberRef = member.getReference();
        nameInput = findViewById(R.id.nameInput);
        dateInput = findViewById(R.id.dateInput);
        categorySpinner = findViewById(R.id.categorySpinner);
        notesInput = findViewById(R.id.notesInput);
        invitesSpinner = findViewById(R.id.inviteSpinner);
        backBtn = findViewById(R.id.backBtnNewActivity);
        newCategoryBtn = findViewById(R.id.newCategoryButton);
        saveBtn = findViewById(R.id.saveBtnNewActivity);
        cancelBtn = findViewById(R.id.cancelBtnNewActivity);
        smsCheckbox = findViewById(R.id.smsCheckBox);
        pushCheckbox = findViewById(R.id.pushCheckBox);
        emailCheckbox = findViewById(R.id.emailCheckBox);
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
        newCategoryBtn.setOnClickListener(v -> {
            CategoryBottomSheet categoryBottomSheet = new CategoryBottomSheet();
            categoryBottomSheet.show(getSupportFragmentManager(), "CategoryBottomSheet");
        });
        dateInput.setOnClickListener(this::showDatePickerDialog);
        saveBtn.setOnClickListener(v -> saveActivity());
        cancelBtn.setOnClickListener(v -> finish());
//        updateCategoryAdapter();
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
        db.collection("categories").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                categoryList = new ArrayList<>();
                // define urgent color, important color, and normal color
                Color urgentColor = Color.valueOf(Color.RED);
                Color importantColor = Color.valueOf(Color.YELLOW);
                Color normalColor = Color.valueOf(Color.GREEN);
                categoryList.add(new Category("Urgent", urgentColor));
                categoryList.add(new Category("Family", importantColor));
                categoryList.add(new Category("Casual", normalColor));
                if (task.getResult() != null) {
                    for (DocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            Category category = Category.getCategory(document);
                            if (category != null) {
                                if (category.getCreatedBy().equals(memberRef)) {
                                    if (category.getCreatedForType().contains("activity") || category.getCreatedForType().contains("all") || category.getCreatedForType().contains("both")) {
                                        categoryList.add(category);
                                    }
                                }
                            }
                        }
                    }
                }
                categoryAdapter = new CategoryAdapter(this, categoryList);
                categoryAdapter.setDropDownViewResource(R.layout.category_array_item);
                categorySpinner.setAdapter(categoryAdapter);
            }
        });
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
        datePickerDialog = new DatePickerDialog(this, (DatePickerDialog.OnDateSetListener) (view, year, month, dayOfMonth) -> {
            month = month + 1;
            dateString = dayOfMonth + "/" + month + "/" + year;
            dateInput.setText(dateString);
        }, year, month, day);
        datePickerDialog.show();
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
        }
    }
}