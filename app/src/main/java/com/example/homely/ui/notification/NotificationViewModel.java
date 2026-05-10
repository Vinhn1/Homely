package com.example.homely.ui.notification;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.homely.data.model.Notification;
import com.example.homely.data.repository.NotificationRepository;
import com.example.homely.ui.common.Resource;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class NotificationViewModel extends ViewModel { // ← extends ViewModel, không phải BaseViewModel

    private final NotificationRepository repository;
    private LiveData<Resource<List<Notification>>> notifications;

    public NotificationViewModel() {
        repository = new NotificationRepository();
    }

    public LiveData<Resource<List<Notification>>> getNotifications() {
        if (notifications == null) {
            String uid = FirebaseAuth.getInstance().getUid();
            android.util.Log.d("NotifDebug", "UID hiện tại: " + uid);
            if (uid != null) {
                notifications = repository.getNotifications(uid);
            } else {
                MutableLiveData<Resource<List<Notification>>> empty = new MutableLiveData<>();
                empty.setValue(Resource.error("Chưa đăng nhập")); // ← không truyền null
                return empty;
            }
        }
        return notifications;
    }

    public void markAsRead(String notificationId) {
        repository.markAsRead(notificationId);
    }

    public void markAllAsRead() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) repository.markAllAsRead(uid);
    }
}
