package com.example.familyschedulingapplication.Models;

import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

/**
 * name, createdAt (Date), createdBy (DocumentReference), description, location (GeoPoint), notes, participants(array of members), updatedAt (Date), eventDate (Date)
 * methods: addEvent, deleteEvent, updateEvent, getEvent, getEvents, getEventsByMember, getEventsByDate, getEventsByLocation, Save, getReference, addParticipant, removeParticipant
 */
public class Event {
    private String name;
    private String eventId;
    private Date createdAt;
    private DocumentReference createdBy;
    private String description;
    private String location;
    private String notes;
    private ArrayList<DocumentReference> participants;
    private Date updatedAt;
    private DocumentReference reference;
    private Date eventDate;
    public static final String collection = "events";
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Event() {
    }

    public Event(String name, Date createdAt, DocumentReference createdBy, String description, String location, String notes, ArrayList<DocumentReference> participants, Date updatedAt, Date eventDate) {
        this.name = name;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.description = description;
        this.location = location;
        this.notes = notes;
        this.participants = participants;
        this.updatedAt = updatedAt;
        this.eventDate = eventDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
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

    public ArrayList<DocumentReference> getParticipants() {
        return participants;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public static void addEvent(Event event, OnCompleteListener<DocumentReference> onCompleteListener) {
        db.collection(collection).add(event).addOnCompleteListener(onCompleteListener);
    }

    public static void addEvent(Event event) {
        db.collection(collection).add(event);
    }

//    public static void deleteEvent(String id) {
//        db.collection(collection).document(id).delete();
//    }

    public static void deleteEvent(Event event) {
        db.collection(collection).document(event.getReference().getId()).delete();
    }

    public void deleteEvent(OnCompleteListener<Void> onCompleteListener) {
        db.collection(collection).document(this.getReference().getId()).delete().addOnCompleteListener(onCompleteListener);
    }

    public static void updateEvent(Event event, OnCompleteListener<Void> onCompleteListener) {
        event.setUpdatedAt(new Date());
        db.collection(collection).document(event.getReference().getId()).set(event).addOnCompleteListener(onCompleteListener);
    }

    public static void updateEvent(Event event) {
        event.setUpdatedAt(new Date());
        db.collection(collection).document(event.getReference().getId()).set(event).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(null, "Event updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(null, "Event update failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void getEvent(DocumentReference evId, OnCompleteListener onCompleteListener) {
        db.collection(collection).document(evId.getId()).get().addOnCompleteListener(onCompleteListener);
    }
    public static void getEvent(DocumentSnapshot evId, OnCompleteListener onCompleteListener) {
        db.collection(collection).document(evId.getId()).get().addOnCompleteListener(onCompleteListener);
    }

    public static Event GetEvent(DocumentReference evId) {
        Event event = new Event();
        db.collection(collection).document(evId.getId()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    event.setName(document.getString("name"));
                    event.setCreatedAt(document.getDate("createdAt"));
                    event.setCreatedBy(document.getDocumentReference("createdBy"));
                    event.setDescription(document.getString("description"));
                    event.setLocation(document.getString("location"));
                    event.setNotes(document.getString("notes"));
                    event.setParticipants((ArrayList<DocumentReference>) document.get("participants"));
                    event.setUpdatedAt(document.getDate("updatedAt"));
                    event.setEventDate(document.getDate("eventDate"));
                    event.setReference(document.getReference());
                }
            }
        });
        return event;
    }

    public static Task<DocumentSnapshot> getEventByEventId(String evId) {
        return db.collection(collection).document(evId).get();
    }

    public static Event getEvent(DocumentSnapshot evId) {
        Event event = new Event();
        event.setName(evId.getString("name"));
        event.setCreatedAt(evId.getDate("createdAt"));
        event.setCreatedBy(evId.getDocumentReference("createdBy"));
        event.setDescription(evId.getString("description"));
        event.setLocation(evId.getString("location"));
        event.setNotes(evId.getString("notes"));
        event.setParticipants((ArrayList<DocumentReference>) evId.get("participants"));
        event.setUpdatedAt(evId.getDate("updatedAt"));
        event.setEventDate(evId.getDate("eventDate"));
        event.setReference(evId.getReference());
        return event;
    }

    public static void getEvents(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        db.collection(collection).get().addOnCompleteListener(onCompleteListener);
    }

    public static ArrayList<Event> getEvents() {
        final ArrayList<Event> events = new ArrayList<>();
        db.collection(collection).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    events.add(document.toObject(Event.class));
                }
            } else {
                System.out.println("Error getting documents: " + task.getException());
            }
        });
        return events;
    }

    public static void getEventsByMember(DocumentReference memId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        // find events member is a participant in and events created by memId
        db.collection(collection).whereArrayContains("participants", memId).whereEqualTo("createdBy", memId).get().addOnCompleteListener(onCompleteListener);
    }

    public static ArrayList<Event> getEventsByMember(DocumentReference memId) {
        // find events member is a participant in and events created by memId
        ArrayList<Event> events = new ArrayList<>();
        db.collection(collection).whereArrayContains("participants", memId).whereEqualTo("createdBy", memId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    events.add(document.toObject(Event.class));
                }
            }
        });
        return events;
    }

    public static void getEventsByDate(Date date, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        db.collection(collection).whereEqualTo("eventDate", date).get().addOnCompleteListener(onCompleteListener);
    }

    public static void getEventsByLocation(String location, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        db.collection(collection).whereEqualTo("location", location).get().addOnCompleteListener(onCompleteListener);
    }

    public void Save() {
        if (getReference() == null) {
            addEvent(this);
        } else {
            updateEvent(this);
        }
    }

    public DocumentReference getReference() {
        return reference;
    }

    public void setParticipants(ArrayList<DocumentReference> participants) {
        this.participants = participants;
    }

    public void setReference(DocumentReference reference) {
        this.reference = reference;
    }

//    public void setParticipants(ArrayList<Member> participants) {
//        this.participants = participants;
//    }

    public void addParticipant(Member member) {
        this.participants.add(member.getReference());
    }
}
