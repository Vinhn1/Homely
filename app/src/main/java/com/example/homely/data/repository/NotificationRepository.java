package com.example.homely.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.homely.data.model.Notification;
import com.example.homely.data.remote.firebase.firestore.FirestoreNotificationSource;
import com.example.homely.ui.common.Resource;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

public class NotificationRepository {

    private final FirestoreNotificationSource source;

    public NotificationRepository() {
        this.source = new FirestoreNotificationSource();
    }

    public LiveData<Resource<List<Notification>>> getNotifications(String userId) {
        MutableLiveData<Resource<List<Notification>>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading()); // ← không truyền null

        source.listenNotifications(userId, new FirestoreNotificationSource.OnNotificationsListener() {
            @Override
            public void onNotifications(List<Notification> notifications) {
                liveData.setValue(Resource.success(notifications));
            }

            @Override
            public void onError(Exception e) {
                liveData.setValue(Resource.error(e.getMessage())); // ← không truyền null
            }
        });

        return liveData;
    }

    public LiveData<Resource<Integer>> getUnreadCount(String userId) {
        MutableLiveData<Resource<Integer>> liveData = new MutableLiveData<>();
        source.getUnreadCount(userId)
                .addOnSuccessListener(snapshots ->
                        liveData.setValue(Resource.success(snapshots.size())))
                .addOnFailureListener(e ->
                        liveData.setValue(Resource.error(e.getMessage())));
        return liveData;
    }

    public LiveData<Integer> listenUnreadCount(String userId) {
        MutableLiveData<Integer> liveData = new MutableLiveData<>();
        source.listenUnreadCount(userId, count -> liveData.setValue(count));
        return liveData;
    }

    public void markAsRead(String notificationId) {
        source.markAsRead(notificationId);
    }

    public void markAllAsRead(String userId) {
        source.markAllAsRead(userId);
    }

    public void sendNotification(Notification notification) {
        source.sendNotification(notification);
    }
}