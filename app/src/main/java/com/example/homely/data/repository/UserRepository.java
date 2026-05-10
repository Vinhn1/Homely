package com.example.homely.data.repository;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.homely.data.model.User;
import com.example.homely.data.remote.firebase.auth.FirebaseAuthSource;
import com.example.homely.data.remote.firebase.firestore.FirestoreUserSource;
import com.example.homely.data.remote.firebase.storage.StorageSource;
import com.example.homely.ui.common.Resource;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

/**
 * Repository cho màn hình Profile.
 * Điều phối giữa FirestoreUserSource, StorageSource, và FirebaseAuthSource.
 */
public class UserRepository {

    private static final String TAG = "UserRepository";
    private static final String AVATAR_FOLDER = "avatars";

    private final FirestoreUserSource userSource;
    private final StorageSource storageSource;
    private final FirebaseAuthSource authSource;

    public UserRepository() {
        this.userSource = new FirestoreUserSource();
        this.storageSource = new StorageSource();
        this.authSource = new FirebaseAuthSource();
    }

    // ─────────────────────────────────────────────
    // Lấy thông tin user hiện tại
    // ─────────────────────────────────────────────

    /**
     * Lấy thông tin user đang đăng nhập từ Firestore.
     * Trả về Resource<User> bọc trạng thái Loading / Success / Error.
     */
    public LiveData<Resource<User>> getCurrentUser() {
        MutableLiveData<Resource<User>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        FirebaseUser firebaseUser = authSource.getCurrentUser();
        if (firebaseUser == null) {
            result.setValue(Resource.error("Chưa đăng nhập"));
            return result;
        }

        userSource.getUserById(firebaseUser.getUid())
                .addOnSuccessListener(user -> {
                    if (user != null) {
                        result.setValue(Resource.success(user));
                    } else {
                        result.setValue(Resource.error("Không tìm thấy thông tin người dùng"));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "getCurrentUser failed", e);
                    result.setValue(Resource.error("Lỗi tải dữ liệu: " + e.getMessage()));
                });

        return result;
    }

    // ─────────────────────────────────────────────
    // Cập nhật profile (không đổi ảnh)
    // ─────────────────────────────────────────────

    /**
     * Cập nhật tên, số điện thoại và giữ nguyên avatarUrl hiện tại.
     *
     * @param name           Tên mới
     * @param phone          Số điện thoại mới
     * @param currentAvatar  URL avatar hiện tại (không thay đổi)
     */
    public LiveData<Resource<Void>> updateProfile(String name, String phone, String currentAvatar) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        FirebaseUser firebaseUser = authSource.getCurrentUser();
        if (firebaseUser == null) {
            result.setValue(Resource.error("Chưa đăng nhập"));
            return result;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("phone", phone != null ? phone : "");
        if (currentAvatar != null && !currentAvatar.isEmpty()) {
            updates.put("avatarUrl", currentAvatar);
        }

        userSource.updateUser(firebaseUser.getUid(), updates)
                .addOnSuccessListener(aVoid -> result.setValue(Resource.success(null)))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "updateProfile failed", e);
                    result.setValue(Resource.error("Cập nhật thất bại: " + e.getMessage()));
                });

        return result;
    }

    // ─────────────────────────────────────────────
    // Cập nhật profile kèm ảnh mới
    // ─────────────────────────────────────────────

    /**
     * Upload ảnh mới lên Cloudinary, sau đó cập nhật tên, phone, avatarUrl vào Firestore.
     *
     * @param name     Tên mới
     * @param phone    Số điện thoại mới
     * @param imageUri URI ảnh mới từ bộ nhớ thiết bị
     */
    public LiveData<Resource<Void>> updateProfileWithAvatar(String name, String phone, Uri imageUri) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        FirebaseUser firebaseUser = authSource.getCurrentUser();
        if (firebaseUser == null) {
            result.setValue(Resource.error("Chưa đăng nhập"));
            return result;
        }

        // B1: Upload ảnh lên Cloudinary
        storageSource.uploadImage(imageUri, AVATAR_FOLDER, new StorageSource.OnUploadCallback() {
            @Override
            public void onSuccess(String avatarUrl) {
                // B2: Sau khi upload xong, cập nhật Firestore với URL mới
                Map<String, Object> updates = new HashMap<>();
                updates.put("name", name);
                updates.put("phone", phone != null ? phone : "");
                updates.put("avatarUrl", avatarUrl);

                userSource.updateUser(firebaseUser.getUid(), updates)
                        .addOnSuccessListener(aVoid -> result.postValue(Resource.success(null)))
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "updateUser after avatar upload failed", e);
                            result.postValue(Resource.error("Upload ảnh thành công nhưng lưu thông tin thất bại: " + e.getMessage()));
                        });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "uploadImage failed: " + error);
                result.postValue(Resource.error("Upload ảnh thất bại: " + error));
            }
        });

        return result;
    }

    // ─────────────────────────────────────────────
    // Xoá tài khoản
    // ─────────────────────────────────────────────

    /**
     * Xoá document Firestore của user, sau đó xoá tài khoản Firebase Auth.
     * Thứ tự: Firestore trước → Auth sau.
     * Lý do: Nếu xóa Auth trước, mọi thao tác Firestore tiếp theo sẽ bị từ chối (permission denied).
     */
    public LiveData<Resource<Void>> deleteAccount() {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        FirebaseUser firebaseUser = authSource.getCurrentUser();
        if (firebaseUser == null) {
            result.setValue(Resource.error("Chưa đăng nhập"));
            return result;
        }

        String uid = firebaseUser.getUid();

        // B1: Xóa dữ liệu Firestore
        Map<String, Object> deleteMarker = new HashMap<>();
        userSource.updateUser(uid, deleteMarker); // tuỳ chọn: có thể đánh dấu "deleted: true" trước

        // Xóa document
        userSource.getUserDocument(uid)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // B2: Xóa tài khoản Auth
                    firebaseUser.delete()
                            .addOnSuccessListener(unused -> result.postValue(Resource.success(null)))
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "deleteAuthAccount failed", e);
                                // Firestore đã xóa nhưng Auth chưa xóa được
                                // (thường do cần re-authenticate gần đây)
                                result.postValue(Resource.error("Xoá tài khoản thất bại. Vui lòng đăng nhập lại và thử lại: " + e.getMessage()));
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "deleteFirestoreUser failed", e);
                    result.setValue(Resource.error("Xoá dữ liệu thất bại: " + e.getMessage()));
                });

        return result;
    }

    // ─────────────────────────────────────────────
    // Đăng xuất
    // ─────────────────────────────────────────────

    /**
     * Đăng xuất khỏi Firebase Auth.
     */
    public void signOut() {
        authSource.signOut();
    }
}