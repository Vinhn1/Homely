package com.example.homely.data.remote.firebase.firestore;

import com.example.homely.data.model.*;
import com.google.android.gms.tasks.*;
import com.google.firebase.firestore.*;

import java.util.*;

public class FirestoreNotificationSource {
    private static final String COLLECTION = "notifications";
    private final FirebaseFirestore db;

    public FirestoreNotificationSource() {
        this.db = FirebaseFirestore.getInstance();
    }

    /** Lắng nghe realtime thông báo của user */
    public ListenerRegistration listenNotifications(String userId,
                                                    OnNotificationsListener listener) {
        return db.collection(COLLECTION)
                .whereEqualTo("toUserId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        listener.onError(error);
                        return;
                    }
                    if (snapshots != null) {
                        List<Notification> list = snapshots.toObjects(Notification.class);
                        listener.onNotifications(list);
                    }
                });
    }

    /** Đếm số thông báo chưa đọc */
    public Task<QuerySnapshot> getUnreadCount(String userId) {
        return db.collection(COLLECTION)
                .whereEqualTo("toUserId", userId)
                .whereEqualTo("read", false)
                .get();
    }

    /** Đánh dấu một thông báo là đã đọc */
    public Task<Void> markAsRead(String notificationId) {
        return db.collection(COLLECTION)
                .document(notificationId)
                .update("read", true);
    }

    /** Đánh dấu tất cả là đã đọc */
    public Task<Void> markAllAsRead(String userId) {
        // Dùng batch write
        return db.collection(COLLECTION)
                .whereEqualTo("toUserId", userId)
                .whereEqualTo("read", false)
                .get()
                .continueWithTask(task -> {
                    com.google.firebase.firestore.WriteBatch batch = db.batch();
                    for (var doc : task.getResult().getDocuments()) {
                        batch.update(doc.getReference(), "read", true);
                    }
                    return batch.commit();
                });
    }

    /** Gửi thông báo mới (lưu vào Firestore) */
    public Task<Void> sendNotification(Notification notification) {
        return db.collection(COLLECTION)
                .document()
                .set(notification);
    }

    public interface OnNotificationsListener {
        void onNotifications(List<Notification> notifications);
        void onError(Exception e);
    }

    public ListenerRegistration listenUnreadCount(String userId,
                                                  OnUnreadCountListener listener) {
        return db.collection(COLLECTION)
                .whereEqualTo("toUserId", userId)
                .whereEqualTo("read", false)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) return;
                    int count = snapshots != null ? snapshots.size() : 0;
                    listener.onCount(count);
                });
    }

    public interface OnUnreadCountListener {
        void onCount(int count);
    }
}
