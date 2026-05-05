package com.example.homely.data.repository;

import com.example.homely.data.model.*;
import com.example.homely.data.remote.firebase.firestore.*;

/**
 * Repository cho đánh giá.
 */

public class ReviewRepository {
    private final FirestoreReviewSource reviewSource;

    public ReviewRepository(FirestoreReviewSource reviewSource) {
        this.reviewSource = reviewSource;
    }

    /**
     * Thêm đánh giá cho phòng (kiểm tra user đã thuê phòng hay chưa).
     * @param roomId ID phòng
     * @param review Đối tượng Review
     * @return LiveData<Resource<Void>>
     */
    public LiveData<Resource<Void>> addReview(String roomId, Review review) {
        // TODO: implement
        return null;
    }

    /**
     * Lấy danh sách đánh giá của phòng (realtime).
     * @param roomId ID phòng
     * @return LiveData<Resource<List<Review>>> (sắp xếp mới nhất)
     */
    public LiveData<Resource<List<Review>>> getReviewsForRoom(String roomId) {
        // TODO: implement
        return null;
    }
}
