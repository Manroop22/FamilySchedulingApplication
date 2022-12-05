package com.example.familyschedulingapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.familyschedulingapplication.Adapters.ConflictAdapter;
import com.example.familyschedulingapplication.Models.Conflict;
import com.example.familyschedulingapplication.Models.Member;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;

public class ConflictsActivity extends AppCompatActivity {
	public Date eventDate;
	public String activityId;
	public String conflictId;
	public String homeId;
	public String userId;
	public ArrayList<Conflict> conflicts;
	public ArrayList<Conflict> conflictsByEvent;
	public ArrayList<String> conflicteeIds;
	ImageButton backBtn, syncBtn;
	TextView conflictNotice, conflictMessage;
	EditText conflictDate, proposedDate;
	Button doneBtn;
	RecyclerView conflictsRecycler;
	ConflictAdapter conflictAdapter;
	ArrayList<Conflict> conflictList;
	DocumentReference homeRef;
	Date dateRes;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conflicts);
		backBtn = findViewById(R.id.exitConflicts);
		syncBtn = findViewById(R.id.syncConflicts);
		conflictNotice = findViewById(R.id.conflictNotice);
		conflictMessage = findViewById(R.id.conflictMessage);
		conflictDate = findViewById(R.id.conflictDate);
		proposedDate = findViewById(R.id.proposedDate);
		doneBtn = findViewById(R.id.conflictsDone);
		conflictsRecycler = findViewById(R.id.conflictRecycler);
		eventDate = new Date(getIntent().getLongExtra("eventDate", 0));
//		activityId = getIntent().getStringExtra("activityId");
		homeId = getIntent().getStringExtra("homeId");
		userId = getIntent().getStringExtra("userId");
		init();
	}

	void init() {
		conflictDate.setText(eventDate.toString());
		conflictsByEvent = new ArrayList<>();
		syncBtn.setOnClickListener(v -> sync());
		backBtn.setOnClickListener(v -> finish());
		doneBtn.setOnClickListener(v -> {
			ArrayList<Boolean> resolved = new ArrayList<>();
			for (Conflict conflict : conflictsByEvent) {
				if(conflict.getResolved()) {
					resolved.add(true);
				} else {
					resolved.add(false);
				}
			}
			if(resolved.contains(false)) {
				Toast.makeText(this, "Please resolve all conflicts before continuing", Toast.LENGTH_SHORT).show();
				conflictMessage.setText("Make sure everyone has accepted the changes before continuing");
			} else {
				finish();
			}
		});
		proposedDate.setOnClickListener(v -> toggleMaterialDatePicker());
		sync();
	}

	void toggleMaterialDatePicker() {
		MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
		builder.setTitleText("Select a date");
		MaterialDatePicker<Long> materialDatePicker = builder.build();
		materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
		materialDatePicker.addOnPositiveButtonClickListener(selection -> {
			dateRes = new Date(selection);
			proposedDate.setText(dateRes.toString());
		});
	}

	void sync() {
		Member.getMember(userId, task -> {
			if (task.isSuccessful()) {
				Member member = task.getResult().toObject(Member.class);
				assert member != null;
				homeRef = member.getHomeId();
				if(proposedDate.getText().toString().isEmpty()) {
					Toast.makeText(ConflictsActivity.this, "Please enter a proposed date", Toast.LENGTH_SHORT).show();
				} else {
					Conflict.checkForActivityConflicts(member.getHomeId(), member.getReference(), eventDate, dateRes);
					updateAdapter();
				}
			}
		});
	}

	void updateAdapter() {
		Conflict.getConflictByOriginalDate(homeRef, eventDate, task -> {
			if (task.isSuccessful()) {
				conflictList = new ArrayList<>();
				for (QueryDocumentSnapshot conflictSnap : task.getResult()) {
					Conflict conflict = conflictSnap.toObject(Conflict.class);
					conflictList.add(conflict);
				}
				if (conflictList.isEmpty()) {
					conflictNotice.setText("There are no conflicts for this event");
				} else {
					conflictNotice.setText(String.format("There are %d conflicts for this event", conflictList.size()));
				}
				conflictAdapter = new ConflictAdapter(conflictList, R.layout.conflict_item);
				conflictsRecycler.setAdapter(conflictAdapter);
				conflictsRecycler.setLayoutManager(new LinearLayoutManager(ConflictsActivity.this));
			}
		});
	}
}