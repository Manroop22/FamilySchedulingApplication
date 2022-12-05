package com.example.familyschedulingapplication.Models;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Activity implements Serializable {
    private String name;
    private String activityId;
    private DocumentReference category;
    private String notes;
    private ArrayList<DocumentReference> invites;
    private ArrayList<String> notificationMethod;
    private DocumentReference createdBy;
    private DocumentReference reference;
    private Date createdAt;
    private Date updatedAt;
    private Date activityDate;
    private DocumentReference homeId;
    private static final String TAG = "Activity";
    public static final String collection = "activities";
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public Activity() {}

    public Activity(String name, DocumentReference category, String notes, ArrayList<DocumentReference> invites, ArrayList<String> notificationMethod, DocumentReference createdBy, Date createdAt, Date updatedAt, Date activityDate, DocumentReference homeId) {
        this.name = name;
        this.category = category;
        this.notes = notes;
        this.invites = invites;
        this.notificationMethod = notificationMethod;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.activityDate = activityDate;
        this.homeId = homeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public DocumentReference getCategory() {
        return category;
    }

    public void setCategory(DocumentReference category) {
        this.category = category;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public ArrayList<DocumentReference> getInvites() {
        return invites;
    }

    public void setInvites(ArrayList<DocumentReference> invites) {
        this.invites = invites;
    }

    public ArrayList<String> getNotificationMethod() {
        return notificationMethod;
    }

    public void setNotificationMethod(ArrayList<String> notificationMethod) {
        this.notificationMethod = notificationMethod;
    }

    public void addNotificationMethod(String notificationMethod) {
        this.notificationMethod.add(notificationMethod);
    }

    public void removeNotificationMethod(String notificationMethod) {
        this.notificationMethod.remove(notificationMethod);
    }

    public DocumentReference getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(DocumentReference createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(Date activityDate) {
        this.activityDate = activityDate;
    }

    public DocumentReference getHomeId() {
        return homeId;
    }

    public void setHomeId(DocumentReference homeId) {
        this.homeId = homeId;
    }

    public static Activity getActivity(DocumentSnapshot document) {
        Activity activity = document.toObject(Activity.class);
        assert activity != null;
        activity.setReference(document.getReference());
        return activity;
    }

    public static Activity getActivityByCreatorAndName(DocumentReference createdBy, String name) {
        Activity act = new Activity();
        try {
            QuerySnapshot query = db.collection(collection).whereEqualTo("createdBy", createdBy).whereEqualTo("name", name).get().getResult();
            if (query.getDocuments().size() > 0) {
                act = getActivity(query.getDocuments().get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return act;
    }

    public static QuerySnapshot getActivitiesByMember(DocumentReference memRef) {
        return db.collection(collection).whereArrayContains("invites", memRef).whereEqualTo("createdBy", memRef).get().getResult();
    }

    public static Activity getActivityById(String actId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        Activity act = new Activity();
        try {
            db.collection(collection).document(actId).get().addOnCompleteListener(onCompleteListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return act;
    }

    public void saveActivity(OnCompleteListener<Void> onCompleteListener) {
        db.collection(collection).document(this.activityId).set(this).addOnCompleteListener(onCompleteListener);
    }

    public void deleteActivity(OnCompleteListener<Void> onCompleteListener) {
        db.collection(collection).document(this.activityId).delete().addOnCompleteListener(onCompleteListener);
    }

    public static void getActivitiesByHomeId(DocumentReference homeId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        db.collection(collection).whereEqualTo("homeId", homeId).get().addOnCompleteListener(onCompleteListener);
    }

    public static void addActivity(Activity activity, OnCompleteListener<Void> onCompleteListener) {
        // Activity.getActivityId() and add new Activity to Activitys collection
        db.collection(collection).document(activity.getActivityId()).set(activity).addOnCompleteListener(onCompleteListener);
    }

    public static void updateActivity(Activity activity, OnCompleteListener<Void> onCompleteListener) {
        db.collection(collection).document(activity.getActivityId()).set(activity).addOnCompleteListener(onCompleteListener);
    }

    public static void deleteActivity(Activity activity, OnCompleteListener<Void> onCompleteListener) {
        db.collection(collection).document(activity.getActivityId()).delete().addOnCompleteListener(onCompleteListener);
    }

    public static void getActivityByHomeId(DocumentReference homeId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        db.collection(collection).whereEqualTo("homeId", homeId).get().addOnCompleteListener(onCompleteListener);
    }

    public static Activity getActivity(String activityId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        Activity Activity = new Activity();
        try {
            db.collection(collection).document(activityId).get().addOnCompleteListener(onCompleteListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Activity;
    }

    public DocumentReference getReference() {
        return db.collection(collection).document(this.activityId);
    }

    public void setReference(DocumentReference docRef) {
        this.reference = docRef;
    }
}
