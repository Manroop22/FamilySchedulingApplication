package com.example.familyschedulingapplication;

public class Message {
    private String title;
    private String msgText;
    private String NotificationType;

    public Message(String title, String msgText, String notificationType) {
        this.title = title;
        this.msgText = msgText;
        NotificationType = notificationType;
    }

    public Message(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsgText() {
        return msgText;
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }

    public String getNotificationType() {
        return NotificationType;
    }

    public void setNotificationType(String notificationType) {
        NotificationType = notificationType;
    }

    @Override
    public String toString() {
        return "Message{" +
                "title='" + title + '\'' +
                ", msgText='" + msgText + '\'' +
                ", NotificationType='" + NotificationType + '\'' +
                '}';
    }
}
