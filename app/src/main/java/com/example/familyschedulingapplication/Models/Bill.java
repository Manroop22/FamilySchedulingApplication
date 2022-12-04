package com.example.familyschedulingapplication.Models;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class Bill {
    private String billId;
    private String name;
    private double amount;
    private Date dueDate;
    private String note;
    private DocumentReference createdBy;
    private Date createdAt;
    private Date updatedAt;
    private ArrayList<DocumentReference> permitted;
    private DocumentReference paidBy;
    private String link;
    private int occurrence;
    private Boolean paid;
    private Date paidAt;
    private ArrayList<String> notificationType;
    private static final String TAG = "Bill";
    public static final String collection = "bills";
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Bill() {}

    public Bill(String billId, String name, double amount, Date dueDate, String note, DocumentReference createdBy, Date createdAt, Date updatedAt, ArrayList<DocumentReference> permitted, DocumentReference paidBy, String link, int occurrence, Boolean paid, ArrayList<String> notificationType, Date paidAt) {
        this.billId = billId;
        this.name = name;
        this.amount = amount;
        this.dueDate = dueDate;
        this.note = note;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.permitted = permitted;
        this.paidBy = paidBy;
        this.link = link;
        this.occurrence = occurrence;
        this.paid = paid;
        this.notificationType = notificationType;
        this.paidAt = paidAt;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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

    public ArrayList<DocumentReference> getPermitted() {
        return permitted;
    }

    public void setPermitted(ArrayList<DocumentReference> permitted) {
        this.permitted = permitted;
    }

    public DocumentReference getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(DocumentReference paidBy) {
        this.paidBy = paidBy;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(int occurrence) {
        this.occurrence = occurrence;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public Date getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(Date paidAt) {
        this.paidAt = paidAt;
    }

    public ArrayList<String> getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(ArrayList<String> notificationType) {
        this.notificationType = notificationType;
    }

    public static void addBill(Bill bill, OnCompleteListener<Void> onCompleteListener) {
        // bill.getBillId() and add new bill to bills collection
        db.collection(collection).document(bill.getBillId()).set(bill).addOnCompleteListener(onCompleteListener);
    }

    public static void updateBill(Bill bill, OnCompleteListener<Void> onCompleteListener) {
        db.collection(collection).document(bill.getBillId()).set(bill).addOnCompleteListener(onCompleteListener);
    }

    public static void deleteBill(Bill bill, OnCompleteListener<Void> onCompleteListener) {
        db.collection(collection).document(bill.getBillId()).delete().addOnCompleteListener(onCompleteListener);
    }

    public static Bill getBill(String billId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        Bill bill = new Bill();
        try {
            db.collection(collection).document(billId).get().addOnCompleteListener(onCompleteListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bill;
    }

    public static void getBills(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        db.collection(collection).get().addOnCompleteListener(onCompleteListener);
    }

    public static void getBillsByMember(DocumentReference memRef, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        db.collection(collection).whereEqualTo("createdBy", memRef).get().addOnCompleteListener(onCompleteListener);
    }

    public static void getBillsIfPermited(DocumentReference memRef, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        db.collection(collection).whereArrayContains("permitted", memRef).get().addOnCompleteListener(onCompleteListener);
    }

    public static Bill getBill(DocumentSnapshot billSnap) {
        Bill bill = new Bill();
        bill.setBillId(billSnap.getId());
        bill.setName(billSnap.getString("name"));
        bill.setAmount((Double) billSnap.get("amount"));
        bill.setDueDate(billSnap.getDate("dueDate"));
        bill.setNote(billSnap.getString("note"));
        bill.setCreatedBy(billSnap.getDocumentReference("createdBy"));
        bill.setCreatedAt(billSnap.getDate("createdAt"));
        bill.setUpdatedAt(billSnap.getDate("updatedAt"));
        bill.setPermitted((ArrayList<DocumentReference>) billSnap.get("permitted"));
        bill.setPaidBy(billSnap.getDocumentReference("paidBy"));
        bill.setPaidAt(billSnap.getDate("paidAt"));
        bill.setLink(billSnap.getString("link"));
        bill.setOccurrence(((Long) billSnap.get("occurrence")).intValue());
        bill.setPaid(billSnap.getBoolean("paid"));
        bill.setNotificationType((ArrayList<String>) billSnap.get("notificationType"));
        return bill;
    }
}
