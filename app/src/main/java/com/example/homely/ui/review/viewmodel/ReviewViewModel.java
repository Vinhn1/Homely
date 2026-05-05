package com.example.homely.ui.review.viewmodel;

import com.example.homely.data.model.*;
import com.example.homely.data.repository.*;

import java.util.*;

/**
 * ViewModel cho đánh giá.
 */

public class ReviewViewModel {
    private final ReviewRepository repository;

    public ReviewViewModel(ReviewRepository repository) {
        this.repository = repository;
    }

    public void loadReviews(String roomId) {
        // TODO: call repository.getReviewsForRoom
    }

    public LiveData<Resource<List<Review>>> getReviewList() {
        return null;
    }

    public void submitReview(String roomId, float rating, String comment) {
        // TODO: validate rating, create Review, call repository.addReview
    }

    public LiveData<Resource<Void>> getSubmitResult() {
        return null;
    }
}
