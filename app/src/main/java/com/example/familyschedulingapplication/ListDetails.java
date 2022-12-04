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
import com.example.familyschedulingapplication.Models.List;
import com.example.familyschedulingapplication.Models.ListItem;
import com.example.familyschedulingapplication.Models.Member;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
        member = Member.getMemberByUserId(user.getUid());
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
        saveBtn.setOnClickListener(v -> saveList());
        cancelBtn.setOnClickListener(v -> switchMode("view"));
        editBtn.setOnClickListener(v -> switchMode("edit"));
        deleteBtn.setOnClickListener(v -> {
            List.deleteList(list);
            finish();
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
            // dialog if values have changed
            AlertDialog.Builder builder = new AlertDialog.Builder(ListDetails.this);
            builder.setTitle("Are you sure you want to exit?");
            builder.setPositiveButton("Yes", (dialog, which) -> finish());
            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            builder.show();
        });
        addCategoryBtn.setOnClickListener(v -> CategoryBottomSheet.newInstance("add", null).show(getSupportFragmentManager(), CategoryBottomSheet.TAG));
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
                if (list.getCategory() != null) {
//                    categorySpinner.setSelection(categoryAdapter.getPosition());
                    categorySpinner.setSelection(CategoryAdapter.categories.indexOf(list.getCategory()));
                }
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
        db.collection("tasks").document(listId).get().addOnCompleteListener(task -> {
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
            if (categorySpinner.getSelectedItem() != null) {
                list.setCategory(categoryAdapter.getItem(categorySpinner.getSelectedItemPosition()).getReference());
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
            db.collection("tasks").document(listId).set(list).addOnSuccessListener(aVoid -> {
                Log.d("ListDetails", "DocumentSnapshot successfully written!");
                Toast.makeText(ListDetails.this, "List saved!", Toast.LENGTH_SHORT).show();
                list = List.getTaskByReference(db.collection("tasks").document(listId).get().getResult());
                switchMode("view");
            }).addOnFailureListener(e -> {
                Log.w("ListDetails", "Error writing document", e);
                Toast.makeText(ListDetails.this, "Error saving list!", Toast.LENGTH_SHORT).show();
            });
        }
    }
}