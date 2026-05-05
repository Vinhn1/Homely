package com.example.homely.data.remote.firebase.storage;

import android.net.*;

import com.google.android.gms.tasks.*;
import com.google.firebase.storage.*;

/**
 * Nguồn dữ liệu Firebase Storage.
 * Upload và xóa ảnh.
 */

public class StorageSource {
    private final FirebaseStorage storage;

    public StorageSource() {
        storage = FirebaseStorage.getInstance();
    }

    /**
     * Upload một ảnh lên Storage và trả về download URL.
     * @param fileUri URI của file ảnh trên thiết bị
     * @param path    Đường dẫn lưu trên Storage (ví dụ: "rooms/{roomId}/img_1.jpg")
     * @return Task<Uri> chứa URL tải xuống
     */
    public Task<Uri> uploadImage(Uri fileUri, String path) {
        // TODO: implement
        return null;
    }

    /**
     * Xóa ảnh theo download URL.
     * @param imageUrl URL của ảnh cần xóa
     * @return Task<Void>
     */
    public Task<Void> deleteImage(String imageUrl) {
        // TODO: implement
        return null;
    }
}
