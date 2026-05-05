package com.example.homely.ui.room.viewmodel;

import android.net.*;

import com.example.homely.data.model.*;
import com.example.homely.data.repository.*;

import java.util.*;

/**
 * ViewModel cho danh sách phòng, chi tiết, thêm phòng, lọc.
 */

public class RoomViewModel {
    private final RoomRepository repository;
    private RoomFilter currentFilter = new RoomFilter();

    public RoomViewModel(RoomRepository repository) {
        this.repository = repository;
    }

    public void loadRoomList(RoomFilter filters) {
        // TODO: call repository.loadRooms
    }

    public LiveData<Resource<List<Room>>> getRoomList() {
        return null;
    }

    public void getRoomDetail(String roomId) {
        // TODO: call repository.getRoomDetail
    }

    public LiveData<Resource<Room>> getRoomDetail() {
        return null;
    }

    public void addRoom(Room room, List<Uri> images) {
        // TODO: call repository.addRoom
    }

    public LiveData<Resource<Room>> getAddRoomResult() {
        return null;
    }

    public void applyFilter(RoomFilter newFilter) {
        currentFilter = newFilter;
        loadRoomList(currentFilter);
    }
}
