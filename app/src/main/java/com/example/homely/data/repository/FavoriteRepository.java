package com.example.homely.data.repository;

import com.example.homely.data.model.*;
import com.example.homely.data.remote.firebase.firestore.*;

import java.util.*;

/**
 * Repository cho yêu thích.
 */

public class FavoriteRepository {
    private final FirestoreFavoriteSource favoriteSource;

    public FavoriteRepository(FirestoreFavoriteSource favoriteSource) {
        this.favoriteSource = favoriteSource;
    }

    /**
     * Thêm yêu thích.
     * @param userId UID người dùng
     * @param roomId ID phòng
     * @return LiveData<Resource<Void>>
     */
    public LiveData<Resource<Void>> addFavorite(String userId, String roomId) {
        // TODO: implement
        return null;
    }

    /**
     * Xóa yêu thích theo favoriteId.
     * @param favoriteId ID của document yêu thích
     * @return LiveData<Resource<Void>>
     */
    public LiveData<Resource<Void>> removeFavorite(String favoriteId) {
        // TODO: implement
        return null;
    }

    /**
     * Lấy danh sách yêu thích của người dùng (kèm thông tin phòng).
     * @param userId UID người dùng
     * @return LiveData<Resource<List<Favorite>>>
     */
    public LiveData<Resource<List<Favorite>>> getFavorites(String userId) {
        // TODO: implement
        return null;
    }
}
