package com.example.familyschedulingapplication.ModalBottomSheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.familyschedulingapplication.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorListener;

public class CategoryBottomSheet extends BottomSheetDialogFragment {
    public static final String TAG = "CategoryBottomSheet";
    private static final String ARG_ITEM_COUNT = "item_count";

    public static CategoryBottomSheet newInstance(int itemCount) {
        final CategoryBottomSheet fragment = new CategoryBottomSheet();
        final Bundle args = new Bundle();
        args.putInt(ARG_ITEM_COUNT, itemCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_bottom_sheet, container, false);
        EditText categoryName = view.findViewById(R.id.categoryNameModal);
        categoryName.setEnabled(false);
        ColorPickerView colorPickerView = view.findViewById(R.id.categoryColor);
        colorPickerView.setColorListener((ColorListener) (color, fromUser) -> {
            // do something here
        });
        return view;
    }
}
