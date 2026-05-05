package com.example.homely.data.model;

import java.util.*;

// Lớp dùng để đóng gói các tiêu chí lọc khi tìm kiếm phòng trọ.
// Không lưu trong Firestore, chỉ sử dụng trong bộ nhớ để truyền giữa các tầng.
// Các trường có thể null nếu không áp dụng bộ lọc đó.
public class RoomFilter {
    private Double priceMin; // Giá tối thiểu (VND)
    private double priceMax; // Giá tối đa (VND)
    private double areaMin; // Diện tích tối thiểu (m2)
    private double areaMax; // Diện tích tối đa
    private String address; // Địa chỉ (tìm kiếm gần đúng)
    private List<String> amenities; // Danh sách tiện ích bắt buộc (wifi, "ac")

    public RoomFilter() {
    }

    public RoomFilter(Double priceMin, double priceMax, double areaMin, double areaMax, String address, List<String> amenities) {
        this.priceMin = priceMin;
        this.priceMax = priceMax;
        this.areaMin = areaMin;
        this.areaMax = areaMax;
        this.address = address;
        this.amenities = amenities;
    }

    public Double getPriceMin() {
        return priceMin;
    }

    public void setPriceMin(Double priceMin) {
        this.priceMin = priceMin;
    }

    public double getPriceMax() {
        return priceMax;
    }

    public void setPriceMax(double priceMax) {
        this.priceMax = priceMax;
    }

    public double getAreaMin() {
        return areaMin;
    }

    public void setAreaMin(double areaMin) {
        this.areaMin = areaMin;
    }

    public double getAreaMax() {
        return areaMax;
    }

    public void setAreaMax(double areaMax) {
        this.areaMax = areaMax;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }
}
