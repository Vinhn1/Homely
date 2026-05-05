package com.example.homely.data.remote.firebase.firestore;


import com.google.android.gms.tasks.*;
import com.google.firebase.firestore.*;

/**
 * Nguồn dữ liệu Firestore cho admin (báo cáo, duyệt phòng).
 */

public class FirestoreAdminSource {
    private final FirebaseFirestore firestore;

    public FirestoreAdminSource() {
        firestore = FirebaseFirestore.getInstance();
    }

    /**
     * Lấy danh sách báo cáo chưa xử lý (status = 'pending').
     * @return Task<QuerySnapshot>
     */
    public Task<QuerySnapshot> getPendingReports() {
        // TODO: implement
        return null;
    }

    /**
     * Cập nhật trạng thái của báo cáo thành 'resolved'.
     * @param reportId ID báo cáo
     * @return Task<Void>
     */
    public Task<Void> resolveReport(String reportId) {
        // TODO: implement
        return null;
    }

    /**
     * Duyệt phòng: cập nhật status thành 'available'.
     * @param roomId ID phòng
     * @return Task<Void>
     */
    public Task<Void> approveRoom(String roomId) {
        // TODO: implement
        return null;
    }
}
