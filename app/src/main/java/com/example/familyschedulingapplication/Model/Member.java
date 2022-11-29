package com.example.familyschedulingapplication.Model;


import android.util.Log;

import com.google.cloud.Date;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Firestore collection members
 * name, userId, homeId, email, phone, active, joinedAt
 * userId = Firestore user.uid
 */
public class Member {
    private String name;
    private String userId;
    private String homeId;
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

    public Member(String name, String userId, String homeId, String email, String phone, boolean active, Date joinedAt) {
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

    public String getHomeId() {
        return homeId;
    }

    public void setHomeId(String homeId) {
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

    public static ArrayList<Member> GetMembers(String homeId) {
        // Get members from firestore members collection where homeId == homeId
        ArrayList<Member> members = new ArrayList<>();
        db.collection(collection).whereEqualTo("homeId", "/homes/"+homeId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    Log.d("GetMembers", document.getId() + " => " + document.getData());
                    members.add(document.toObject(Member.class));
                }
            } else {
                Log.d("GetMembers", "Error getting documents: ", task.getException());
            }
        });
        return members;
    }

    public static void AddMember(Member member) {
        // Add member to firestore members collection
        db.collection(collection).add(member).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("AddMember", "Member added successfully");
            } else {
                Log.d("AddMember", "Error adding member: ", task.getException());
            }
        });
    }

    public static void UpdateMember(Member member, String item, Object value) {
        // Update member in firestore members collection
        // cast value to correct type
        switch(item) {
            case "name":
                member.setName((String) value);
                break;
            case "userId":
                member.setUserId((String) value);
                break;
            case "homeId":
                member.setHomeId((String) value);
                break;
            case "email":
                member.setEmail((String) value);
                break;
            case "phone":
                member.setPhone((String) value);
                break;
            case "active":
                member.setActive((boolean) value);
                break;
            case "joinedAt":
                member.setJoinedAt((Date) value);
                break;
        }
        db.collection(collection).document(member.getUserId()).set(member).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("UpdateMember", "Member updated successfully");
            } else {
                Log.d("UpdateMember", "Error updating member: ", task.getException());
            }
        });
    }

    public static void DeleteMember(Member member) {
        // Delete member from firestore members collection
        db.collection(collection).document(member.getUserId()).delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("DeleteMember", "Member deleted successfully");
            } else {
                Log.d("DeleteMember", "Error deleting member: ", task.getException());
            }
        });
    }

    public static void DeleteMembers(String homeId) {
        // Delete members from firestore members collection where homeId == homeId
        db.collection(collection).whereEqualTo("homeId", "/homes/"+homeId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    Log.d("DeleteMembers", document.getId() + " => " + document.getData());
                    db.collection(collection).document(document.getId()).delete().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Log.d("DeleteMembers", "Member deleted successfully");
                        } else {
                            Log.d("DeleteMembers", "Error deleting member: ", task1.getException());
                        }
                    });
                }
            } else {
                Log.d("DeleteMembers", "Error getting documents: ", task.getException());
            }
        });
    }

    public static Member GetMember(String memberId) {
        // Get member from firestore members collection where userId == memberId
        final Member[] member = {new Member()};
        db.collection(collection).whereEqualTo("userId", memberId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    Log.d("GetMember", document.getId() + " => " + document.getData());
                    member[0] = document.toObject(Member.class);
                }
            } else {
                Log.d("GetMember", "Error getting documents: ", task.getException());
            }
        });
        return member[0];
    }

    public void Save() {
        // Save member to firestore members collection
        if (this.getUserId() == null) {
            db.collection(collection).add(this).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("Save", "Member saved successfully");
                } else {
                    Log.d("Save", "Error saving member: ", task.getException());
                }
            });
        } else {
            db.collection(collection).document(this.getUserId()).set(this).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("Save", "Member saved successfully");
                } else {
                    Log.d("Save", "Error saving member: ", task.getException());
                }
            });
        }
    }

    public static Member findMember(String userId) {
        // Get member from firestore members collection where userId == userId
        final Member[] member = {new Member()};
        db.collection(collection).whereEqualTo("userId", userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    Log.d("findMember", document.getId() + " => " + document.getData());
                    member[0] = document.toObject(Member.class);
                }
            } else {
                Log.d("findMember", "Error getting documents: ", task.getException());
            }
        });
        return member[0];
    }
}
