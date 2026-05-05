package com.example.homely.data.model;

import java.util.Date;

// Đại diện cho người dùng trong ứng dụng homely
// Lưu trữ thông tin cá nhân, vai trò (tenant, landlord, admin) và thời gian tạo.
// Ánh xạ với collection "users" trong firestore
public class User {
    private String uid;
    private String email;
    private String name;
    private String phone;
    private String role;
    private String avatarUrl;
    private Date createdAt;


    public User() {
    }

    public User(String uid, String email, String name, String phone, String role, String avatarUrl, Date createdAt) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.role = role;
        this.avatarUrl = avatarUrl;
        this.createdAt = createdAt;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
