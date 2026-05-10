package com.example.homely.data.remote.firebase.firestore;

import com.example.homely.data.model.*;
import com.google.android.gms.tasks.*;
import com.google.firebase.firestore.*;
import com.google.firebase.firestore.EventListener;

import java.util.*;

public class FirestoreRoomSource {
    private static final String COLLECTION = "rooms";
    private final FirebaseFirestore firestore;

    public FirestoreRoomSource() {
        firestore = FirebaseFirestore.getInstance();
    }

    public Task<QuerySnapshot> getRooms(RoomFilter filters) {
        Query query = firestore.collection(COLLECTION)
                .whereEqualTo("status", "available");

        if (filters != null) {
            if (filters.getPriceMin() != null)
                query = query.whereGreaterThanOrEqualTo("price", filters.getPriceMin());
            if (filters.getPriceMax() > 0)
                query = query.whereLessThanOrEqualTo("price", filters.getPriceMax());
            if (filters.getAreaMin() > 0)
                query = query.whereGreaterThanOrEqualTo("area", filters.getAreaMin());
            if (filters.getAreaMax() > 0)
                query = query.whereLessThanOrEqualTo("area", filters.getAreaMax());
            if (filters.getAddress() != null && !filters.getAddress().isEmpty())
                query = query.whereEqualTo("address", filters.getAddress());
            if (filters.getAmenities() != null && !filters.getAmenities().isEmpty())
                query = query.whereArrayContainsAny("amenities", filters.getAmenities());
        }

        return query.orderBy("createdAt", Query.Direction.DESCENDING).get();
    }

    public Task<DocumentSnapshot> getRoomById(String roomId) {
        // Fix: return null → thực sự query Firestore
        return firestore.collection(COLLECTION).document(roomId).get();
    }

    public ListenerRegistration getRoomsRealtime(EventListener<QuerySnapshot> listener) {
        return firestore.collection(COLLECTION)
                .whereEqualTo("status", "available")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener(listener);
    }

    public Task<Void> addRoom(Room room) {
        // Fix: return null → dùng set() với roomId đã có sẵn
        return firestore.collection(COLLECTION)
                .document(room.getRoomId())
                .set(room);
    }

    public Task<Void> updateRoom(String roomId, Map<String, Object> updates) {
        // Fix: return null → thực sự update Firestore
        return firestore.collection(COLLECTION)
                .document(roomId)
                .update(updates);
    }

    public Task<Void> deleteRoom(String roomId) {
        // soft-delete: chỉ đổi status thay vì xóa hẳn
        return firestore.collection(COLLECTION)
                .document(roomId)
                .update("status", "hidden");
    }

    public Task<QuerySnapshot> getRoomsByLandlord(String landlordId) {
        return firestore.collection(COLLECTION)
                .whereEqualTo("landlordId", landlordId)
                .whereNotEqualTo("status", "hidden")
                .orderBy("status")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get();
    }


    // Bài đăng của người khác
    public Task<QuerySnapshot> getRoomsExcludingLandlord(String landlordId) {
        return firestore.collection(COLLECTION)
                .whereEqualTo("status", "available")
                .whereNotEqualTo("landlordId", landlordId)
                .orderBy("landlordId")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get();
    }


}