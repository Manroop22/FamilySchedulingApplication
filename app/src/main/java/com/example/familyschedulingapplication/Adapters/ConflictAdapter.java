package com.example.familyschedulingapplication.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familyschedulingapplication.Models.Conflict;
import com.example.familyschedulingapplication.Models.Member;
import com.example.familyschedulingapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ConflictAdapter extends RecyclerView.Adapter<ConflictAdapter.ViewHolder> {
	public static final String TAG = "ConflictAdapter";
	public final ArrayList<Conflict> conflictList;
	public int layoutId;
	int count=0;
	FirebaseFirestore db = FirebaseFirestore.getInstance();

	public ConflictAdapter(ArrayList<Conflict> conflictList, int layout) {
		this.conflictList = conflictList;
		this.layoutId = layout;
	}

	@NonNull
	@Override
	public ConflictAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		Log.i (TAG,  "onCreateViewHolder: "+ count++);
		LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext ());
		View view = layoutInflater.inflate (layoutId, parent, false);
		return new ViewHolder (view);
	}

	@Override
	public void onBindViewHolder(@NonNull ConflictAdapter.ViewHolder holder, int position) {
		Conflict conflict = conflictList.get(position);
		holder.conflictDate.setText(conflict.getConflictDate().toString());
		conflict.getConflictee().get().addOnCompleteListener(task -> {
			if (task.isSuccessful()) {
				Member member = task.getResult().toObject(Member.class);
				assert member != null;
				holder.conflictName.setText(member.getName());
			}
		});
	}

	@Override
	public int getItemCount() {
		return conflictList.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		public EditText conflictDate, conflictName;
		Button sendChange;
		ImageButton acceptButton, rejectButton;
		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			conflictName = itemView.findViewById(R.id.conflictName);
			conflictDate = itemView.findViewById(R.id.conflictDate);
		}
	}
}
