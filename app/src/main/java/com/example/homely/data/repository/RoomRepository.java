package com.example.homely.data.repository;

import android.net.*;

import com.example.homely.data.model.*;
import com.example.homely.data.remote.firebase.firestore.*;
import com.example.homely.data.remote.firebase.storage.*;

import java.util.*;

/**
 * Repository cho các thao tác liên quan đến phòng trọ.
 */

public class RoomRepository {
    private final FirestoreRoomSource roomSource;
    private final StorageSource storageSource;

    public RoomRepository(FirestoreRoomSource roomSource, StorageSource storageSource) {
        this.roomSource = roomSource;
        this.storageSource = storageSource;
    }

    /**
     * Tải danh sách phòng realtime theo bộ lọc.
     * @param filters Bộ lọc (có thể null để lấy tất cả)
     * @return LiveData<Resource<List<Room>>> (Loading, Success, Error)
     */
    public LiveData<Resource<List<Room>>> loadRooms(RoomFilter filters) {
        // TODO: implement snapshot listener
        return null;
    }

    /**
     * Lấy chi tiết phòng realtime.
     * @param roomId ID phòng
     * @return LiveData<Resource<Room>>
     */
    public LiveData<Resource<Room>> getRoomDetail(String roomId) {
        // TODO: implement
        return null;
    }

    /**
     * Thêm phòng mới: upload ảnh lên Storage, lấy URLs, lưu room vào Firestore.
     * @param room   Đối tượng Room (chưa có ảnh URLs)
     * @param images Danh sách URI ảnh cục bộ
     * @return LiveData<Resource<Room>> (có thể báo tiến trình upload)
     */
    public LiveData<Resource<Room>> addRoom(Room room, List<Uri> images) {
        // TODO: implement
        return null;
    }

    /**
     * Cập nhật thông tin phòng.
     * @param roomId  ID phòng
     * @param updates Map các trường cần cập nhật
     * @return LiveData<Resource<Void>>
     */
    public LiveData<Resource<Void>> updateRoom(String roomId, Map<String, Object> updates) {
        // TODO: implement
        return null;
    }

    /**
     * Xóa (soft-delete) phòng.
     * @param roomId ID phòng
     * @return LiveData<Resource<Void>>
     */
    public LiveData<Resource<Void>> deleteRoom(String roomId) {
        // TODO: implement
        return null;
    }
}
