package com.example.familyschedulingapplication.ModalBottomSheets;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.familyschedulingapplication.LoginActivity;
import com.example.familyschedulingapplication.Models.Home;
import com.example.familyschedulingapplication.Models.Member;
import com.example.familyschedulingapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ProfileBottomSheet extends BottomSheetDialogFragment {
	public static final String TAG = "ShareBottomSheet";
	public FirebaseFirestore db = FirebaseFirestore.getInstance();
	Home homeRef;
	DocumentReference memRef;
	FirebaseUser user;
	Member member;
	String profileMode = "view", homeMode = "view";
	Button editBtn, saveBtn, cancelBtn, logoutBtn;
	EditText name, email, phone, password, homeName, homeAddr, homeDesc;
	LinearLayout passwordLayout, homeInfoLayout;

	public static ProfileBottomSheet newInstance(Home homeId, Member member) {
		final ProfileBottomSheet fragment = new ProfileBottomSheet();
		final Bundle args = new Bundle();
		fragment.setArguments(args);
		fragment.homeRef = homeId;
		fragment.member = member;
		return fragment;
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_profile_bottom_sheet, container, false);
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
		user = FirebaseAuth.getInstance().getCurrentUser();
		assert user != null;
		memRef= db.collection(Member.collection).document(user.getUid());
		saveBtn = view.findViewById(R.id.saveProfile);
		cancelBtn = view.findViewById(R.id.cancelProfile);
		editBtn = view.findViewById(R.id.editProfile);
		logoutBtn = view.findViewById(R.id.profileLogout);
		name = view.findViewById(R.id.profileName);
		email = view.findViewById(R.id.profileEmail);
		phone = view.findViewById(R.id.profilePhone);
		password = view.findViewById(R.id.profilePass);
		passwordLayout = view.findViewById(R.id.profilePasswordLayout);
		homeName = view.findViewById(R.id.homeName);
		homeAddr = view.findViewById(R.id.homeAddress);
		homeDesc = view.findViewById(R.id.homeDescription);
		homeInfoLayout = view.findViewById(R.id.homeInfoLayout);
		initProfile();
	}

	void initProfile() {
		switchProfileMode(profileMode, homeMode);
		if (homeRef.getCreatedBy().equals(memRef)) {
			homeInfoLayout.setVisibility(View.VISIBLE);
		} else {
			homeInfoLayout.setVisibility(View.GONE);
		}
		editBtn.setOnClickListener(v -> switchProfileMode("edit", homeMode));
		saveBtn.setOnClickListener(v -> saveProfile());
		cancelBtn.setOnClickListener(v -> {
			if (profileMode.equals("edit")) {
				switchProfileMode("view", homeMode);
			} else {
				dismiss();
			}
		});
		logoutBtn.setOnClickListener(v -> {
			FirebaseAuth.getInstance().signOut();
			dismiss();
			logout();
		});
	}

	void logout() {
		Intent intent = new Intent(getContext(), LoginActivity.class);
		startActivity(intent);
		getActivity().finish();
	}

	void saveProfile() {
		// update FirebaseUser then update Member
		// if name is changed, update FirebaseUser
		if (validateFields()) {
			if (!name.getText().toString().equals("")) {
				UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
						.setDisplayName(name.getText().toString())
						.build();
				user.updateProfile(profileUpdates)
						.addOnCompleteListener(task -> {
							if (task.isSuccessful()) {
								Toast.makeText(getContext(), "Profile updated.", Toast.LENGTH_SHORT).show();
							}
						});
			}
			if (!email.getText().toString().equals("")) {
				user.updateEmail(email.getText().toString())
						.addOnCompleteListener(task -> {
							if (task.isSuccessful()) {
								Toast.makeText(getContext(), "Email updated.", Toast.LENGTH_SHORT).show();
							}
						});
			}
			if (!phone.getText().toString().equals("")) {
				member.setPhone(phone.getText().toString());
			}
			// update member
			member.setName(name.getText().toString());
			member.setEmail(email.getText().toString());
			Member.updateMember(member, task -> {
				if (task.isSuccessful()) {
					Toast.makeText(getContext(), "Profile updated.", Toast.LENGTH_SHORT).show();
					// re authenticate user
					AuthCredential credential = EmailAuthProvider.getCredential(member.getEmail(), password.getText().toString());
					user.reauthenticate(credential)
							.addOnCompleteListener(task1 -> {
								if (task1.isSuccessful()) {
									Toast.makeText(getContext(), "Re-authenticated.", Toast.LENGTH_SHORT).show();
									switchProfileMode("view", homeMode);
									logout();
								}
							});
				} else {
					Toast.makeText(getContext(), "Profile update failed.", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	boolean validateFields() {
		if (name.getText().toString().isEmpty()) {
			name.setError("Name is required");
			Toast.makeText(getContext(), "Name is required", Toast.LENGTH_SHORT).show();
			return false;
		} else {
			name.setError(null);
		}
		if (email.getText().toString().isEmpty()) {
			email.setError("Email is required");
			Toast.makeText(getContext(), "Email is required", Toast.LENGTH_SHORT).show();
			return false;
		} else {
			email.setError(null);
		}
		if (phone.getText().toString().isEmpty()) {
			phone.setError("Phone is required");
			Toast.makeText(getContext(), "Phone is required", Toast.LENGTH_SHORT).show();
			return false;
		} else {
			phone.setError(null);
		}
		if (password.getText().toString().isEmpty()) {
			password.setError("Password is required");
			Toast.makeText(getContext(), "Password is required", Toast.LENGTH_SHORT).show();
			return false;
		} else {
			password.setError(null);
		}
		return true;
	}

	void switchProfileMode(String profmode, String homode) {
		profileMode = profmode;
		setValues();
		switch(profileMode) {
			case "view":
				name.setEnabled(false);
				email.setEnabled(false);
				phone.setEnabled(false);
				password.setEnabled(false);
				password.setVisibility(View.GONE);
				passwordLayout.setVisibility(View.GONE);
				saveBtn.setVisibility(View.GONE);
				cancelBtn.setVisibility(View.GONE);
				editBtn.setVisibility(View.VISIBLE);
				break;
			case "edit":
				name.setEnabled(true);
				email.setEnabled(true);
				phone.setEnabled(true);
				password.setEnabled(true);
				password.setVisibility(View.VISIBLE);
				passwordLayout.setVisibility(View.VISIBLE);
				saveBtn.setVisibility(View.VISIBLE);
				cancelBtn.setVisibility(View.VISIBLE);
				editBtn.setVisibility(View.GONE);
				break;
		}
		switch(homode) {
			case "view":
				homeName.setEnabled(false);
				homeAddr.setEnabled(false);
				homeDesc.setEnabled(false);
//				homeEditBtn.setImageResource(R.drawable.ic_baseline_edit_24);
				break;
			case "edit":
				homeName.setEnabled(true);
				homeAddr.setEnabled(true);
				homeDesc.setEnabled(true);
//				homeEditBtn.setImageResource(R.drawable.ic_baseline_save_24);
				break;
		}
	}

	void setValues() {
		name.setText(member.getName());
		email.setText(member.getEmail());
		phone.setText(member.getPhone());
		homeName.setText(homeRef.getName());
		homeAddr.setText(homeRef.getLocation());
		homeDesc.setText(homeRef.getDescription());
	}
}