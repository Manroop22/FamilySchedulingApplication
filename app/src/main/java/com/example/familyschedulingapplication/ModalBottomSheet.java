package com.example.familyschedulingapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

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
        ImageButton calendarBtn = view.findViewById(R.id.calendarBtn);
        ImageButton billsBtn = view.findViewById(R.id.billsBtn);
        ImageButton boardBtn = view.findViewById(R.id.boardBtn);
        ImageButton listsBtn = view.findViewById(R.id.listsBtn);
        ImageButton logoutBtn = view.findViewById(R.id.logoutBtn);
        homeBtn.setOnClickListener(v -> goTo(MainActivity.class));
        boardBtn.setOnClickListener(v -> goTo(MessageBoard.class));
        logoutBtn.setOnClickListener(v -> MainActivity.signOut());

        return view;
    }

    public void goTo(Class<? extends AppCompatActivity> activity) {
        Intent intent = new Intent(getActivity(), activity);
        startActivity(intent);
    }
}