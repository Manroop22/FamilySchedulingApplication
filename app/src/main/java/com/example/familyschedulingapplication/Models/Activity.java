package com.example.familyschedulingapplication.Models;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Date;

public class Activity {
    private String name;
    private DocumentReference category;
    private String notes;
    private ArrayList<DocumentReference> invites;
    private ArrayList<String> notificationMethod;
    private DocumentReference createdBy;
    private Date createdAt;
    private Date updatedAt;
    private Date activityDate;
    private static final String TAG = "Activity";
    public static final String collection = "activities";
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public Activity() {}

    public Activity(String name, DocumentReference category, String notes, ArrayList<DocumentReference> invites, ArrayList<String> notificationMethod, DocumentReference createdBy, Date createdAt, Date updatedAt, Date activityDate) {
        this.name = name;
        this.category = category;
        this.notes = notes;
        this.invites = invites;
        this.notificationMethod = notificationMethod;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.activityDate = activityDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public static Activity getActivity(DocumentSnapshot document) {
        Activity act = new Activity();
        act.setName(document.getString("name"));
        act.setCategory(document.getDocumentReference("category"));
        act.setNotes(document.getString("notes"));
        act.setInvites((ArrayList<DocumentReference>) document.get("invites"));
        act.setNotificationMethod((ArrayList<String>) document.get("notificationMethod"));
        act.setCreatedBy(document.getDocumentReference("createdBy"));
        act.setCreatedAt(document.getDate("createdAt"));
        act.setUpdatedAt(document.getDate("updatedAt"));
        act.setActivityDate(document.getDate("activityDate"));
        return act;
    }

    public static Activity getActivityById(String actId) {
        DocumentSnapshot document = db.collection(collection).document(actId).get().getResult();
        return getActivity(document);
    }

    public static void addActivity(Activity act) {
        db.collection(collection).add(act);
    }

    public static void updateActivity(String actId, Activity act) {
        act.setUpdatedAt(new Date());
        db.collection(collection).document(actId).set(act);
    }

    public static void deleteActivity(String actId) {
        db.collection(collection).document(actId).delete();
    }

    public void save() {
        if (this.createdAt == null) {
            addActivity(this);
        } else {
            updateActivity(this.getReference().getId(), this);
        }
    }

    public DocumentReference getReference() {
        final DocumentReference[] docRef = new DocumentReference[1];
        db.collection(collection).whereEqualTo("name", this.name).whereEqualTo("category", this.category).whereEqualTo("notes", this.notes).whereEqualTo("invites", this.invites).whereEqualTo("notificationMethod", this.notificationMethod).whereEqualTo("createdBy", this.createdBy).whereEqualTo("createdAt", this.createdAt).whereEqualTo("updatedAt", this.updatedAt).whereEqualTo("activityDate", this.activityDate).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    docRef[0] = document.getReference();
                }
            }
        });
        return docRef[0];
    }
}
