package com.example.familyschedulingapplication.Models;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

public class HomeInvite {
	private Boolean accepted;
	private Date acceptedAt;
	private String accessCode;
	private Date createdAt;
	private DocumentReference homeId;
	private DocumentReference invitedBy;
	private String invitedByEmail;
	private DocumentReference invitedMember;
	private String invitedMemberEmail;
	private String homeInviteId;
	public static final String TAG = "HomeInvite";
	public static final String collection = "homeInvites";
	private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

	public HomeInvite() {
	}

	public HomeInvite(Boolean accepted, Date acceptedAt, String accessCode, Date createdAt, DocumentReference homeId, DocumentReference invitedBy, String invitedByEmail, DocumentReference invitedMember, String invitedMemberEmail) {
		this.accepted = accepted;
		this.acceptedAt = acceptedAt;
		this.accessCode = accessCode;
		this.createdAt = createdAt;
		this.homeId = homeId;
		this.invitedBy = invitedBy;
		this.invitedByEmail = invitedByEmail;
		this.invitedMember = invitedMember;
		this.invitedMemberEmail = invitedMemberEmail;
	}

	public Boolean getAccepted() {
		return accepted;
	}

	public void setAccepted(Boolean accepted) {
		this.accepted = accepted;
	}

	public Date getAcceptedAt() {
		return acceptedAt;
	}

	public void setAcceptedAt(Date acceptedAt) {
		this.acceptedAt = acceptedAt;
	}

	public String getAccessCode() {
		return accessCode;
	}

	public void setAccessCode(String accessCode) {
		this.accessCode = accessCode;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public DocumentReference getHomeId() {
		return homeId;
	}

	public void setHomeId(DocumentReference homeId) {
		this.homeId = homeId;
	}

	public DocumentReference getInvitedBy() {
		return invitedBy;
	}

	public void setInvitedBy(DocumentReference invitedBy) {
		this.invitedBy = invitedBy;
	}

	public String getInvitedByEmail() {
		return invitedByEmail;
	}

	public void setInvitedByEmail(String invitedByEmail) {
		this.invitedByEmail = invitedByEmail;
	}

	public DocumentReference getInvitedMember() {
		return invitedMember;
	}

	public void setInvitedMember(DocumentReference invitedMember) {
		this.invitedMember = invitedMember;
	}

	public String getInvitedMemberEmail() {
		return invitedMemberEmail;
	}

	public void setInvitedMemberEmail(String invitedMemberEmail) {
		this.invitedMemberEmail = invitedMemberEmail;
	}

	public String getHomeInviteId() {
		return homeInviteId;
	}

	public void setHomeInviteId(String homeInviteId) {
		this.homeInviteId = homeInviteId;
	}

	public static void getHomeInviteById(String homeInviteId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
		db.collection(collection).document(homeInviteId).get().addOnCompleteListener(onCompleteListener);
	}

	public static void getHomeInvitesByHomeId(DocumentReference homeId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
		db.collection(collection).whereEqualTo("homeId", homeId).get().addOnCompleteListener(onCompleteListener);
	}

	public static void getHomeInvitesByInvitedMemberId(String invitedMemberId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
		db.collection(collection).whereEqualTo("invitedMember", invitedMemberId).get().addOnCompleteListener(onCompleteListener);
	}

	public static void getHomeInvitesByInvitedMemberEmail(String invitedMemberEmail, OnCompleteListener<QuerySnapshot> onCompleteListener) {
		db.collection(collection).whereEqualTo("invitedMemberEmail", invitedMemberEmail).get().addOnCompleteListener(onCompleteListener);
	}

	public static void getHomeInvitesByInvitedByEmail(String invitedByEmail, OnCompleteListener<QuerySnapshot> onCompleteListener) {
		db.collection(collection).whereEqualTo("invitedByEmail", invitedByEmail).get().addOnCompleteListener(onCompleteListener);
	}

	public static void getHomeInvitesByAccessCode(String accessCode, OnCompleteListener<QuerySnapshot> onCompleteListener) {
		db.collection(collection).whereEqualTo("accessCode", accessCode).get().addOnCompleteListener(onCompleteListener);
	}

	public static void getHomeInvitesByMemberNotAccepted(DocumentReference memberId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
		db.collection(collection).whereEqualTo("invitedMember", memberId).whereEqualTo("accepted", false).get().addOnCompleteListener(onCompleteListener);
	}

	public static void getHomeInvitesByMemberEmailNotAccepted(String memberEmail, OnCompleteListener<QuerySnapshot> onCompleteListener) {
		db.collection(collection).whereEqualTo("invitedMemberEmail", memberEmail).whereEqualTo("accepted", false).get().addOnCompleteListener(onCompleteListener);
	}

	public static void getHomeInviteByHomeMemberAndAccessCode(DocumentReference homeId, DocumentReference memberId, String accessCode, OnCompleteListener<QuerySnapshot> onCompleteListener) {
		db.collection(collection).whereEqualTo("homeId", homeId).whereEqualTo("invitedMember", memberId).whereEqualTo("accessCode", accessCode).get().addOnCompleteListener(onCompleteListener);
	}

	public static void updateHomeInvite(HomeInvite homeInvite, OnCompleteListener<Void> onCompleteListener) {
		db.collection(collection).document(homeInvite.getHomeInviteId()).set(homeInvite).addOnCompleteListener(onCompleteListener);
	}

	public static void deleteHomeInvite(HomeInvite homeInvite, OnCompleteListener<Void> onCompleteListener) {
		db.collection(collection).document(homeInvite.getHomeInviteId()).delete().addOnCompleteListener(onCompleteListener);
	}

	public static void createHomeInvite(HomeInvite homeInvite, OnCompleteListener<Void> onCompleteListener) {
		db.collection(collection).document(homeInvite.getHomeInviteId()).set(homeInvite).addOnCompleteListener(onCompleteListener);
	}
}
