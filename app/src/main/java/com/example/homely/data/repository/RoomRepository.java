package com.example.homely.data.repository;

import android.net.*;

import androidx.lifecycle.*;

import com.example.homely.data.model.*;
import com.example.homely.data.remote.firebase.firestore.*;
import com.example.homely.data.remote.firebase.storage.*;
import com.example.homely.ui.common.*;
import com.google.android.gms.tasks.*;
import com.google.firebase.firestore.*;

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
        MutableLiveData<Resource<List<Room>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        roomSource.getRoomsRealtime((snapshot, e) -> {
            if (e != null) {
                result.setValue(Resource.error(e.getMessage()));
                return;
            }
            if (snapshot != null) {
                List<Room> rooms = new ArrayList<>();
                for (DocumentSnapshot doc : snapshot.getDocuments()) {
                    Room room = doc.toObject(Room.class);
                    if (room != null) {
                        room.setRoomId(doc.getId()); // ← QUAN TRỌNG
                        rooms.add(room);
                    }
                }
                result.setValue(Resource.success(rooms));
            }
        });

        return result;
    }

    /**
     * Lấy chi tiết phòng realtime.
     * @param roomId ID phòng
     * @return LiveData<Resource<Room>>
     */
    public LiveData<Resource<Room>> getRoomDetail(String roomId) {
        MutableLiveData<Resource<Room>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        roomSource.getRoomById(roomId)
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Room room = doc.toObject(Room.class);
                        if (room != null) {
                            room.setRoomId(doc.getId()); // ← QUAN TRỌNG
                        }
                        result.setValue(Resource.success(room));
                    } else {
                        result.setValue(Resource.error("Không tìm thấy phòng"));
                    }
                })
                .addOnFailureListener(e -> result.setValue(Resource.error(e.getMessage())));

        return result;
    }

    /**
     * Thêm phòng mới: upload ảnh lên Storage, lấy URLs, lưu room vào Firestore.
     * @param room   Đối tượng Room (chưa có ảnh URLs)
     * @param images Danh sách URI ảnh cục bộ
     * @return LiveData<Resource<Room>> (có thể báo tiến trình upload)
     */
    public LiveData<Resource<Room>> addRoom(Room room, List<Uri> images) {
        // TODO: implement
        MutableLiveData<Resource<Room>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        if (room.getRoomId() == null || room.getRoomId().isEmpty()) {
            room.setRoomId(UUID.randomUUID().toString());
        }

        if (images == null || images.isEmpty()) {
            saveRoomToFirestore(room, result);
            return result;
        }

        List<String> uploadedUrls = new ArrayList<>();
        int[] count = {0}; // đếm số ảnh upload xong

        for (int i = 0; i < images.size(); i++) {
            storageSource.uploadImage(images.get(i), "rooms/" + room.getRoomId(),
                    new StorageSource.OnUploadCallback() {
                        @Override
                        public void onSuccess(String url) {
                            uploadedUrls.add(url);
                            count[0]++;
                            // Khi tất cả ảnh upload xong
                            if (count[0] == images.size()) {
                                room.setImages(uploadedUrls);
                                saveRoomToFirestore(room, result);
                            }
                        }

                        @Override
                        public void onError(String error) {
                            result.postValue(Resource.error("Upload ảnh thất bại: " + error));
                        }
                    });
        }

        return result;
    }

    // Phòng của người khác
    public LiveData<Resource<List<Room>>> getOtherRooms(String landlordId) {
        MutableLiveData<Resource<List<Room>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        roomSource.getRoomsExcludingLandlord(landlordId)
                .addOnSuccessListener(snapshot -> {
                    List<Room> rooms = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Room room = doc.toObject(Room.class);
                        if (room != null) {
                            room.setRoomId(doc.getId());
                            rooms.add(room);
                        }
                    }
                    result.setValue(Resource.success(rooms));
                })
                .addOnFailureListener(e ->
                        result.setValue(Resource.error(e.getMessage())));
        return result;
    }


    // helper: lưu room vào Firestore sau khi có đủ imageUrls
    private void saveRoomToFirestore(Room room, MutableLiveData<Resource<Room>> result) {
        roomSource.addRoom(room)  // ← kiểm tra trong FirestoreRoomSource đang dùng add() hay set()
                .addOnSuccessListener(v -> result.setValue(Resource.success(room)))
                .addOnFailureListener(e -> result.setValue(Resource.error(e.getMessage())));
    }
    /**
     * Cập nhật thông tin phòng.
     * @param roomId  ID phòng
     * @param updates Map các trường cần cập nhật
     * @return LiveData<Resource<Void>>
     */
    public LiveData<Resource<Void>> updateRoom(String roomId, Map<String, Object> updates) {
        // TODO: implement
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        roomSource.updateRoom(roomId, updates)
                .addOnSuccessListener(v -> result.setValue(Resource.success(null)))
                .addOnFailureListener(e -> result.setValue(Resource.error(e.getMessage())));

        return result;
    }

    /**
     * Xóa (soft-delete) phòng.
     * @param roomId ID phòng
     * @return LiveData<Resource<Void>>
     */
    public LiveData<Resource<Void>> deleteRoom(String roomId) {
        // TODO: implement
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        roomSource.deleteRoom(roomId)
                .addOnSuccessListener(v -> result.setValue(Resource.success(null)))
                .addOnFailureListener(e -> result.setValue(Resource.error(e.getMessage())));
        return result;
    }

    public LiveData<Resource<List<Room>>> getMyRooms(String landlordId) {
        MutableLiveData<Resource<List<Room>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        roomSource.getRoomsByLandlord(landlordId)
                .addOnSuccessListener(snapshot -> {
                    List<Room> rooms = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Room room = doc.toObject(Room.class);
                        if (room != null) {
                            room.setRoomId(doc.getId()); // ← thiếu dòng này
                            rooms.add(room);
                        }
                    }
                    result.setValue(Resource.success(rooms));
                })
                .addOnFailureListener(e ->
                        result.setValue(Resource.error(e.getMessage())));

        return result;
    }
}
