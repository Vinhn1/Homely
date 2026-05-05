package com.example.homely.data.model;

import com.google.firebase.firestore.*;

import java.util.*;

// Đại diện cho một phòng trọ được đăng tin trên Homely.
// Chứa thông tin chi tiết: chủ phòng, giá, diện tích, địa chỉ, tiện ích, ảnh, trạng thái.
// Ánh xạ với collection "rooms" trong Firestore.
public class Room {
    private String roomId;
    private String landlordId; // UID của chủ phòng (ref: users)
    private String title;
    private String description;
    private double price; // Giá thuê (VND/tháng)
    private double area; // Diện tích (m2)
    private String address;
    private GeoPoint location; // Tọa độ (lat, lng) dùng cho gg maps
    private List<String> amenities; // Danh sách tiện ích (wifi, gác, bảo vệ...)
    private List<String> images; // Danh sách URL ảnh từ Storage
    private String status; // Trạng thái: "vailable", "rented", "hidden"

    public Room() {
    }

    public Room(String roomId, String landlordId, String title, String description, double price, double area, String address, GeoPoint location, List<String> amenities, List<String> images, String status) {
        this.roomId = roomId;
        this.landlordId = landlordId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.area = area;
        this.address = address;
        this.location = location;
        this.amenities = amenities;
        this.images = images;
        this.status = status;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getLandlordId() {
        return landlordId;
    }

    public void setLandlordId(String landlordId) {
        this.landlordId = landlordId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
