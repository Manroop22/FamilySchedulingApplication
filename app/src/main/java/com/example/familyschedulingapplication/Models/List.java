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

    public static void getLists(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        db.collection(collection).get().addOnCompleteListener(onCompleteListener);
    }

    public static void addList(List list, OnCompleteListener<Void> onCompleteListener) {
        // List.getListId() and add new List to Lists collection
        db.collection(collection).document(list.getTaskId()).set(list).addOnCompleteListener(onCompleteListener);
    }

    public static void updateList(List list, OnCompleteListener<Void> onCompleteListener) {
        db.collection(collection).document(list.getTaskId()).set(list).addOnCompleteListener(onCompleteListener);
    }

    public static void deleteList(List list, OnCompleteListener<Void> onCompleteListener) {
        db.collection(collection).document(list.getTaskId()).delete().addOnCompleteListener(onCompleteListener);
    }

    public static void getList(String listId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        try {
            db.collection(collection).document(listId).get().addOnCompleteListener(onCompleteListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setReference(DocumentReference reference) {
        this.reference = reference;
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
