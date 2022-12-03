package com.example.familyschedulingapplication.Models;

/**
 * properties: name, createdAt, createdBy (references a member collection document), accessCode, description, location (address string from google maps), active, address (lat, long)
 * methods: addHome, updateHome, deleteHome, getHome, getHomes, getHomesByMember, getReference (returns a reference to the home document), getHomeByReference (returns a home object from a reference), Save
 */
import android.util.Log;

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

    public static void addHome(Home home) {
        db.collection(collection).add(home);
    }

    public static void updateHome(Home home) {
        db.collection(collection).document(home.getReference().getId()).set(home);
    }

    public static void deleteHome(Home home) {
        db.collection(collection).document(home.getReference().getId()).delete();
    }

    public static Home getHome(String homeId) {
        DocumentSnapshot documentSnapshot = db.collection(collection).document(homeId).get().getResult();
        return documentSnapshot.toObject(Home.class);
    }

    public static ArrayList<Home> getHomes() {
        ArrayList<Home> homes = new ArrayList<>();
        db.collection(collection).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    homes.add(document.toObject(Home.class));
                }
            } else {
                Log.d("Home", "Error getting documents: ", task.getException());
            }
        });
        return homes;
    }

    public static Home getHomeById(DocumentReference homeId) {
        // if document reference is equal to the homeId, return the home object
        Home[] home = new Home[1];
        db.collection(collection).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    if (document.getReference().equals(homeId)) {
                        home[0] = document.toObject(Home.class);
                    }
                }
            } else {
                Log.d("Home", "Error getting documents: ", task.getException());
            }
        });
        return home[0];
    }

    public static Home getHomeById(DocumentSnapshot snap) {
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

    public static ArrayList<Home> getHomesByMember(String memberId) {
        ArrayList<Home> homes = new ArrayList<>();
        QuerySnapshot querySnapshot = db.collection(collection).whereEqualTo("createdBy", memberId).get().getResult();
        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
            homes.add(documentSnapshot.toObject(Home.class));
        }
        return homes;
    }

    public DocumentReference getReference() {
        return db.collection(collection).document();
    }

    public static Home getHomeByReference(DocumentReference reference) {
        DocumentSnapshot documentSnapshot = reference.get().getResult();
        return documentSnapshot.toObject(Home.class);
    }

    public void Save() {
        if (getReference() == null) {
            addHome(this);
        } else {
            updateHome(this);
        }
    }
}