package com.example.familyschedulingapplication.Model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;

/**
 * name, description, location, notes, participants(array of members), createdAt, createdBy (references a member collection document), updatedAt
 */
public class Event {
    private String name;
    private String description;
    private String location;
    private String notes;
    private String[] participants;
    private String createdAt;
    private String createdBy;
    private String updatedAt;
    public static final String collection = "events";
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Event() {
    }

    public Event(String name, String description, String location, String notes, String[] participants, String createdAt, String createdBy, String updatedAt) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.notes = notes;
        this.participants = participants;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String[] getParticipants() {
        return participants;
    }

    public void setParticipants(String[] participants) {
        this.participants = participants;
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

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static String getCollection() {
        return collection;
    }

    public static FirebaseFirestore getDb() {
        return db;
    }

    public static void createEvent(Event event) {
        db.collection(collection).add(event);
    }

    public static Event getEvent(String id) {
        final Event[] event = new Event[1];
        db.collection(collection).document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    event[0] = document.toObject(Event.class);
                }
            } else {
                event[0] = null;
            }
        });
        return event[0];
    }

    public static void updateEvent(Event event) {
        db.collection(collection).document(event.getCreatedBy()).set(event);
    }

    public static void deleteEvent(String id) {
        db.collection(collection).document(id).delete();
    }

    public static void deleteAllEvents() {
        db.collection(collection).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    document.getReference().delete();
                }
            }
        });
    }

    public static void deleteAllEventsByUser(String userId) {
        db.collection(collection).whereEqualTo("createdBy", userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    document.getReference().delete();
                }
            }
        });
    }

    @NonNull
    @Override
    public String toString() {
        return "Event{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", notes='" + notes + '\'' +
                ", participants=" + Arrays.toString(participants) +
                ", createdAt='" + createdAt + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
