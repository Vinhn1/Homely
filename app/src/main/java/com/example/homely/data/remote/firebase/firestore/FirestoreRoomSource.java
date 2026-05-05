package com.example.homely.data.remote.firebase.firestore;

import com.example.homely.data.model.*;
import com.google.android.gms.tasks.*;
import com.google.firebase.firestore.*;

import java.util.*;

/**
 * Nguồn dữ liệu Firestore cho phòng trọ.
 * Trả về Task (bất đồng bộ) để Repository xử lý.
 */

public class FirestoreRoomSource {
    private final FirebaseFirestore firestore;

    public FirestoreRoomSource(){
        firestore = FirebaseFirestore.getInstance();
    }

    /**
     * Truy vấn danh sách phòng theo bộ lọc.
     * @param filters Các tiêu chí lọc (giá, diện tích, tiện ích, địa chỉ)
     * @return Task<QuerySnapshot> chứa kết quả truy vấn
     */
    public Task<QuerySnapshot> getRooms(RoomFilter filters){
        // TODO: implement query with filters
        return null;
    }

    /**
     * Lấy chi tiết một phòng theo ID.
     * @param roomId ID của phòng
     * @return Task<DocumentSnapshot>
     */
    public Task<DocumentSnapshot> getRoomById(String roomId){
        // TODO: implement
        return  null;
    }

    /**
     * Thêm phòng mới vào Firestore.
     * @param room Đối tượng Room (chưa có roomId)
     * @return Task<Void> khi hoàn thành
     */
    public Task<Void> addRoom(Room room){
        // TODO: implement
        return null;
    }

    /**
     * Cập nhật các trường của phòng.
     * @param roomId  ID phòng cần cập nhật
     * @param updates Map chứa các trường và giá trị mới
     * @return Task<Void>
     */
    public Task<Void> updateRoom(String roomId, Map<String, Object> updates){
        // TODO: implement
        return null;
    }

    /**
     * Xóa (hoặc soft-delete) phòng bằng cách đặt status = "hidden".
     * @param roomId ID phòng cần xóa
     * @return Task<Void>
     */
    public Task<Void> deleteRoom(String roomId){
        // TODO: implement
        return null;
    }
}
