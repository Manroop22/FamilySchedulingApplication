package com.example.familyschedulingapplication.Misc;

import static java.util.UUID.randomUUID;

import com.google.firebase.firestore.DocumentReference;

import java.util.Date;

public class Notification {
	private String title;
	private String message;
	private String type;
	private String id;
	private String userId;
	private DocumentReference homeId;
	private Date date;

	public Notification(String title, String message, String type, String userId, DocumentReference homeId) {
		this.title = title;
		this.message = message;
		this.type = type;
		this.userId = userId;
		this.homeId = homeId;
		this.date = new Date();
		this.id = randomUUID().toString();
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public DocumentReference getHomeId() {
		return homeId;
	}

	public void setHomeId(DocumentReference homeId) {
		this.homeId = homeId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void sendNotification() {

	}
}
