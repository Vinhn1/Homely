package com.example.homely.data.model;

import java.util.*;

// Đại diện cho một tin nhắn trong cuộc trò chuyện (subcollection của Chat).
// Lưu nội dung, người gửi và thời gian.
// Ánh xạ với subcollection "messages" trong document Chat.
public class Message {
    private String messageId;
    private String senderId; // UID người gửi (ref: users)
    private String text; // Nội dung tin nhắn
    private Date timestamp; // Thời gian gửi

    public Message() {
    }

    public Message(String messageId, String senderId, String text, Date timestamp) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
