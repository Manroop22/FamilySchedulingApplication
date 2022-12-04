package com.example.familyschedulingapplication.Models;

import com.google.firebase.firestore.DocumentReference;

import java.util.Date;

public class HomeInvite {
    String accessCode;
    String invitedByEmail;
    String invitedMemberEmail;
    String homeInviteId;
    Date acceptedAt;
    Date createdAt;
    DocumentReference homeId;
    DocumentReference invitedBy;
    DocumentReference invitedMember;
    boolean accepted;

    public HomeInvite(String accessCode, String invitedByEmail, String invitedMemberEmail, String homeInviteId, Date acceptedAt, Date createdAt, DocumentReference homeId, DocumentReference invitedBy, DocumentReference invitedMember, boolean accepted) {
        this.accessCode = accessCode;
        this.invitedByEmail = invitedByEmail;
        this.invitedMemberEmail = invitedMemberEmail;
        this.homeInviteId = homeInviteId;
        this.acceptedAt = acceptedAt;
        this.createdAt = createdAt;
        this.homeId = homeId;
        this.invitedBy = invitedBy;
        this.invitedMember = invitedMember;
        this.accepted = accepted;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public String getInvitedByEmail() {
        return invitedByEmail;
    }

    public void setInvitedByEmail(String invitedByEmail) {
        this.invitedByEmail = invitedByEmail;
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

    public Date getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(Date acceptedAt) {
        this.acceptedAt = acceptedAt;
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

    public DocumentReference getInvitedMember() {
        return invitedMember;
    }

    public void setInvitedMember(DocumentReference invitedMember) {
        this.invitedMember = invitedMember;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    @Override
    public String toString() {
        return "HomeInvite{" +
                "accessCode='" + accessCode + '\'' +
                ", invitedByEmail='" + invitedByEmail + '\'' +
                ", invitedMemberEmail='" + invitedMemberEmail + '\'' +
                ", homeInviteId='" + homeInviteId + '\'' +
                ", acceptedAt=" + acceptedAt +
                ", createdAt=" + createdAt +
                ", homeId=" + homeId +
                ", invitedBy=" + invitedBy +
                ", invitedMember=" + invitedMember +
                ", accepted=" + accepted +
                '}';
    }

}
