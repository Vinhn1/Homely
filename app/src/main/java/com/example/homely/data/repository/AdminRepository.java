package com.example.homely.data.repository;

import com.example.homely.data.model.*;
import com.example.homely.data.remote.firebase.firestore.*;

import java.util.*;

/**
 * Repository cho admin (báo cáo, duyệt phòng).
 */

public class AdminRepository {
    private final FirestoreAdminSource adminSource;

    public AdminRepository(FirestoreAdminSource adminSource) {
        this.adminSource = adminSource;
    }

    /**
     * Lấy danh sách báo cáo chưa xử lý.
     * @return LiveData<Resource<List<Report>>>
     */
    public LiveData<Resource<List<Report>>> getReports() {
        // TODO: implement
        return null;
    }

    /**
     * Xử lý báo cáo (chuyển status thành 'resolved').
     * @param reportId ID báo cáo
     * @return LiveData<Resource<Void>>
     */
    public LiveData<Resource<Void>> resolveReport(String reportId) {
        // TODO: implement
        return null;
    }

    /**
     * Duyệt phòng (chuyển status thành 'available').
     * @param roomId ID phòng
     * @return LiveData<Resource<Void>>
     */
    public LiveData<Resource<Void>> approveRoom(String roomId) {
        // TODO: implement
        return null;
    }
}
