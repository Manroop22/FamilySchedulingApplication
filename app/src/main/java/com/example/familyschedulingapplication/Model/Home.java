package com.example.familyschedulingapplication.Model;

/**
 * name, createdAt, createdBy (references a member collection document), description, location (address string from google maps), active, address (lat, long)
 */
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;


public class Home {
    private String name;
    private Date createdAt;
    private String createdBy;
    private String description;
    private String location;
    private boolean active;
    private String address;
    public static final String collection = "homes";
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Home() {
    }

    public Home(String name, Date createdAt, String createdBy, String description, String location, boolean active, String address) {
        this.name = name;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.description = description;
        this.location = location;
        this.active = active;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public static void CreateHome(Home home) {
        db.collection(collection).add(home).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Home", "Home created successfully");
            } else {
                Log.d("Home", "Home creation failed");
            }
        });
    }

    public static void UpdateHome(Home home) {
        db.collection(collection).document(home.getName()).set(home).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Home", "Home updated successfully");
            } else {
                Log.d("Home", "Home update failed");
            }
        });
    }

    public static void DeleteHome(Home home) {
        db.collection(collection).document(home.getName()).delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Home", "Home deleted successfully");
            } else {
                Log.d("Home", "Home deletion failed");
            }
        });
    }

    public static Home GetHome(String homeId) {
        Home home = new Home();
        db.collection(collection).document(homeId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    home.setName(document.getString("name"));
                    home.setCreatedAt(document.getDate("createdAt"));
                    home.setCreatedBy(document.getString("createdBy"));
                    home.setDescription(document.getString("description"));
                    home.setLocation(document.getString("location"));
                    home.setActive(document.getBoolean("active"));
                    home.setAddress(document.getString("address"));
                } else {
                    Log.d("Home", "No such document");
                }
            } else {
                Log.d("Home", "get failed with ", task.getException());
            }
        });
        return home;
    }

    public void addMemberToHome(String memberId) {
        Member member = Member.GetMember(memberId);
        member.setHomeId(this.getName());
        member.Save();
    }

    public void removeMemberFromHome(String memberId) {
        Member member = Member.GetMember(memberId);
        member.setHomeId(null);
        member.Save();
    }

    public void Save() {
        if (this.getName() == null) {
            this.setCreatedAt(new Date());
            CreateHome(this);
        } else {
            UpdateHome(this);
        }
    }
}