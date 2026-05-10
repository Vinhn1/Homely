package com.example.homely.data.remote.firebase.storage;

import android.net.*;

import com.cloudinary.android.*;
import com.cloudinary.android.callback.*;
import com.google.android.gms.tasks.*;
import com.google.firebase.storage.*;

import java.util.*;

/**
 * Nguồn dữ liệu Firebase Storage.
 * Upload và xóa ảnh.
 */

public class StorageSource {

    public interface OnUploadCallback {
        void onSuccess(String url);
        void onError(String error);
    }

    private final FirebaseStorage storage;

    public StorageSource() {
        storage = FirebaseStorage.getInstance();
    }

    /**
     * Upload một ảnh lên Storage và trả về download URL.
     * @param fileUri URI của file ảnh trên thiết bị
     * @return Task<Uri> chứa URL tải xuống
     */
    public void uploadImage(Uri fileUri, String folder, OnUploadCallback callback) {
        MediaManager.get().upload(fileUri)
                .option("folder", folder)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {}

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String url = (String) resultData.get("secure_url");
                        callback.onSuccess(url);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        callback.onError(error.getDescription());
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {}
                })
                .dispatch();
    }

    /**
     * Xóa ảnh theo download URL.
     * @param imageUrl URL của ảnh cần xóa
     * @return Task<Void>
     */
    public Task<Void> deleteImage(String imageUrl) {
        // TODO: implement
        // getReferenceFromUrl() chuyển URL → StorageReference để xoá
        return storage.getReferenceFromUrl(imageUrl).delete();
    }
}
