package com.example.familyschedulingapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.familyschedulingapplication.Models.Conflict;

import java.util.ArrayList;

public class ConflictsActivity extends AppCompatActivity {
	public String eventId;
	public String activityId;
	public String conflictId;
	public String homeId;
	public String userId;
	public ArrayList<Conflict> conflicts;
	public ArrayList<Conflict> conflictsByEvent;
	public ArrayList<String> conflicteeIds;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conflicts);
	}
}