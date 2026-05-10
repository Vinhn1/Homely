package com.example.homely.data.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.*;

public class Notification {

    @DocumentId
    private String id;

    // userId nhận thông báo
    private String toUserId;

    // userId gửi (người thuê, chủ trọ, ...)
    private String fromUserId;
    private String fromUserName;
    private String fromUserAvatar;

    // Loại: "new_review", "new_message", "room_approved", "new_report"
    private String type;

    private String title;
    private String message;

    // ID tài nguyên liên quan (roomId, chatId, ...)
    private String referenceId;

    @PropertyName("read")
    private boolean isRead;

    @ServerTimestamp
    private Timestamp createdAt;

    public Notification() {}

    public Notification(String toUserId, String fromUserId, String fromUserName,
                        String fromUserAvatar, String type, String title,
                        String message, String referenceId) {
        this.toUserId = toUserId;
        this.fromUserId = fromUserId;
        this.fromUserName = fromUserName;
        this.fromUserAvatar = fromUserAvatar;
        this.type = type;
        this.title = title;
        this.message = message;
        this.referenceId = referenceId;
        this.isRead = false;
    }

    // --- Getters & Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getToUserId() { return toUserId; }
    public void setToUserId(String toUserId) { this.toUserId = toUserId; }

    public String getFromUserId() { return fromUserId; }
    public void setFromUserId(String fromUserId) { this.fromUserId = fromUserId; }

    public String getFromUserName() { return fromUserName; }
    public void setFromUserName(String fromUserName) { this.fromUserName = fromUserName; }

    public String getFromUserAvatar() { return fromUserAvatar; }
    public void setFromUserAvatar(String fromUserAvatar) { this.fromUserAvatar = fromUserAvatar; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }

    @PropertyName("read")
    public boolean isRead() { return isRead; }

    @PropertyName("read")
    public void setRead(boolean read) { isRead = read; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}