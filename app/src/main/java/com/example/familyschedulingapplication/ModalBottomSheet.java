package com.example.familyschedulingapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     ModalBottomSheet.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class ModalBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "ModalBottomSheet";
    // TODO: Customize parameter argument names
    private static final String ARG_ITEM_COUNT = "item_count";

    // TODO: Customize parameters
    public static ModalBottomSheet newInstance(int itemCount) {
        final ModalBottomSheet fragment = new ModalBottomSheet();
        final Bundle args = new Bundle();
        args.putInt(ARG_ITEM_COUNT, itemCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_modal_bottom_sheet_dialog, container, false);
        ImageButton homeBtn = view.findViewById(R.id.homeBtn);
        TextView homeBtnText = view.findViewById(R.id.homeBtnText);
        ImageButton calendarBtn = view.findViewById(R.id.calendarBtn);
        TextView calendarBtnText = view.findViewById(R.id.calendarBtnText);
        ImageButton billsBtn = view.findViewById(R.id.billsBtn);
        TextView billsBtnText = view.findViewById(R.id.billsBtnText);
        ImageButton boardBtn = view.findViewById(R.id.boardBtn);
        TextView boardBtnText = view.findViewById(R.id.boardBtnText);
        ImageButton listsBtn = view.findViewById(R.id.listsBtn);
        TextView listsBtnText = view.findViewById(R.id.listsBtnText);
        ImageButton logoutBtn = view.findViewById(R.id.logoutBtn);
        TextView logoutBtnText = view.findViewById(R.id.logoutBtnText);
        homeBtn.setOnClickListener(v -> goTo(MainActivity.class));
        homeBtnText.setOnClickListener(v -> goTo(MainActivity.class));
        logoutBtn.setOnClickListener(v -> MainActivity.signOut());
        logoutBtnText.setOnClickListener(v -> MainActivity.signOut());
        calendarBtn.setOnClickListener(v -> goTo(EventMainScreen.class));
        calendarBtnText.setOnClickListener(v -> goTo(EventMainScreen.class));
        return view;
    }

    public void goTo(Class<? extends AppCompatActivity> activity) {
        Intent intent = new Intent(getActivity(), activity);
        startActivity(intent);
    }
}