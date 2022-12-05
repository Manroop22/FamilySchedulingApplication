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

    public Event(String name, Date date) {
        this.name = name;
        this.eventDate = date;
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

    public static void addEvent(Event event, OnCompleteListener<Void> onCompleteListener) {
        db.collection(collection).document(event.getReference().getId()).set(event).addOnCompleteListener(onCompleteListener);
    }

    public void deleteEvent(OnCompleteListener<Void> onCompleteListener) {
        db.collection(collection).document(this.getReference().getId()).delete().addOnCompleteListener(onCompleteListener);
    }

    public static void updateEvent(Event event, OnCompleteListener<Void> onCompleteListener) {
        event.setUpdatedAt(new Date());
        db.collection(collection).document(event.getReference().getId()).set(event).addOnCompleteListener(onCompleteListener);
    }

    public static void getEvent(DocumentReference evId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        db.collection(collection).document(evId.getId()).get().addOnCompleteListener(onCompleteListener);
    }

    public static void getEventByCreatedBy(DocumentReference createdBy, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        db.collection(collection).whereEqualTo("createdBy", createdBy).get().addOnCompleteListener(onCompleteListener);
    }

    public static void getEventByParticipant(DocumentReference participant, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        db.collection(collection).whereArrayContains("participants", participant).get().addOnCompleteListener(onCompleteListener);
    }

    public static void getEventByEventId(String evId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        db.collection(collection).document(evId).get().addOnCompleteListener(onCompleteListener);
    }

    public static Event getEvent(DocumentSnapshot evId) {
        return evId.toObject(Event.class);
    }

    public static void getEvents(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        db.collection(collection).get().addOnCompleteListener(onCompleteListener);
    }

    public DocumentReference getReference() {
        return db.collection(collection).document(eventId);
    }

    public void setParticipants(ArrayList<DocumentReference> participants) {
        this.participants = participants;
    }

    public void setReference(DocumentReference reference) {
        this.reference = reference;
    }

    public void addParticipant(Member member) {
        this.participants.add(member.getReference());
    }
}
