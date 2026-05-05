package com.example.homely.data.remote.firebase.firestore;


import com.example.homely.data.model.*;
import com.google.android.gms.tasks.*;
import com.google.firebase.firestore.*;

/**
 * Nguồn dữ liệu Firestore cho yêu thích.
 */

public class FirestoreFavoriteSource {
    private final FirebaseFirestore firestore;

    public FirestoreFavoriteSource(){
        firestore = FirebaseFirestore.getInstance();
    }

    /**
     * Thêm một yêu thích mới.
     * @param favorite Đối tượng Favorite (chưa có favoriteId)
     * @return Task<Void>
     */
    public Task<Void> addFavorite(Favorite favorite){
        // TODO: implement
        return null;
    }


    /**
     * Xóa yêu thích theo favoriteId.
     * @param favoriteId ID của document yêu thích
     * @return Task<Void>
     */
    public Task<Void> removeFavorite(String favoriteId){
        // TODO: implement
        return null;
    }

    /**
     * Lấy danh sách yêu thích của một người dùng.
     * @param userId UID người dùng
     * @return Task<QuerySnapshot>
     */
    public Task<QuerySnapshot> getFavoritesByUser(String userId){
        // TODO: implement
        return null;
    }
}
