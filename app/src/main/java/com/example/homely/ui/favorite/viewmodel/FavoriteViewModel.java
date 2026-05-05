package com.example.homely.ui.favorite.viewmodel;

import com.example.homely.data.model.*;
import com.example.homely.data.repository.*;

import java.util.*;

/**
 * ViewModel cho yêu thích.
 */

public class FavoriteViewModel {
    private final FavoriteRepository repository;

    public FavoriteViewModel(FavoriteRepository repository) {
        this.repository = repository;
    }

    public void toggleFavorite(String roomId, boolean isFav) {
        // TODO: if isFav true -> removeFavorite, else addFavorite
    }

    public void loadFavorites() {
        // TODO: get current user id and call repository.getFavorites
    }

    public LiveData<Resource<List<Favorite>>> getFavoriteList() {
        return null;
    }
}
