package com.example.homely.data.remote.firebase.firestore;

import androidx.lifecycle.*;

import com.example.homely.data.model.*;
import com.example.homely.ui.common.*;
import com.google.android.gms.tasks.*;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;

import java.util.*;

/**
 * Nguồn dữ liệu Firestore cho users collection.
 */

public class FirestoreUserSource {

    private final FirebaseFirestore firestore;
    // Đặt tên thư mục (collection) là một hằng số để không bao giờ gõ sai chữ "users"
    private static final String COLLECTION_USERS = "users";

    // Constructor: Khi class này được gọi để sử dụng, nó sẽ tự động lấy kết nối database ngay lập tức
    public FirestoreUserSource(){
        firestore = FirebaseFirestore.getInstance();
    }

    /**
     * Lưu thông tin user vào Firestore.
     * @param user User object (đã có uid)
     * @return Task<Void> khi hoàn thành
     */
    public Task<Void> saveUser(User user){
        // Chốt chặn an toàn: Nếu object User đưa vào bị thiếu mã định danh (UID), báo lỗi lập tức để tránh lưu rác lên database
        if(user.getUid() == null){
            throw new IllegalArgumentException("User uid không được rỗng");
        }

        // Mở thư mục "users" -> Tìm hoặc tạo một tờ giấy (document) có tên chính là UID của user đó
        DocumentReference docRef = firestore.collection(COLLECTION_USERS).document(user.getUid());

        // Ghi toàn bộ dữ liệu của object user vào tờ giấy đó.
        // Hàm trả về 'Task' để báo cho ViewModel/Repository biết là việc cất đồ này đã xong hay bị lỗi (rớt mạng...).
        return docRef.set(user);
    }

    /**
     * Lấy thông tin user theo uid (dùng cho snapshot listener trong repository).
     * @param uid Firebase Auth UID
     * @return DocumentReference để attach listener
     */
    public DocumentReference getUserDocument(String uid){
        // Không lấy dữ liệu trực tiếp, mà chỉ trả về "tọa độ địa chỉ" của user này trên database.
        // Mục đích là để tầng Repository lấy tọa độ này và gắn một "máy lắng nghe" (SnapshotListener) vào,
        // giúp app tự động cập nhật khi user thay đổi thông tin (ví dụ: đổi avatar).
        return firestore.collection(COLLECTION_USERS).document(uid);
    }

    /**
     * Lấy thông tin user theo uid, trả về Task<User> dùng được ngay.
     * @param uid Firebase Auth UID
     * @return Task<User>
     */
    public Task<User> getUserById(String uid) {
        return firestore.collection(COLLECTION_USERS)
                .document(uid)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        return task.getResult().toObject(User.class);
                    }
                    return null;
                });
    }

    public Task<Void> updateUser(String userId, Map<String, Object> updates) {
        return firestore.collection("users")
                .document(userId)
                .update(updates);
    }


}
