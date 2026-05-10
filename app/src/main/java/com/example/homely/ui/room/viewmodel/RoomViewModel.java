package com.example.homely.ui.room.viewmodel;

import android.net.*;

import androidx.lifecycle.*;
import androidx.lifecycle.Observer;

import com.example.homely.data.model.*;
import com.example.homely.data.repository.*;
import com.example.homely.ui.common.*;
import com.google.firebase.auth.*;

import java.util.*;

/**
 * ViewModel cho danh sách phòng, chi tiết, thêm phòng, lọc.
 */

public class RoomViewModel extends ViewModel{
    private final RoomRepository repository;
    private RoomFilter currentFilter = new RoomFilter();
    private LiveData<Resource<Room>> addResult;

    // LiveData nội bộ — fragment observe các field này
    private final MutableLiveData<Resource<List<Room>>> roomList = new MutableLiveData<>();
    private final MutableLiveData<Resource<Room>> roomDetail = new MutableLiveData<>();
    private final MutableLiveData<Resource<Room>> addRoomResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<Void>> updateResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<Void>> deleteResult = new MutableLiveData<>();



    public RoomViewModel(RoomRepository repository) {
        this.repository = repository;
    }



    // Load danh sách phòng
    public void loadRoomList(RoomFilter filters) {
        roomList.setValue(Resource.loading()); // báo loading ngay
        repository.loadRooms(filters).observeForever(new Observer<Resource<List<Room>>>() {
            @Override
            public void onChanged(Resource<List<Room>> resource) {
                roomList.setValue(resource);
                // tự remove sau khi nhận kết quả cuối
                if (resource.status != Resource.Status.LOADING) {
                    repository.loadRooms(filters).removeObserver(this);
                }
            }
        });
    }

    // SearchFragment
    public LiveData<Resource<List<Room>>> getOtherRooms() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return repository.getOtherRooms(uid);
    }

    // Fragment observe LiveData này để hiển thị danh sách
    public LiveData<Resource<List<Room>>> getRoomList() {
        return roomList;
    }

    // Chi tiết phòng
    public void getRoomDetail(String roomId) {
        roomDetail.setValue(Resource.loading());
        repository.getRoomDetail(roomId).observeForever(new Observer<Resource<Room>>() {
            @Override
            public void onChanged(Resource<Room> resource) {
                roomDetail.setValue(resource);
                if (resource.status != Resource.Status.LOADING) {
                    repository.getRoomDetail(roomId).removeObserver(this);
                }
            }
        });
    }

    public LiveData<Resource<Room>> getRoomDetail() {
        return roomDetail;
    }

    // Thêm phòng
    public LiveData<Resource<Room>> addRoom(Room room, List<Uri> images) {
        addResult = repository.addRoom(room, images);
        return addResult;
    }

    public LiveData<Resource<Room>> getAddRoomResult() {
        return addRoomResult;
    }

    //  Cập nhật filter và reload
    public void applyFilter(RoomFilter newFilter) {
        currentFilter = newFilter;
        // reload với filter mới
        loadRoomList(currentFilter);
    }

    // Cập nhật phòng
    public LiveData<Resource<Void>> updateRoom(String roomId, Map<String, Object> updates) {
        return repository.updateRoom(roomId, updates);
    }

    // Xóa phòng
    public LiveData<Resource<Void>> deleteRoom(String roomId) {
        return repository.deleteRoom(roomId);
    }

    public LiveData<Resource<List<Room>>> getMyRooms() {
        String uid = com.google.firebase.auth.FirebaseAuth
                .getInstance().getCurrentUser().getUid();
        return repository.getMyRooms(uid);
    }

    public LiveData<Resource<List<Room>>> getAllRooms() {
        return repository.getAllRooms();
    }

    // Cleanup khi ViewModel bị destroy
    protected void onCleared() {
        super.onCleared();
    }
}
