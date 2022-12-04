package com.example.familyschedulingapplication.Models;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class Message {
    private String messageId;
    private String title;
    private String message;
    private DocumentReference createdBy;
    private Boolean published;
    private Date createdAt;
    private ArrayList<DocumentReference> recipients;
    private ArrayList<String> notificationType;
    private static final String TAG = "Message";
    public static final String collection = "messages";
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public Message() {}

    public Message(String messageId, String title, String message, DocumentReference createdBy, Boolean published, Date createdAt, ArrayList<DocumentReference> recipients, ArrayList<String> notificationType) {
        this.messageId = messageId;
        this.title = title;
        this.message = message;
        this.createdBy = createdBy;
        this.published = published;
        this.createdAt = createdAt;
        this.recipients = recipients;
        this.notificationType = notificationType;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DocumentReference getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(DocumentReference createdBy) {
        this.createdBy = createdBy;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public ArrayList<DocumentReference> getRecipients() {
        return recipients;
    }

    public void setRecipients(ArrayList<DocumentReference> recipients) {
        this.recipients = recipients;
    }

    public ArrayList<String> getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(ArrayList<String> notificationType) {
        this.notificationType = notificationType;
    }

    public static void addMessage(Message message, OnCompleteListener<Void> onCompleteListener) {
        db.collection(collection).document(message.getMessageId()).set(message).addOnCompleteListener(onCompleteListener);
        return;
    }

    public static void getMessage(String messageId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        db.collection(collection).document(messageId).get().addOnCompleteListener(onCompleteListener);
    }

    public static void updateMessage(Message message, OnCompleteListener<Void> onCompleteListener) {
        db.collection(collection).document(message.getMessageId()).set(message).addOnCompleteListener(onCompleteListener);
    }

    public static void deleteMessage(String messageId, OnCompleteListener<Void> onCompleteListener) {
        db.collection(collection).document(messageId).delete().addOnCompleteListener(onCompleteListener);
    }

    public static void getMessagesByMembersInHome(String homeId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        // loop through members where homeId = homeId, get messages where recipients contain member or createdBy = member
        db.collection(Member.collection).whereEqualTo("homeId", homeId).get().addOnCompleteListener(onCompleteListener);
    }

    public static void getMessagesByMember(DocumentReference memberId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        // get messages where recipients contain member or createdBy = member
        db.collection(collection).whereEqualTo("createdBy", memberId).get().addOnCompleteListener(onCompleteListener);
    }

    public static void getMessagesReceived(DocumentReference memberId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        // get messages where recipients contain member
        db.collection(collection).whereArrayContains("recipients", memberId).get().addOnCompleteListener(onCompleteListener);
    }
}
