package com.example.familyschedulingapplication.ModalBottomSheets;

import static java.util.UUID.randomUUID;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.familyschedulingapplication.Adapters.CategoryAdapter;
import com.example.familyschedulingapplication.Models.Category;
import com.example.familyschedulingapplication.Models.Member;
import com.example.familyschedulingapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorListener;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class CategoryBottomSheet extends BottomSheetDialogFragment {
    public static final String TAG = "CategoryBottomSheet";
    private static final String ARG_ITEM_COUNT = "item_count";
    private static final String MODE = "view";
    private Category category;
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
    public String cattype;

    public static CategoryBottomSheet newInstance(String mode, Category category, String type) {
        final CategoryBottomSheet fragment = new CategoryBottomSheet();
        final Bundle args = new Bundle();
//        args.putInt(ARG_ITEM_COUNT, itemCount);
        if (mode != null) {
            args.putString(MODE, mode);
        }
        if (category != null) {
            args.putSerializable("category", (Serializable) category);
        }
        if (type != null) {
            args.putString("cattype", type);
        }
        fragment.setArguments(args);
//        adapter = new CategoryAdapter(fragment.getContext(), R.layout.category_array_item, CategoryAdapter.categories);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // set draggable to false, set peekable to true
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
//        bottomSheetBehavior.setDraggable(true);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setPeekHeight(200);
        bottomSheetBehavior.setFitToContents(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        EditText categoryName = view.findViewById(R.id.categoryNameModal);
        categoryName.setEnabled(false);
        TextView categoryMode = view.findViewById(R.id.categoryMode);
        String firstUpper = getArguments().getString(MODE).substring(0, 1).toUpperCase() + getArguments().getString(MODE).substring(1);
        categoryMode.setText(String.format("%s Category", firstUpper));
        ColorPickerView colorPickerView = view.findViewById(R.id.categoryColor);
        Button saveBtn = view.findViewById(R.id.saveCategoryBtn);
        Button cancelBtn = view.findViewById(R.id.cancelCategoryBtn);
        ImageButton editBtn = view.findViewById(R.id.editCategoryBtn);
        ImageButton deleteBtn = view.findViewById(R.id.deleteCategoryBtn);
        EditText colorInput = view.findViewById(R.id.colorInput);
        assert getArguments() != null;
        switchMode(getArguments().getString(MODE), view);
        colorPickerView.setColorListener((ColorListener) (color, fromUser) -> {
            colorInput.setText(String.valueOf(color));
            colorInput.setBackgroundColor(color);
        });
        deleteBtn.setOnClickListener(v -> {
            Category.deleteCategory(category, task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Category deleted successfully");
                    dismiss();
                } else {
                    Log.d(TAG, "Category delete failed");
                }
            });
        });
        editBtn.setOnClickListener(v -> switchMode("edit", view));
        saveBtn.setOnClickListener(v -> saveValues(view));
        cancelBtn.setOnClickListener(v -> switchMode("view", view));
    }

    public void switchMode(String mode, View view) {
        EditText categoryName = view.findViewById(R.id.categoryNameModal);
        ColorPickerView colorPickerView = view.findViewById(R.id.categoryColor);
        Button saveBtn = view.findViewById(R.id.saveCategoryBtn);
        Button cancelBtn = view.findViewById(R.id.cancelCategoryBtn);
        ImageButton editBtn = view.findViewById(R.id.editCategoryBtn);
        ImageButton deleteBtn = view.findViewById(R.id.deleteCategoryBtn);
        EditText colorInput = view.findViewById(R.id.colorInput);
        if (mode.equals("view")) {
            categoryName.setEnabled(false);
            colorPickerView.setEnabled(false);
            // set colorPickerView height to 0
            colorPickerView.getLayoutParams().height = 0;
            saveBtn.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.GONE);
            editBtn.setVisibility(View.VISIBLE);
            deleteBtn.setVisibility(View.VISIBLE);
//            colorInput.setVisibility(View.GONE);
        } else if (mode.equals("edit") || mode.equals("create") || mode.equals("add")) {
            categoryName.setEnabled(true);
            colorPickerView.setEnabled(true);
            // set colorPickerView height to 0
            colorPickerView.getLayoutParams().height = getResources().getDimensionPixelSize(R.dimen.modalColorPickerHeight);
            saveBtn.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.VISIBLE);
            editBtn.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.GONE);
            colorInput.setVisibility(View.VISIBLE);
        }
    }

    public void saveValues(View view) {
        EditText categoryName = view.findViewById(R.id.categoryNameModal);
        EditText colorInput = view.findViewById(R.id.colorInput);
        if (category == null) {
            category = new Category();
        }
        category.setName(categoryName.getText().toString());
        category.setColor(Integer.parseInt(colorInput.getText().toString()));
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        DocumentReference memberRef = db.collection("members").document(user.getUid());
//        Member member = Member.getMemberByMemberId(memberRef);
        assert getArguments() != null;
        if (getArguments().getString(MODE).equals("add") || getArguments().getString(MODE).equals("create")) {
            // if activity is CreateActivity, set createdForType to "activity"
            try {
                category.setCreatedAt(sd.parse(new Date().toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            category.setCreatedBy(memberRef);
            category.setCreatedAt(new Date(System.currentTimeMillis()));
            category.setCreatedForType(getArguments().getString("cattype"));
            category.setCategoryId(randomUUID().toString());
            Category.addCategory(category, task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Category added successfully");
                    CategoryAdapter.categories.add(category);
                    dismiss();
                } else {
                    Log.d(TAG, "Category add failed");
                }
            });
        } else if (getArguments().getString(MODE).equals("edit")) {
            try {
                category.setUpdatedAt(sd.parse(new Date().toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Category.updateCategory(category, (OnCompleteListener<Void>) task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Category successfully updated!");
                } else {
                    Log.w(TAG, "Error updating category", task.getException());
                }
            });
        }
        dismiss();
    }
}
