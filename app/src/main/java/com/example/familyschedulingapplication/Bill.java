package com.example.familyschedulingapplication;

import java.util.Date;

public class Bill {
    private String name;
    private float amount;
    private Date date;

    public Bill(String name, Date date,float amount) {
        this.name = name;
        this.amount = amount;
        this.date=date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "name='" + name + '\'' +
                ", amount=" + amount +
                ", date=" + date +
                '}';
    }
}
