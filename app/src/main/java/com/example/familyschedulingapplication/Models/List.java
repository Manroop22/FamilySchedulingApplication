package com.example.familyschedulingapplication.Models;

import static java.util.UUID.*;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * name, createdAt, createdBy (references a member collection document), note, type, category (references listCategories collection document)
 */
public class List {
    private String name;
    private String taskId; // uuid
    private Date createdAt;
    private Date updatedAt;
    private DocumentReference createdBy, reference;
    private String notes;
    private String type = "list";
    private ArrayList<ListItem> listItems;
    private DocumentReference category;
    public static final String collection = "tasks";
    private static final String TAG = "List";
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public List() {}

    public List(String name, String taskId, Date createdAt, Date updatedAt, DocumentReference createdBy, String notes, String type, ArrayList<ListItem> listItems, DocumentReference category) {
        this.name = name;
        this.taskId = taskId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.notes = notes;
        this.type = type;
        this.listItems = listItems;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
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

    public DocumentReference getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(DocumentReference createdBy) {
        this.createdBy = createdBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<ListItem> getListItems() {
        return listItems;
    }

    public void setListItems(ArrayList<ListItem> listItems) {
        this.listItems = listItems;
    }

    public DocumentReference getCategory() {
        return category;
    }

    public void setCategory(DocumentReference category) {
        this.category = category;
    }

    public DocumentReference getReference() {
        return db.collection(collection).document(taskId);
    }

    public static List getList(String taskId) {
        DocumentSnapshot documentSnapshot = db.collection(collection).document(taskId).get().getResult();
        if (documentSnapshot.exists()) {
            return documentSnapshot.toObject(List.class);
        } else {
            Log.d(TAG, "No such document");
            return null;
        }
    }

    public static ArrayList<List> getLists() {
        ArrayList<List> lists = new ArrayList<>();
        QuerySnapshot querySnapshot = db.collection(collection).get().getResult();
        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
            lists.add(documentSnapshot.toObject(List.class));
        }
        return lists;
    }

    public static void addList(List list) {
        // generate a random unique uuid
        list.setTaskId(randomUUID().toString());
        db.collection(collection).document(list.getTaskId()).set(list).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "DocumentSnapshot successfully written!");
            } else {
                Log.w(TAG, "Error writing document", task.getException());
            }
        });
    }

    public static void updateList(List list) {
        list.setUpdatedAt(new Date());
        db.collection(collection).document(list.getTaskId()).set(list).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "DocumentSnapshot successfully updated!");
            } else {
                Log.d(TAG, "Error updating document", task.getException());
            }
        });
    }

    public static void deleteList(List list) {
        db.collection(collection).document(list.getTaskId()).delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "DocumentSnapshot successfully deleted!");
            } else {
                Log.d(TAG, "Error deleting document", task.getException());
            }
        });
    }

    public static void deleteList(String taskId) {
        db.collection(collection).document(taskId).delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "DocumentSnapshot successfully deleted!");
            } else {
                Log.d(TAG, "Error deleting document", task.getException());
            }
        });
    }

    public static void deleteLists(ArrayList<List> lists) {
        for (List list : lists) {
            db.collection(collection).document(list.getTaskId()).delete();
        }
    }

    public static void deleteLists() {
        ArrayList<List> lists = getLists();
        for (List list : lists) {
            db.collection(collection).document(list.getTaskId()).delete();
        }
    }

    public static void deleteListsByCategory(DocumentReference category) {
        ArrayList<List> lists = getLists();
        for (List list : lists) {
            if (list.getCategory().equals(category)) {
                db.collection(collection).document(list.getTaskId()).delete();
            }
        }
    }

    public static void deleteListsByCategory(String categoryId) {
        ArrayList<List> lists = getLists();
        for (List list : lists) {
            if (list.getCategory().getId().equals(categoryId)) {
                db.collection(collection).document(list.getTaskId()).delete();
            }
        }
    }

    public static void deleteListsByMember(DocumentReference member) {
        ArrayList<List> lists = getLists();
        for (List list : lists) {
            if (list.getCreatedBy().equals(member)) {
                db.collection(collection).document(list.getTaskId()).delete();
            }
        }
    }

    public static void deleteListsByMember(String memberId) {
        ArrayList<List> lists = getLists();
        for (List list : lists) {
            if (list.getCreatedBy().getId().equals(memberId)) {
                db.collection(collection).document(list.getTaskId()).delete();
            }
        }
    }

    public static void deleteListsByMemberAndCategory(DocumentReference member, DocumentReference category) {
        ArrayList<List> lists = getLists();
        for (List list : lists) {
            if (list.getCreatedBy().equals(member) && list.getCategory().equals(category)) {
                db.collection(collection).document(list.getTaskId()).delete();
            }
        }
    }

    public static void save(List list) {
        if (list.getTaskId() == null) {
            addList(list);
        } else {
            updateList(list);
        }
    }

    public void save() {
        if (this.getTaskId() == null) {
            addList(this);
        } else {
            updateList(this);
        }
    }

    public void setReference(DocumentReference reference) {
        this.reference = reference;
    }

    public static List getListByTaskId(String taskId) {
        AtomicReference<List> list = new AtomicReference<>(new List());
        db.collection(collection).whereEqualTo("taskId", taskId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    list.get().setTaskId(document.getString("taskId"));
                    list.get().setName(document.getString("name"));
                    list.get().setCreatedAt(document.getDate("createdAt"));
                    list.get().setUpdatedAt(document.getDate("updatedAt"));
                    list.get().setCreatedBy(document.getDocumentReference("createdBy"));
                    list.get().setNotes(document.getString("notes"));
                    list.get().setType(document.getString("type"));
//                    list.get().setListItems((ArrayList<ListItem>) document.get("listItems"));
                    ArrayList<ListItem> listItems = new ArrayList<>();
                    for (Map<String, Object> listItem : (ArrayList<Map<String, Object>>) document.get("listItems")) {
                        listItems.add(new ListItem(listItem));
                    }
                    list.get().setListItems(listItems);
                    list.get().setCategory(document.getDocumentReference("category"));
                }
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
        return list.get();
    }

    public static void getListByListId(String listId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        db.collection(collection).document(listId).get().addOnCompleteListener(onCompleteListener);
    }

    public static List getTaskByReference(DocumentSnapshot taskRef) {
        List list = new List();
        list.setTaskId(taskRef.getString("taskId"));
        list.setName(taskRef.getString("name"));
        list.setCreatedAt(taskRef.getDate("createdAt"));
        list.setUpdatedAt(taskRef.getDate("updatedAt"));
        list.setCreatedBy(taskRef.getDocumentReference("createdBy"));
        list.setNotes(taskRef.getString("notes"));
        list.setType(taskRef.getString("type"));
//        Log.d(TAG, "getTaskByReference: " + taskRef.get("listItems"));
//        Log.d(TAG, Objects.requireNonNull(taskRef.getData()).get("listItems").toString());
        // from Map to ArrayList of ListItem
        ArrayList<ListItem> listItems = new ArrayList<>();
        for (Map<String, Object> listItem : (ArrayList<Map<String, Object>>) taskRef.get("listItems")) {
            listItems.add(new ListItem(listItem));
        }
        list.setListItems(listItems);
//        for (DocumentSnapshot listItemRef : (ArrayList<DocumentSnapshot>) taskRef.get("listItems")) {
//            list.getListItems().add(new ListItem(listItemRef.getString("name"), listItemRef.getBoolean("checked")));
//        }
//        list.setListItems((ArrayList<ListItem>) taskRef.get("listItems"));
        list.setCategory(taskRef.getDocumentReference("category"));
        return list;
    }
}
