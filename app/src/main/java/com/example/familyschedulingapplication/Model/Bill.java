package com.example.familyschedulingapplication.Model;

import com.google.firebase.firestore.DocumentReference;

import org.w3c.dom.Document;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
          /*  bill.put("name", name.getText().toString());
                    bill.put("date", dateText.getText().toString());
                    bill.put("due", dateText2.getText().toString());
                    bill.put("occurrence", occurrence.getText().toString());
                    bill.put("note", note.getText().toString());
                    bill.put("link", link.getText().toString());
                    bill.put("email", email.isChecked());
                    bill.put("sms", sms.isChecked());
                    bill.put("push", push.isChecked());*/


public class Bill {
    private String name;
    private Date createdAt;
    private DocumentReference createdBy;
    private Date date;
    private Date due;
    private int occurrence;
    private String note;
    private URL url;
    private ArrayList<String> notifyBy;

    public Bill(String name, Date createdAt, DocumentReference createdBy,Date date, Date due, int occurrence, String note, URL url, ArrayList<String> notifyBy) {
        this.name = name;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.date = date;
        this.due = due;
        this.occurrence = occurrence;
        this.note = note;
        this.url = url;
        this.notifyBy = notifyBy;
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

    public DocumentReference getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(DocumentReference createdBy) {
        this.createdBy = createdBy;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDue() {
        return due;
    }

    public void setDue(Date due) {
        this.due = due;
    }

    public int getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(int occurrence) {
        this.occurrence = occurrence;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public ArrayList<String> getNotifyBy() {
        return notifyBy;
    }

    public void setNotifyBy(ArrayList<String> notifyBy) {
        this.notifyBy = notifyBy;
    }
}
