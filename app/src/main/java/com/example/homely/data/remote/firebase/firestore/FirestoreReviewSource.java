package com.example.homely.data.remote.firebase.firestore;

import com.example.homely.data.model.*;
import com.google.android.gms.tasks.*;
import com.google.firebase.firestore.*;

/**
 * Nguồn dữ liệu Firestore cho đánh giá.
 */

public class FirestoreReviewSource {
    private final FirebaseFirestore firestore;

    public FirestoreReviewSource() {
        firestore = FirebaseFirestore.getInstance();
    }

    /**
     * Thêm đánh giá cho phòng.
     * @param review Đối tượng Review
     * @return Task<Void>
     */
    public Task<Void> addReview(Review review) {
        // TODO: implement
        return null;
    }

    /**
     * Lấy danh sách đánh giá của một phòng, sắp xếp theo createdAt giảm dần.
     * @param roomId ID phòng
     * @return Task<QuerySnapshot>
     */
    public Task<QuerySnapshot> getReviewsForRoom(String roomId) {
        // TODO: implement
        return null;
    }
}
