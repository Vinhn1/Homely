package com.example.homely.data.model;

import java.util.*;

// Đại diện cho một cuộc trò chuyện giữa người thuê và chủ phòng.
// Mỗi chat liên kết với một phòng trọ cụ thể.
// Ánh xạ với collection "chats" trong Firestore.
public class Chat {
    private String chatId;
    private List<String> participants; // Danh sách UID tham gia [tenanId, landlordId]
    private String roomId; //  ID phòng liên quan (ref: rooms)
    private String lastMessage; // Nội dung tin nhắn cuối (dùng để preview)
    private Date lastMessageTime; // Thời gian tin nhắn cuối (dùng để sắp xếp)

    public Chat() {
    }

    public Chat(String chatId, List<String> participants, String roomId, String lastMessage, Date lastMessageTime) {
        this.chatId = chatId;
        this.participants = participants;
        this.roomId = roomId;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Date getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(Date lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }
}
