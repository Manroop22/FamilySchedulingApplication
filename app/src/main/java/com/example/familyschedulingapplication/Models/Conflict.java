package com.example.familyschedulingapplication.Models;

import static java.util.UUID.randomUUID;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.familyschedulingapplication.ConflictsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.Date;

public class Conflict {
	private String conflictId;
	private DocumentReference conflictee;
	private Boolean isResolved;
	private Boolean conflicteeAccept;
	private Date conflictDate;
	private DocumentReference homeId;
	private DocumentReference eventId;
	private DocumentReference activityId;
	private Date originalDate;
	private Date proposedDate;
	private DocumentReference proposer;
	private Boolean proposerAccept;
	private static final String TAG = "Conflict";
	public static final String collection = "conflicts";
	private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

	public Conflict() {
	}

	public Conflict(DocumentReference conflictee, Boolean isResolved, Boolean conflicteeAccept, Date conflictDate, DocumentReference homeId, DocumentReference eventId, DocumentReference activityId, Date originalDate, Date proposedDate, DocumentReference proposer, Boolean proposerAccept) {
		this.conflictee = conflictee;
		this.isResolved = isResolved;
		this.conflicteeAccept = conflicteeAccept;
		this.conflictDate = conflictDate;
		this.homeId = homeId;
		this.eventId = eventId;
		this.activityId = activityId;
		this.originalDate = originalDate;
		this.proposedDate = proposedDate;
		this.proposer = proposer;
		this.proposerAccept = proposerAccept;
	}

	public String getConflictId() {
		return conflictId;
	}

	public void setConflictId(String conflictId) {
		this.conflictId = conflictId;
	}

	public DocumentReference getConflictee() {
		return conflictee;
	}

	public void setConflictee(DocumentReference conflictee) {
		this.conflictee = conflictee;
	}

	public Boolean getResolved() {
		return isResolved;
	}

	public void setResolved(Boolean resolved) {
		isResolved = resolved;
	}

	public Boolean getConflicteeAccept() {
		return conflicteeAccept;
	}

	public void setConflicteeAccept(Boolean conflicteeAccept) {
		this.conflicteeAccept = conflicteeAccept;
	}

	public Date getConflictDate() {
		return conflictDate;
	}

	public void setConflictDate(Date conflictDate) {
		this.conflictDate = conflictDate;
	}

	public DocumentReference getHomeId() {
		return homeId;
	}

	public void setHomeId(DocumentReference homeId) {
		this.homeId = homeId;
	}

	public DocumentReference getEventId() {
		return eventId;
	}

	public void setEventId(DocumentReference eventId) {
		this.eventId = eventId;
	}

	public DocumentReference getActivityId() {
		return activityId;
	}

	public void setActivityId(DocumentReference activityId) {
		this.activityId = activityId;
	}

	public Date getOriginalDate() {
		return originalDate;
	}

	public void setOriginalDate(Date originalDate) {
		this.originalDate = originalDate;
	}

	public Date getProposedDate() {
		return proposedDate;
	}

	public void setProposedDate(Date proposedDate) {
		this.proposedDate = proposedDate;
	}

	public DocumentReference getProposer() {
		return proposer;
	}

	public void setProposer(DocumentReference proposer) {
		this.proposer = proposer;
	}

	public Boolean getProposerAccept() {
		return proposerAccept;
	}

	public void setProposerAccept(Boolean proposerAccept) {
		this.proposerAccept = proposerAccept;
	}

	public void saveConflict(OnCompleteListener<Void> onCompleteListener) {
		db.collection(collection).document(this.getConflictId()).set(this).addOnCompleteListener(onCompleteListener);
	}

	public void deleteConflict(OnCompleteListener<Void> onCompleteListener) {
		db.collection(collection).document(this.getConflictId()).delete().addOnCompleteListener(onCompleteListener);
	}

	public static void getConflict(String conflictId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
		db.collection(collection).document(conflictId).get().addOnCompleteListener(onCompleteListener);
	}

	public static void getConflicts(OnCompleteListener<QuerySnapshot> onCompleteListener) {
		db.collection(collection).get().addOnCompleteListener(onCompleteListener);
	}

	public static void getConflictsByHomeId(String homeId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
		db.collection(collection).whereEqualTo("homeId", homeId).get().addOnCompleteListener(onCompleteListener);
	}

	public static void getConflictsByEventId(String eventId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
		db.collection(collection).whereEqualTo("eventId", eventId).get().addOnCompleteListener(onCompleteListener);
	}

	public static void getConflictsByActivityId(String activityId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
		db.collection(collection).whereEqualTo("activityId", activityId).get().addOnCompleteListener(onCompleteListener);
	}

	public static void getConflictsByProposerId(String proposerId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
		db.collection(collection).whereEqualTo("proposer", proposerId).get().addOnCompleteListener(onCompleteListener);
	}

	public static void getConflictsByConflicteeId(String conflicteeId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
		db.collection(collection).whereEqualTo("conflictee", conflicteeId).get().addOnCompleteListener(onCompleteListener);
	}

	public static void resolveConflict(Conflict conflict, OnCompleteListener<Void> onCompleteListener) {
		// check if both parties have accepted
		if (conflict.getConflicteeAccept() && conflict.getProposerAccept()) {
			// update the activity/event to the new date
			if (conflict.getEventId() != null) {
				Event.getEvent(conflict.getEventId(), task -> {
					if (task.isSuccessful()) {
						Event event = task.getResult().toObject(Event.class);
						assert event != null;
						event.setEventDate(conflict.getProposedDate());
						event.saveEvent(task1 -> {
							if (task1.isSuccessful()) {
								// delete the conflict
								conflict.deleteConflict(onCompleteListener);
							} else {
								Log.e(TAG, "Error updating event date", task1.getException());
							}
						});
					} else {
						Log.e(TAG, "Error getting event", task.getException());
					}
				});
			}
		} else {
			// tell the user to wait for the other party to accept
			Log.e(TAG, "Error: both parties must accept the new date");
		}
	}

	public static void checkForActivityConflicts(DocumentReference homeId, DocumentReference userId, Date eventDate, Date proposedDate) {
		// get all activities with homeId, check if any of them overlap with the eventDate, if so, create a conflict
		// get all events with homeId, check if any of them overlap with the eventDate, if so, create a conflict
		// check if the proposedDate overlaps with any of the above, if so, create a conflict
		// get all activities with homeId
		db.collection(Activity.collection).whereEqualTo("homeId", homeId).whereEqualTo("activityDate", eventDate).get().addOnCompleteListener(task -> {
			if (task.isSuccessful()) {
				for (QueryDocumentSnapshot document : task.getResult()) {
					// create a conflict
					Activity activity = document.toObject(Activity.class);
					Conflict conflict = new Conflict();
					conflict.setHomeId(homeId);
					conflict.setConflictId(randomUUID().toString());
					conflict.setActivityId(document.getReference());
					conflict.setProposer(userId);
					conflict.setProposedDate(proposedDate);
					conflict.setConflictDate(activity.getCreatedAt());
					conflict.setConflictee(activity.getCreatedBy());
					conflict.setOriginalDate(eventDate);
					conflict.setActivityId(activity.getReference());
					conflict.setResolved(false);
					conflict.saveConflict(task1 -> {
						if (task1.isSuccessful()) {
							Log.d(TAG, "Conflict created");
						} else {
							Log.e(TAG, "Error creating conflict", task1.getException());
						}
					});
				}
			} else {
				Log.e(TAG, "Error getting activities", task.getException());
			}
		});
	}

	public static void getConflictByOriginalDate(DocumentReference homeId, Date originalDate, OnCompleteListener<QuerySnapshot> onCompleteListener) {
		db.collection(collection).whereEqualTo("homeId", homeId).whereEqualTo("originalDate", originalDate).get().addOnCompleteListener(onCompleteListener);
	}

	public static void checkForEventConflicts(DocumentReference homeId, DocumentReference userId, Date eventDate, Date proposedDate) {
		// get all events with homeId
		Event.getEventsByHomeId(homeId, task -> {
			if (task.isSuccessful()) {
				ArrayList<Boolean> hasConflict = new ArrayList<>();
				for (QueryDocumentSnapshot document : task.getResult()) {
					Event event = document.toObject(Event.class);
					// check if any of them overlap with the eventDate
					if (event.getEventDate().after(eventDate) && event.getEventDate().before(proposedDate)) {
						hasConflict.add(true);
						// create a conflict
						Conflict conflict = new Conflict();
						conflict.setHomeId(homeId);
						conflict.setEventId(document.getReference());
						conflict.setProposer(userId);
						conflict.setProposedDate(proposedDate);
						conflict.setOriginalDate(eventDate);
						conflict.saveConflict(task1 -> {
							if (task1.isSuccessful()) {
								Log.d(TAG, "Conflict created");
							} else {
								Log.e(TAG, "Error creating conflict", task1.getException());
							}
						});
					}
				}
				if (hasConflict.isEmpty()) {
					Log.d(TAG, "No conflicts found");
				}
			} else {
				Log.e(TAG, "Error getting events", task.getException());
			}
		});
	}


}
