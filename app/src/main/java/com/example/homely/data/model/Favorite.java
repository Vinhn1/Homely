package com.example.homely.data.model;

import java.util.*;

// Lưu thông tin yêu thích của người dùng đối với một phòng trọ.
// Mỗi document là một cặp (userId, roomId) duy nhất.
// Ánh xạ với collection "favorites" trong Firestore.
public class Favorite {
    private String favoriteId;
    private String userId; // UID người dùng (ref: users)
    private String roomId; // Id phòng yêu thích (ref: rooms)
    private Date saveAt; // Thời điểm lưu

    public Favorite() {
    }

    public Favorite(String favoriteId, String userId, String roomId, Date saveAt) {
        this.favoriteId = favoriteId;
        this.userId = userId;
        this.roomId = roomId;
        this.saveAt = saveAt;
    }

    public String getFavoriteId() {
        return favoriteId;
    }

    public void setFavoriteId(String favoriteId) {
        this.favoriteId = favoriteId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Date getSaveAt() {
        return saveAt;
    }

    public void setSaveAt(Date saveAt) {
        this.saveAt = saveAt;
    }
}
