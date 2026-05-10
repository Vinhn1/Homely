package com.example.homely.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.homely.R;
import com.example.homely.data.remote.firebase.firestore.FirestoreRoomSource;
import com.example.homely.data.remote.firebase.storage.StorageSource;
import com.example.homely.data.repository.RoomRepository;
import com.example.homely.databinding.FragmentHomeBinding;
import com.example.homely.ui.common.Resource;
import com.example.homely.ui.common.ViewModelFactory;
import com.example.homely.ui.room.RoomAdapter;
import com.example.homely.ui.room.viewmodel.RoomViewModel;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RoomViewModel roomViewModel;
    private RoomAdapter adapter;

    public HomeFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViewModel();
        setupRecyclerView();
        observeRooms();

        // Click "Đăng thêm" → navigate sang AddRoomFragment
        binding.tvSeeAll.setOnClickListener(v ->
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_home_to_addRoom));
    }

    // Khởi tạo RoomViewModel với RoomRepository
    private void setupViewModel() {
        RoomRepository repository = new RoomRepository(
                new FirestoreRoomSource(),
                new StorageSource()
        );
        Map<Class<? extends androidx.lifecycle.ViewModel>,
                androidx.lifecycle.ViewModel> creators = new HashMap<>();
        creators.put(RoomViewModel.class, new RoomViewModel(repository));

        ViewModelFactory factory = new ViewModelFactory(creators);
        roomViewModel = new ViewModelProvider(this, factory).get(RoomViewModel.class);
    }

    // Gắn adapter vào RecyclerView, xử lý click item → sang RoomDetailFragment
    private void setupRecyclerView() {
        adapter = new RoomAdapter(room -> {
            Bundle args = new Bundle();
            args.putString("roomId", room.getRoomId()); // truyền roomId sang detail
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_home_to_roomDetail, args); // dùng action, không dùng id trực tiếp
        });

        binding.rvListings.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvListings.setAdapter(adapter);
    }

    // Observe danh sách phòng từ Firestore, cập nhật adapter khi có dữ liệu
    private void observeRooms() {
        // Dùng getRoomList() thay vì getMyRooms() để test trước
        roomViewModel.getRoomList().observe(getViewLifecycleOwner(), result -> {
            android.util.Log.d("HomeFragment", "Status: " + result.status);
            if (result.data != null) {
                android.util.Log.d("HomeFragment", "Size: " + result.data.size());
            }

            if (result.status == Resource.Status.SUCCESS && result.data != null) {
                adapter.submitList(result.data);
                binding.rvListings.setVisibility(View.VISIBLE);
            } else if (result.status == Resource.Status.ERROR) {
                android.util.Log.e("HomeFragment", "Lỗi: " + result.message);
            }
        });

        // Trigger load tất cả phòng trước để test
        roomViewModel.loadRoomList(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}