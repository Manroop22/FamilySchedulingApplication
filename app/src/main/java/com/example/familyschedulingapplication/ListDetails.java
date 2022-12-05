package com.example.familyschedulingapplication;

import static java.util.UUID.randomUUID;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.familyschedulingapplication.Adapters.CategoryAdapter;
import com.example.familyschedulingapplication.Adapters.ListItemAdapter;
import com.example.familyschedulingapplication.ModalBottomSheets.CategoryBottomSheet;
import com.example.familyschedulingapplication.Models.Category;
import com.example.familyschedulingapplication.Models.List;
import com.example.familyschedulingapplication.Models.ListItem;
import com.example.familyschedulingapplication.Models.Member;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class ListDetails extends AppCompatActivity {
    static String mode = "view";
    TextView listMode;
    ImageButton backBtn, editBtn, deleteBtn, addListItemBtn, addCategoryBtn;
    Button saveBtn, cancelBtn;
    EditText nameInput, notesInput;
    Spinner categorySpinner;
    ArrayList<ListItem> listItems;
    ArrayList<Category> categoryList;
    RecyclerView listItemsRecyclerView;
    DocumentReference listRef, memberRef;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user;
    Member member;
    List list;
    String listId;
    ListItemAdapter listItemAdapter;
    ScrollView scrollView;
    CategoryAdapter categoryAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        mode = getIntent().getStringExtra("mode");
        if (mode == null) {
            mode = "view";
        }
        user = FirebaseAuth.getInstance().getCurrentUser();
        listMode = findViewById(R.id.billMode);
        assert user != null;
//        member = Member.getMemberByUserId(user.getUid());
        memberRef = db.collection("members").document(user.getUid());
        listId = getIntent().getStringExtra("listId");
        backBtn = findViewById(R.id.exitBillBtn);
        editBtn = findViewById(R.id.editList);
        deleteBtn = findViewById(R.id.deleteList);
        addListItemBtn = findViewById(R.id.addListItem);
        addCategoryBtn = findViewById(R.id.newCategoryButton);
        saveBtn = findViewById(R.id.saveMsgBtn);
        cancelBtn = findViewById(R.id.cancelMsgBtn);
        nameInput = findViewById(R.id.billNameInput);
        notesInput = findViewById(R.id.noteInput);
        categorySpinner = findViewById(R.id.categorySpinner);
        listItemsRecyclerView = findViewById(R.id.listItemsRecyclerView);
        scrollView = findViewById(R.id.itemScroller);
        if (listId == null) {
            listItems = new ArrayList<>();
            mode = "add";
            init();
        } else {
            List.getListByListId(listId, task -> {
                list = task.getResult().toObject(List.class);
                init();
            });
        }
    }

    public void init() {
        switchMode(mode);
        updateCategoryAdapter();
        saveBtn.setOnClickListener(v -> saveList());
        cancelBtn.setOnClickListener(v -> switchMode("view"));
        editBtn.setOnClickListener(v -> switchMode("edit"));
        deleteBtn.setOnClickListener(v -> {
            List.deleteList(list, task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "List deleted", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Error deleting list", Toast.LENGTH_SHORT).show();
                }
            });
        });
        addListItemBtn.setOnClickListener(v -> {
            ListItem listItem = new ListItem("New Item", false);
            listItems.add(listItem);
//            listItemAdapter.notifyDataSetChanged();
            listItemAdapter = new ListItemAdapter(listItems);
//            listItemAdapter.notifyItemInserted(listItems.size() - 1);
            listItemsRecyclerView.setAdapter(listItemAdapter);
            listItemsRecyclerView.setLayoutManager(new LinearLayoutManager(ListDetails.this));
        });
        backBtn.setOnClickListener(v -> {
            if (!mode.equals("view")) {
                if (validateFields(false)) {
                    // dialog if values have changed
                    AlertDialog.Builder builder = new AlertDialog.Builder(ListDetails.this);
                    builder.setTitle("Are you sure you want to exit?");
                    builder.setPositiveButton("Yes", (dialog, which) -> finish());
                    builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                    builder.show();
                } else {
                    finish();
                }
            } else {
                finish();
            }
        });
        addCategoryBtn.setOnClickListener(v -> CategoryBottomSheet.newInstance("add", null, "list").show(getSupportFragmentManager(), CategoryBottomSheet.TAG));
    }

    public void switchMode(String mode) {
        String modeUpper = mode.substring(0, 1).toUpperCase() + mode.substring(1);
        listMode.setText(String.format("%s List", modeUpper));
        switch (mode) {
            case "view":
                nameInput.setEnabled(false);
                notesInput.setEnabled(false);
                categorySpinner.setEnabled(false);
                addListItemBtn.setEnabled(false);
                addListItemBtn.setVisibility(ImageButton.GONE);
                addCategoryBtn.setEnabled(false);
                addCategoryBtn.setVisibility(ImageButton.GONE);
                saveBtn.setEnabled(false);
                cancelBtn.setEnabled(false);
                editBtn.setVisibility(ImageButton.VISIBLE);
                deleteBtn.setVisibility(ImageButton.VISIBLE);
                // set also for the items in the listItemAdapter
                for (int i = 0; i < listItemsRecyclerView.getChildCount(); i++) {
                    View view = listItemsRecyclerView.getChildAt(i);
                    RecyclerView.ViewHolder rvvh = listItemsRecyclerView.getChildViewHolder(view);
                    if (rvvh instanceof ListItemAdapter.ViewHolder) {
                        ((ListItemAdapter.ViewHolder) rvvh).switchMode("view");
                    }
                }
                break;
            case "edit":
                nameInput.setEnabled(true);
                notesInput.setEnabled(true);
                categorySpinner.setEnabled(true);
                addListItemBtn.setEnabled(true);
                addListItemBtn.setVisibility(ImageButton.VISIBLE);
                addCategoryBtn.setEnabled(true);
                addCategoryBtn.setVisibility(ImageButton.VISIBLE);
                saveBtn.setEnabled(true);
                cancelBtn.setEnabled(true);
                editBtn.setVisibility(ImageButton.GONE);
                deleteBtn.setVisibility(ImageButton.VISIBLE);
                for (int i = 0; i < listItemsRecyclerView.getChildCount(); i++) {
                    View view = listItemsRecyclerView.getChildAt(i);
                    RecyclerView.ViewHolder rvvh = listItemsRecyclerView.getChildViewHolder(view);
                    if (rvvh instanceof ListItemAdapter.ViewHolder) {
                        ((ListItemAdapter.ViewHolder) rvvh).switchMode("edit");
                    }
                }
                break;
            case "add":
            case "create":
            case "new":
                nameInput.setEnabled(true);
                notesInput.setEnabled(true);
                categorySpinner.setEnabled(true);
                addListItemBtn.setEnabled(true);
                addListItemBtn.setVisibility(ImageButton.VISIBLE);
                addCategoryBtn.setEnabled(true);
                addCategoryBtn.setVisibility(ImageButton.VISIBLE);
                saveBtn.setEnabled(true);
                cancelBtn.setEnabled(true);
                editBtn.setVisibility(ImageButton.GONE);
                deleteBtn.setVisibility(ImageButton.GONE);
                break;
        }
        setValues(mode);
    }

    public void setValues(String nMode) {
        // mode = create or add, view, edit
        switch (nMode) {
            case "create":
            case "add":
            case "new":
                nameInput.setText("");
                notesInput.setText("");
                categorySpinner.setSelection(0);
                listItems.clear();
                break;
            case "view":
            case "edit":
                // set all fields to values from db
                nameInput.setText(list.getName());
                notesInput.setText(list.getNotes());
                // set spinner selection to where spinnerPosition value matches category
                // get spinner values, get position of value that matches category
                // set spinner position to that value
                Category.getCategoryByReference(list.getCategory(), task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Category cat = document.toObject(Category.class);
//                    categorySpinner.setSelection(categoryAdapter.getPosition(cat));
                            if (cat.getCategoryId() != null) {
                                if (categoryAdapter.getPosition(cat) != -1) {
                                    categorySpinner.setSelection(categoryAdapter.getPosition(cat));
                                } else {
                                    categorySpinner.setSelection(0);
                                }
                            }
                            Log.d("ActivityDetails", "DocumentSnapshot data: " + document.getData());
                        } else {
                            Log.d("ActivityDetails", "No such document");
                        }
                    } else {
                        Log.d("ActivityDetails", "get failed with ", task.getException());
                    }
                });
                if (listItems == null) {
                    listItems = new ArrayList<>();
                } else {
                    listItems.clear();
                    if (list.getListItems() != null) {
                        listItems.addAll(list.getListItems());
                    }
                }
                break;
        }
        updateListItems();
    }

    public void updateListItems() {
        if (listId == null) {
            listItems.clear();
            listItemAdapter = new ListItemAdapter(listItems);
            listItemsRecyclerView.setAdapter(listItemAdapter);
            listItemsRecyclerView.setLayoutManager(new LinearLayoutManager(ListDetails.this));
            Objects.requireNonNull(listItemsRecyclerView.getLayoutManager()).onRestoreInstanceState(listItemsRecyclerView.getLayoutManager().onSaveInstanceState());
            return;
        }
        db.collection(List.collection).document(listId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                listItems = new ArrayList<>();
                if (document.exists()) {
                    list = List.getTaskByReference(document);
                    switch(mode) {
                        case "view":
                        case "edit":
                            listItems.clear();
                            if (list.getListItems() != null) {
                                ArrayList<ListItem> mitems = list.getListItems();
                                for(ListItem item : mitems) {
                                    Log.d(mitems.getClass().getSimpleName(), item.toString());
                                    listItems.add(new ListItem(item.getName(), item.getCompleted()));
                                }
                            }
                            break;
                    }
                    listItemAdapter = new ListItemAdapter(listItems);
                    listItemsRecyclerView.setAdapter(listItemAdapter);
                    listItemsRecyclerView.setLayoutManager(new LinearLayoutManager(ListDetails.this));
                    Objects.requireNonNull(listItemsRecyclerView.getLayoutManager()).onRestoreInstanceState(listItemsRecyclerView.getLayoutManager().onSaveInstanceState());
                } else {
                    Log.d("ListDetails", "No such document");
                }
            }
        });
    }

    public void updateCategoryAdapter() {
        Category.getCategoryCreatedByMeByType(memberRef, "list", task -> {
            if (task.isSuccessful()) {
                categoryList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    categoryList.add(document.toObject(Category.class));
                }
//              CategoryAdapter.categories = new ArrayList<>(new HashSet<>(CategoryAdapter.categories));
                categoryAdapter = new CategoryAdapter(ListDetails.this, R.layout.category_array_item, categoryList);
                categoryAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
                categorySpinner.setAdapter(categoryAdapter);
                if (mode.equals("add")) {
                    categorySpinner.setSelection(0);
                } else {
                    for (int i = 0; i < categoryList.size(); i++) {
                        if (categoryList.get(i).getReference().equals(list.getCategory())) {
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

    public boolean validateFields(Boolean showErrors) {
        boolean res = true;
        switch (mode) {
            case "create":
            case "add":
                // validate name, date, notes, category, and list items
                if (nameInput.getText().toString().isEmpty()) {
                    if (showErrors) {
                        nameInput.setError("Name is required");
                    }
                    res = false;
                }
                if (listItems.size() == 0) {
                    if (showErrors) {
                        Toast.makeText(this, "Please add some items!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case "view":
                // do nothing
                break;
            case "edit":
                // validate name, date, notes, category, and list items
                if (nameInput.getText().toString().isEmpty()) {
                    if (showErrors) {
                        nameInput.setError("Name is required");
                    }
                    res = false;
                }
                break;
        }
        return res;
    }

    public void saveList() {
        if (validateFields(true)) {
            // check if categoryAdapter stuff is null, if not, set category to categoryAdapter.getCategory()
            if (mode.equals("add") || mode.equals("create") || mode.equals("new")) {
                list = new List();
                Log.d("ListDetails", categorySpinner.getSelectedItem().toString());
//                DocumentReference catRef = db.collection(Category.collection).document(categoryAdapter.getItem(categorySpinner.getSelectedItemPosition()).getCategoryId());
//                Log.d("ListDetails", catRef.toString());
//                Log.d("ListDetails", categoryAdapter.getItem(categorySpinner.getSelectedItemPosition()).getReference().toString());
                list.setCategory(categoryAdapter.getItem(categorySpinner.getSelectedItemPosition()).getReference());
            } else {
                if (categorySpinner.getSelectedItem() != null && categoryAdapter.getItem(categorySpinner.getSelectedItemPosition()) != null) {
                    Log.d("ActivityDetails", categorySpinner.getSelectedItem().toString());
                    DocumentReference catRef = db.collection(Category.collection).document(categoryAdapter.getItem(categorySpinner.getSelectedItemPosition()).getCategoryId());
                    list.setCategory(catRef);
                }
            }
            list.setName(nameInput.getText().toString());
            list.setNotes(notesInput.getText().toString());
            listItems = listItemAdapter.getListItems();
            list.setListItems(listItems);
            switch(mode) {
                case "create":
                case "new":
                case "add":
                    list.setCreatedAt(new Date());
                    list.setCreatedBy(memberRef);
                    list.setTaskId(randomUUID().toString());
                    break;
                case "edit":
                    list.setUpdatedAt(new Date());
                    break;
            }
            List.updateList(list, task -> {
                if (task.isSuccessful()) {
                    Log.d("ListDetails", "List saved successfully");
                    Toast.makeText(this, "List saved successfully", Toast.LENGTH_SHORT).show();
                    switchMode("view");
                    finish();
                } else {
                    Log.d("ListDetails", "List save failed");
                    Toast.makeText(this, "List save failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}