package com.example.familyschedulingapplication.Models;


import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

/**
 * Firestore collection members
 * properties: name, userId, homeId, email, phone, active, joinedAt
 * userId = Firestore user.uid
 * homeId = String that references a home collection document
 * methods = getMembersByHomeId, getMemberByUserId, getMemberByMemberId, addMember, updateMember, deleteMember, Save, save (calls Save) getReference (gets the document reference for the member)
 */
public class Member {
    private String name;
    private String userId;
    private String profileUrl;
    private DocumentReference homeId;
    private String email;
    private String phone;
    private boolean active;
    private Date joinedAt;
    public static final String collection = "members";
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Member() {
    }

    public Member(String userId) {
        this.userId = userId;
    }
    public Member(String name, String userId, String profileUrl, DocumentReference homeId, String email, String phone, boolean active, Date joinedAt) {
        this.name = name;
        this.userId = userId;
        this.profileUrl = profileUrl;
        this.homeId = homeId;
        this.email = email;
        this.phone = phone;
        this.active = active;
        this.joinedAt = joinedAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public DocumentReference getHomeId() {
        return homeId;
    }

    public void setHomeId(DocumentReference homeId) {
        this.homeId = homeId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Date joinedAt) {
        this.joinedAt = joinedAt;
    }

    public static void getMembersByHomeId(String homeId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        db.collection(collection).whereEqualTo("homeId", homeId).get().addOnCompleteListener(onCompleteListener);
    }

    public static void getMembersByHome(DocumentReference home, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        db.collection(collection).whereEqualTo("homeId", home).get().addOnCompleteListener(onCompleteListener);
    }

    public static void getMember(String userId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        db.collection(collection).document(userId).get().addOnCompleteListener(onCompleteListener);
    }

    public static Member getMemberByMemberId(DocumentSnapshot memberSnap) {
        return memberSnap.toObject(Member.class);
    }

    public static void addMember(Member member, OnCompleteListener<Void> onCompleteListener) {
        db.collection(collection).document(member.getUserId()).set(member).addOnCompleteListener(onCompleteListener);
    }

    public static void updateMember(Member member, OnCompleteListener<Void> onCompleteListener) {
        db.collection(collection).document(member.getUserId()).set(member).addOnCompleteListener(onCompleteListener);
    }

    public void updateMember() {
        db.collection(collection).document(this.getUserId()).set(this);
    }

    public static void deleteMember(Member member, OnCompleteListener<Void> onCompleteListener) {
        db.collection(collection).document(member.getUserId()).delete().addOnCompleteListener(onCompleteListener);
    }

    public static void joinHome(DocumentReference homeId, Member member, String accCode, OnCompleteListener<Void> onCompleteListener) {
        member.setHomeId(homeId);
        Home.getHomeById(homeId, task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot homeSnap = task.getResult();
                if (homeSnap.exists()) {
                    Home home = Home.getHome(homeSnap);
                    assert home != null;
                    if (home.getAccessCode().equals(accCode)) {
                        db.collection(collection).document(member.getUserId()).set(member).addOnCompleteListener(onCompleteListener);
                    } else {
                        Log.d("Member", "joinHome: incorrect access code");
                    }
                } else {
                    Log.d("Member", "joinHome: home does not exist");
                }
            } else {
                Log.d("Member", "joinHome: home does not exist");
            }
        });
    }

    public DocumentReference getReference() {
        if (userId == null) {
            return null;
        }
        return db.collection(collection).document(userId);
    }
}
