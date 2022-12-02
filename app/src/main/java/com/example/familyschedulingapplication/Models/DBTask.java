package com.example.familyschedulingapplication.Models;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * name, createdAt, createdBy (references a member collection document), note, type, category (references listCategories collection document)
 */
public class DBTask {
    private String name;
    private String createdAt;
    private String createdBy;
    private String note;
    private String type;
    private String category;
    public static final String collection = "tasks";
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public DBTask() {
    }

    public DBTask(String name, String createdAt, String createdBy, String note, String type, String category) {
        this.name = name;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.note = note;
        this.type = type;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public static DBTask getTask(String id) {
        ArrayList<DBTask> DBTasks = new ArrayList<>();
        db.collection(collection).document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
//                    task[0] = document.toObject(Task.class);
                    DBTasks.add(document.toObject(DBTask.class));
                } else {
                    Log.d("Task", "No such document");
                }
            } else {
                Log.d("Task", "get failed with ", task.getException());
            }
        });
        return DBTasks.get(0);
    }

    public static ArrayList<DBTask> getTasks() {
        ArrayList<DBTask> DBTasks = new ArrayList<>();
        db.collection(collection).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot document = task.getResult();
                if (document != null) {
                    for (DocumentSnapshot doc : document.getDocuments()) {
                        DBTasks.add(doc.toObject(DBTask.class));
                    }
                } else {
                    Log.d("Task", "No such document");
                }
            } else {
                Log.d("Task", "get failed with ", task.getException());
            }
        });
        return DBTasks;
    }

    public static ArrayList<DBTask> getTasksByCategory(String category) {
        ArrayList<DBTask> DBTasks = new ArrayList<>();
        db.collection(collection).whereEqualTo("category", category).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot document = task.getResult();
                if (document != null) {
                    for (DocumentSnapshot doc : document.getDocuments()) {
                        DBTasks.add(doc.toObject(DBTask.class));
                    }
                } else {
                    Log.d("Task", "No such document");
                }
            } else {
                Log.d("Task", "get failed with ", task.getException());
            }
        });
        return DBTasks;
    }

    public static ArrayList<DBTask> getTasksByMember(String member) {
        ArrayList<DBTask> DBTasks = new ArrayList<>();
        db.collection(collection).whereEqualTo("createdBy", member).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot document = task.getResult();
                if (document != null) {
                    for (DocumentSnapshot doc : document.getDocuments()) {
                        DBTasks.add(doc.toObject(DBTask.class));
                    }
                } else {
                    Log.d("Task", "No such document");
                }
            } else {
                Log.d("Task", "get failed with ", task.getException());
            }
        });
        return DBTasks;
    }
}
