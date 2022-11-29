package com.example.familyschedulingapplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Event implements Serializable {
    private String name;
    private String description;
    private ArrayList<String> members=new ArrayList<>(); // This will be used to store the name of the members added.
    private String notes;
    private Date date;
    public Event(String name){this.name=name;}
    public Event(String name, Date date) {
        this.name = name;
        this.date = date;
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

    public ArrayList<String> getMembers() {
        return members;
    }
    public void addMember(ArrayList<String> members){
        setMembers(members);
    }
    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Event{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", members='" + members + '\'' +
                ", notes='" + notes + '\'' +
                ", date=" + date +
                '}';
    }
}
