package com.example.familyschedulingapplication.ModalBottomSheets;

import static java.util.UUID.randomUUID;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.familyschedulingapplication.Models.Category;
import com.example.familyschedulingapplication.Models.HomeInvite;
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

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class ShareBottomSheet extends BottomSheetDialogFragment {
	public static final String TAG = "ShareBottomSheet";
	public FirebaseFirestore db = FirebaseFirestore.getInstance();
	SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
	DocumentReference homeRef;
	String accessCode;

	public static ShareBottomSheet newInstance(DocumentReference homeId, String accessCode) {
		final ShareBottomSheet fragment = new ShareBottomSheet();
		final Bundle args = new Bundle();
		fragment.setArguments(args);
		fragment.homeRef = homeId;
		fragment.accessCode = accessCode;
		return fragment;
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_share_bottom_sheet, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
//		bottomSheetBehavior.setDraggable(true);
		bottomSheetBehavior.setHideable(true);
//		bottomSheetBehavior.setPeekHeight(200);
		bottomSheetBehavior.setFitToContents(true);
		bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//		super.onViewCreated(view, savedInstanceState);
		EditText emailInvite = view.findViewById(R.id.emailInvite);
		ImageButton sendInvite = view.findViewById(R.id.sendInvite);
		sendInvite.setOnClickListener(v -> {
			if (emailInvite.getText().toString().isEmpty()) {
				emailInvite.setError("Please enter an email address");
			} else {
				FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
				HomeInvite homeInvite = new HomeInvite();
				homeInvite.setHomeInviteId(randomUUID().toString());
				homeInvite.setInvitedMemberEmail(emailInvite.getText().toString());
				assert user != null;
				homeInvite.setInvitedBy(db.collection(Member.collection).document(user.getUid()));
				homeInvite.setInvitedByEmail(user.getEmail());
				homeInvite.setHomeId(homeRef);
				homeInvite.setAccessCode(accessCode);
				homeInvite.setCreatedAt(new Date(System.currentTimeMillis()));
				homeInvite.setAccepted(false);
				HomeInvite.createHomeInvite(homeInvite, task -> {
					if (task.isSuccessful()) {
						// send an email to the email address
						String subject = "Home Management Application: You have been invited to join a home";
						String body = "You have been invited to join a home by " + user.getEmail() + ". Please use the following access code to join the home: " + accessCode;
						String emailTo = emailInvite.getText().toString();
						Intent intent = new Intent(Intent.ACTION_SEND);
						intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailTo});
						intent.putExtra(Intent.EXTRA_SUBJECT, subject);
						intent.putExtra(Intent.EXTRA_TEXT, body);
						intent.setType("message/rfc822");
						startActivity(Intent.createChooser(intent, "Choose an email client"));
						dismiss();
					} else {
						Toast.makeText(getContext(), "Error sending invite", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
}