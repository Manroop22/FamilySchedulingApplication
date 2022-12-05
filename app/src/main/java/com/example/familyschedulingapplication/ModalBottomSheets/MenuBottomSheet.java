package com.example.familyschedulingapplication.ModalBottomSheets;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.example.familyschedulingapplication.BillMainActivity;
import com.example.familyschedulingapplication.EventMainScreen;
import com.example.familyschedulingapplication.ListAndActivityMainScreen;
import com.example.familyschedulingapplication.MainActivity;
import com.example.familyschedulingapplication.MessageBoard;
import com.example.familyschedulingapplication.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;

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
public class MenuBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "MenuBottomSheet";
    // TODO: Customize parameter argument names
    private static final String ARG_ITEM_COUNT = "item_count";

    // TODO: Customize parameters
    public static MenuBottomSheet newInstance(int itemCount) {
        final MenuBottomSheet fragment = new MenuBottomSheet();
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
        boardBtn.setOnClickListener(v -> goTo(MessageBoard.class));
        boardBtnText.setOnClickListener(v -> goTo(MessageBoard.class));
        logoutBtn.setOnClickListener(v -> signOut());
        logoutBtnText.setOnClickListener(v -> signOut());
        calendarBtn.setOnClickListener(v -> goTo(EventMainScreen.class));
        calendarBtnText.setOnClickListener(v -> goTo(EventMainScreen.class));
        listsBtn.setOnClickListener(v -> goTo(ListAndActivityMainScreen.class));
        listsBtnText.setOnClickListener(v -> goTo(ListAndActivityMainScreen.class));
        billsBtn.setOnClickListener(v->goTo(BillMainActivity.class));
        billsBtnText.setOnClickListener(v->goTo(BillMainActivity.class));
        return view;
    }
    public void goTo(Class<? extends AppCompatActivity> activity) {
        Intent intent = new Intent(getActivity(), activity);
        startActivity(intent);
    }

    public void signOut() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }
}