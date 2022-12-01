package com.example.familyschedulingapplication.Model;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * name, task (references a task collection document), isCompleted, addedAt, addedBy (references a member collection document), updatedAt
 */
public class DBTaskItem {
    private String name;
    private String task;
    private boolean isCompleted;
    private String addedAt;
    private String addedBy;
    private String updatedAt;
    public static final String collection = "taskItems";
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public DBTaskItem() {
    }

    public DBTaskItem(String name, String task, boolean isCompleted, String addedAt, String addedBy, String updatedAt) {
        this.name = name;
        this.task = task;
        this.isCompleted = isCompleted;
        this.addedAt = addedAt;
        this.addedBy = addedBy;
        this.updatedAt = updatedAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(String addedAt) {
        this.addedAt = addedAt;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static FirebaseFirestore getDb() {
        return db;
    }

    public static String getCollection() {
        return collection;
    }

    public static void create(DBTaskItem DBTaskItem, OnCompleteListener<DocumentReference> onCompleteListener) {
        db.collection(collection).add(DBTaskItem).addOnCompleteListener(onCompleteListener);
    }

    public static void read(String id, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        db.collection(collection).document(id).get().addOnCompleteListener(onCompleteListener);
    }

    public static void update(String id, DBTaskItem DBTaskItem, OnCompleteListener<Void> onCompleteListener) {
        db.collection(collection).document(id).set(DBTaskItem).addOnCompleteListener(onCompleteListener);
    }

    public static void delete(String id, OnCompleteListener<Void> onCompleteListener) {
        db.collection(collection).document(id).delete().addOnCompleteListener(onCompleteListener);
    }

    public static ArrayList<DBTaskItem> getTaskItemsByTask(String task) {
        ArrayList<DBTaskItem> DBTaskItems = new ArrayList<>();
        db.collection(collection).whereEqualTo("task", task).get().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                for (DocumentSnapshot document : task1.getResult()) {
                    DBTaskItems.add(document.toObject(DBTaskItem.class));
                }
            }
        });
        return DBTaskItems;
    }
}
