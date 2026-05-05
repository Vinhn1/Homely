package com.example.homely.data.model;

import java.util.*;

// Đánh giá của người dùng về một phòng trọ sau khi thuê.
// Gồm điểm số (1-5 sao) và nhận xét.
// Ánh xạ với collection "reviews" trong Firestore.
public class Review {
    private String reviewId;
    private String roomId; // ID phòng được đánh giá (ref: rooms)
    private String userId; // UID người đánh giá (ref: users)
    private float rating; // Điểm từ 1.0 đến 5.0
    private String comment; // Nhận xét (không bắt buộc)
    private Date createdAt; // Ngày đánh giá

    public Review() {
    }

    public Review(String reviewId, String roomId, String userId, float rating, String comment, Date createdAt) {
        this.reviewId = reviewId;
        this.roomId = roomId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
