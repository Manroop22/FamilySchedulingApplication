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
        final DocumentReference[] docRef = new DocumentReference[1];
        db.collection(collection).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot query = task.getResult();
                for (DocumentSnapshot dSnap : query) {
                    if (Objects.equals(dSnap.getDocumentReference("createdBy"), this.getCreatedBy()) && Objects.equals(dSnap.getString("name"), this.getName())) {
                        docRef[0] = dSnap.getReference();
                        break;
                    }
                }
            }
        });
        return docRef[0];
    }

    public static QuerySnapshot getCategories() {
        QuerySnapshot categories = null;
        try {
            categories = db.collection(collection).get().getResult();
        } catch (Exception e) {
            Log.d(TAG, "getCategories: " + e.getMessage());
        }
        return categories;
    }

    public static Category getCategory(DocumentSnapshot cat) {
        Category category = null;
        try {
            category = cat.toObject(Category.class);
        } catch (Exception e) {
            Log.d(TAG, "getCategory: " + e.getMessage());
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

    public static void addCategory(Category cat) {
        try {
            db.collection(collection).add(cat).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "addCategory: Category added successfully");
                } else {
                    Log.d(TAG, "addCategory: " + task.getException().getMessage());
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "addCategory: " + e.getMessage());
        }
    }

    public static void updateCategory(Category cat) {
        try {
            db.collection(collection).document(cat.getName()).set(cat).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "updateCategory: " + cat.getName() + " updated successfully");
                } else {
                    Log.d(TAG, "updateCategory: " + cat.getName() + " failed to update");
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "updateCategory: " + e.getMessage());
        }
    }

    public static void deleteCategory(Category cat) {
        try {
            db.collection(collection).document(cat.getName()).delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "deleteCategory: Category deleted successfully");
                } else {
                    Log.d(TAG, "deleteCategory: " + task.getException().getMessage());
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "deleteCategory: " + e.getMessage());
        }
    }

    public static void deleteCategoryByUser(DocumentReference memRef) {
        try {
            QuerySnapshot categories = getCategoriesByCreatedBy(memRef);
            for (QueryDocumentSnapshot cat : categories) {
                deleteCategory(cat.toObject(Category.class));
            }
        } catch (Exception e) {
            Log.d(TAG, "deleteCategoryByUser: " + e.getMessage());
        }
    }

    public void setReference(DocumentReference reference) {
        this.reference = reference;
    }
}
