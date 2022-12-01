package com.example.familyschedulingapplication.Model;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            this.email = user.getEmail();
            this.phone = user.getPhoneNumber();
            this.name = user.getDisplayName();
        }
    }
    public Member(String name, String userId, DocumentReference homeId, String email, String phone, boolean active, Date joinedAt) {
        this.name = name;
        this.userId = userId;
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

    public static ArrayList<Member> getMembersByHomeId(String homeId) {
        ArrayList<Member> members = new ArrayList<>();
        db.collection(collection).whereEqualTo("homeId", homeId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    members.add(document.toObject(Member.class));
                }
            } else {
                Log.d("Member", "Error getting documents: ", task.getException());
            }
        });
        return members;
    }

    public static Member getMemberByUserId(String userId) {
        Member member = new Member();
        db.collection(collection).whereEqualTo("userId", userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    member.setName(document.getString("name"));
                    member.setUserId(document.getString("userId"));
                    member.setHomeId(document.getDocumentReference("homeId"));
                    member.setEmail(document.getString("email"));
                    member.setPhone(document.getString("phone"));
                    member.setActive(Boolean.TRUE.equals(document.getBoolean("active")));
                    member.setJoinedAt(document.getDate("joinedAt"));
                }
            } else {
                Log.d("Member", "Error getting documents: ", task.getException());
            }
        });
        return member;
    }

    public static Member getMemberByMemberId(DocumentReference memberReference) {
        Member member = new Member();
        memberReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                member.setName(document.getString("name"));
                member.setUserId(document.getString("userId"));
                member.setHomeId(document.getDocumentReference("homeId"));
                member.setEmail(document.getString("email"));
                member.setPhone(document.getString("phone"));
                member.setActive(Boolean.TRUE.equals(document.getBoolean("active")));
                member.setJoinedAt(document.getDate("joinedAt"));
            } else {
                Log.d("Member", "Error getting documents: ", task.getException());
            }
        });
        return member;
    }

    public static void addMember(Member member, OnCompleteListener<DocumentReference> onCompleteListener) {
        // if member with userId already exists, update it
        db.collection(collection).whereEqualTo("userId", member.getUserId()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().size() > 0) {
                    updateMember(member);
                } else {
                    db.collection(collection).add(member).addOnCompleteListener(onCompleteListener);
                }
            } else {
                Log.d("Member", "Error getting documents: ", task.getException());
            }
        });
    }

    public static void addMember(Member member) {
        // if member with userId already exists, update it
        db.collection(collection).whereEqualTo("userId", member.getUserId()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().size() > 0) {
                    updateMember(member);
                } else {
                    db.collection(collection).add(member).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Log.d("Member", "Member added with ID: " + task1.getResult().getId());
                        } else {
                            Log.w("Member", "Error adding document", task1.getException());
                        }
                    });
                }
            } else {
                Log.d("Member", "Error getting documents: ", task.getException());
            }
        });
    }

    public static void updateMember(Member member, OnCompleteListener<Void> onCompleteListener) {
        db.collection(collection).document(member.getReference().getId()).set(member).addOnCompleteListener(onCompleteListener);
    }

    public static void updateMember(Member member) {
        db.collection(collection).document(member.getReference().getId()).set(member);
    }

    public static void deleteMember(Member member, OnCompleteListener<Void> onCompleteListener) {
        db.collection(collection).document(member.getReference().getId()).delete().addOnCompleteListener(onCompleteListener);
    }

    public void Save(OnCompleteListener<DocumentReference> onCompleteListener) {
        if (getReference() == null) {
            addMember(this, onCompleteListener);
        } else {
            updateMember(this);
        }
    }

    public void Save() {
        if (getReference() == null) {
            addMember(this);
        } else {
            updateMember(this);
        }
    }

    public DocumentReference getReference() {
        if (userId == null) {
            return null;
        }
        return db.collection(collection).document(userId);
    }
}
