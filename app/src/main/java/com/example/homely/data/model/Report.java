package com.example.homely.data.model;

import java.util.*;

// Báo cáo vi phạm về một phòng trọ từ người dùng.
// Admin sẽ xử lý và cập nhật trạng thái.
// Ánh xạ với collection "reports" trong Firestore.
public class Report {
    private String reportId;
    private String reporterId; // UID người báo cáo (ref: users)
    private String roomId; // ID phòng bị báo cáo (ref: rooms)
    private String reason; // Lý do báo cáo
    private String status; // Trạng thái "pending" (chờ) hoặc "resolved" (đã xử lý)
    private Date createdAt; // Ngày tạo báo cáo

    public Report() {
    }

    public Report(String reportId, String reporterId, String roomId, String reason, String status, Date createdAt) {
        this.reportId = reportId;
        this.reporterId = reporterId;
        this.roomId = roomId;
        this.reason = reason;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getReporterId() {
        return reporterId;
    }

    public void setReporterId(String reporterId) {
        this.reporterId = reporterId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
