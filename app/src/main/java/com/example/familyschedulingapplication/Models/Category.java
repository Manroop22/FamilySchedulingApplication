package com.example.familyschedulingapplication.Models;

import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.Objects;

public class Category {
    private String name;
    private String categoryId;
    private int color; // hexadecimal
    private DocumentReference createdBy;
    private DocumentReference reference;
    private Date createdAt;
    private Date updatedAt;
    private DocumentReference createdFor; // document reference
    private String createdForType; // list or activity
    private static final String TAG = "Category";
    public static final String collection = "categories";
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public Category() {}
    public Category(String name) {
        this.name = name;
    }
    public Category(String name, int color) {
        this.name = name;
        setColor(color);
    }

    public Category(String name, Color color) {
        this.name = name;
        setColor(Color.parseColor(color.toString()));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;
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

    public DocumentReference getCreatedFor() {
        return createdFor;
    }

    public void setCreatedFor(DocumentReference createdFor) {
        this.createdFor = createdFor;
    }

    public String getCreatedForType() {
        return createdForType;
    }

    public void setCreatedForType(String createdForType) {
        this.createdForType = createdForType;
    }

    public DocumentReference getReference() {
        return db.collection(collection).document(categoryId);
    }

    public static void addCategory(Category category, OnCompleteListener<Void> onCompleteListener) {
        // Category.getCategoryId() and add new Category to Category collection
        db.collection(collection).document(category.getCategoryId()).set(category).addOnCompleteListener(onCompleteListener);
    }

    public static void updateCategory(Category category, OnCompleteListener<Void> onCompleteListener) {
        db.collection(collection).document(category.getCategoryId()).set(category).addOnCompleteListener(onCompleteListener);
    }

    public static void deleteCategory(Category category, OnCompleteListener<Void> onCompleteListener) {
        db.collection(collection).document(category.getCategoryId()).delete().addOnCompleteListener(onCompleteListener);
    }

    public static void getCategoryCreatedByMe(DocumentReference memberRef, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        db.collection(collection).whereEqualTo("createdBy", memberRef).get().addOnCompleteListener(onCompleteListener);
    }

    public static void getCategoryCreatedByMeByType(DocumentReference memberRef, String type, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        db.collection(collection).whereEqualTo("createdBy", memberRef).whereEqualTo("createdForType", type).get().addOnCompleteListener(onCompleteListener);
    }

    public static Category getCategory(String CategoryId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        Category category = new Category();
        try {
            db.collection(collection).document(CategoryId).get().addOnCompleteListener(onCompleteListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return category;
    }

    public static QuerySnapshot getCategoriesByCreatedBy(DocumentReference createdBy) {
        QuerySnapshot categories = null;
        try {
            categories = db.collection(collection).whereEqualTo("createdBy", createdBy).get().getResult();
        } catch (Exception e) {
            Log.d(TAG, "getCategoriesByCreatedBy: " + e.getMessage());
        }
        return categories;
    }

    public static Category getCategoryByReference(DocumentReference reference, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        Category category = new Category();
        try {
            db.collection(collection).document(reference.getId()).get().addOnCompleteListener(onCompleteListener);
        } catch (Exception e) {
            Log.d(TAG, "getCategoryByReference: " + e.getMessage());
        }
        return category;
    }

    public void setReference(DocumentReference reference) {
        this.reference = reference;
    }
}
