package com.example.familyschedulingapplication.Models;

/**
 * properties: name, createdAt, createdBy (references a member collection document), accessCode, description, location (address string from google maps), active, address (lat, long)
 * methods: addHome, updateHome, deleteHome, getHome, getHomes, getHomesByMember, getReference (returns a reference to the home document), getHomeByReference (returns a home object from a reference), Save
 */
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;


public class Home {
    private String name;
    private String homeId;
    private Date createdAt;
    private DocumentReference createdBy;
    private String accessCode;
    private String description;
    private String location;
    private boolean active;
    private GeoPoint address;
    public static final String collection = "homes";
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Home() {
    }

    public Home(String name, String accessCode) {
        this.name = name;
        this.accessCode = accessCode;
    }

    public Home(String name, Date createdAt, DocumentReference createdBy, String accessCode, String description, String location, boolean active, GeoPoint address) {
        this.name = name;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.accessCode = accessCode;
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

    public String getHomeId() {
        return homeId;
    }

    public void setHomeId(String homeId) {
        this.homeId = homeId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public DocumentReference getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(DocumentReference createdBy) {
        this.createdBy = createdBy;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
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

    public GeoPoint getAddress() {
        return address;
    }

    public void setAddress(GeoPoint address) {
        this.address = address;
    }

    public static String getCollection() {
        return collection;
    }

    public static FirebaseFirestore getDb() {
        return db;
    }

    public static void addHome(Home home, OnCompleteListener<Void> onCompleteListener) {
        db.collection(collection).document(home.getHomeId()).set(home).addOnCompleteListener(onCompleteListener);
    }

    public static void updateHome(Home home, OnCompleteListener<Void> onCompleteListener) {
        db.collection(collection).document(home.getHomeId()).set(home).addOnCompleteListener(onCompleteListener);
    }

    public void updateHome() {
        db.collection(collection).document(this.getHomeId()).set(this);
    }

    public static void deleteHome(Home home, OnCompleteListener<Void> onCompleteListener) {
        db.collection(collection).document(home.getHomeId()).delete().addOnCompleteListener(onCompleteListener);
    }

    public static void getHomes(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        db.collection(collection).get().addOnCompleteListener(onCompleteListener);
    }

    public static void getHomeByAccessCode(String accessCode, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        db.collection(collection).whereEqualTo("accessCode", accessCode).get().addOnCompleteListener(onCompleteListener);
    }

    public static void getHomeById(DocumentReference homeId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        db.collection(collection).document(homeId.getId()).get().addOnCompleteListener(onCompleteListener);
    }

    public static Home getHome(DocumentSnapshot snap) {
        if (snap == null) {
            return null;
        } else {
            Home home = new Home();
            home.setName(snap.getString("name"));
            home.setAccessCode(snap.getString("accessCode"));
            home.setAddress(snap.getGeoPoint("address"));
            home.setActive(Boolean.TRUE.equals(snap.getBoolean("active")));
            home.setCreatedBy(snap.getDocumentReference("createdBy"));
            home.setCreatedAt(snap.getDate("createdAt"));
            home.setDescription(snap.getString("description"));
            home.setLocation(snap.getString("location"));
            return home;
        }
    }

    public DocumentReference getReference() {
        return db.collection(collection).document(getHomeId());
    }

    public static String createAccessCode() {
        StringBuilder accCode = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int rand = (int) (Math.random() * 36);
            if (rand < 10) {
                accCode.append(rand);
            } else {
                accCode.append((char) (rand + 55));
            }
        }
        return accCode.toString();
    }
}