package com.example.familyschedulingapplication.Models;

import java.util.Map;

public class ListItem {
    private String name;
    private Boolean isCompleted;

    public ListItem() {}

    public ListItem(String name, Boolean isCompleted) {
        this.name = name;
        this.isCompleted = isCompleted;
    }

    public ListItem(Map<String, Object> value) {
        this.name = (String) value.get("name");
        this.isCompleted = (Boolean) value.get("completed");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getCompleted() {
        return isCompleted;
    }

    public void setCompleted(Boolean completed) {
        isCompleted = completed;
    }
}
