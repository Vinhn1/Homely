package com.example.homely.ui.room.detail;

import android.os.Bundle;
import android.view.*;
import android.widget.Toast;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.example.homely.data.model.Room;
import com.example.homely.data.remote.firebase.firestore.*;
import com.example.homely.data.remote.firebase.storage.StorageSource;
import com.example.homely.data.repository.RoomRepository;
import com.example.homely.databinding.FragmentAddRoomBinding;
import com.example.homely.ui.common.*;
import com.example.homely.ui.room.viewmodel.RoomViewModel;
import java.util.*;

public class EditRoomFragment extends Fragment {
    private FragmentAddRoomBinding binding;
    private RoomViewModel roomViewModel;
    private String roomId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddRoomBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            roomId = getArguments().getString("roomId");
        }

        setupViewModel();
        loadCurrentData(); // điền sẵn data hiện tại vào form
        setupListeners();
    }

    private void setupViewModel() {
        RoomRepository repository = new RoomRepository(
                new FirestoreRoomSource(), new StorageSource());
        Map<Class<? extends androidx.lifecycle.ViewModel>,
                androidx.lifecycle.ViewModel> creators = new HashMap<>();
        creators.put(RoomViewModel.class, new RoomViewModel(repository));
        roomViewModel = new ViewModelProvider(this,
                new ViewModelFactory(creators)).get(RoomViewModel.class);
    }

    private void loadCurrentData() {
        // Bước 1: trigger load
        roomViewModel.getRoomDetail(roomId);

        // Bước 2: observe LiveData riêng
        roomViewModel.getRoomDetail().observe(getViewLifecycleOwner(), result -> {
            if (result.status == Resource.Status.SUCCESS && result.data != null) {
                fillForm(result.data);
            }
        });
    }

    private void fillForm(Room room) {
        binding.etTitle.setText(room.getTitle());
        binding.etAddress.setText(room.getAddress());
        binding.etPrice.setText(String.valueOf((int) room.getPrice()));
        binding.etArea.setText(String.valueOf((int) room.getArea()));
        binding.etDescription.setText(room.getDescription());

        if (room.getAmenities() != null) {
            binding.cbWifi.setChecked(room.getAmenities().contains("wifi"));
            binding.cbAc.setChecked(room.getAmenities().contains("ac"));
            binding.cbParking.setChecked(room.getAmenities().contains("parking"));
            binding.cbSecurity.setChecked(room.getAmenities().contains("security"));
            binding.cbFridge.setChecked(room.getAmenities().contains("fridge"));
            binding.cbWasher.setChecked(room.getAmenities().contains("washer"));
            binding.cbWc.setChecked(room.getAmenities().contains("wc"));
            binding.cbKitchen.setChecked(room.getAmenities().contains("kitchen"));
        }

        // Load phone từ User
        if (room.getLandlordId() != null) {
            new FirestoreUserSource().getUserById(room.getLandlordId())
                    .addOnSuccessListener(user -> {
                        if (user != null && binding != null) {
                            binding.etPhone.setText(user.getPhone());
                        }
                    });
        }
    }


    private void setupListeners() {
        binding.ivBack.setOnClickListener(v ->
                Navigation.findNavController(v).navigateUp());

        binding.btnPost.setOnClickListener(v -> saveChanges());
    }

    private void saveChanges() {
        String title   = binding.etTitle.getText().toString().trim();
        String address = binding.etAddress.getText().toString().trim();
        String price   = binding.etPrice.getText().toString().trim();
        String area    = binding.etArea.getText().toString().trim();
        String phone   = binding.etPhone.getText().toString().trim();

        if (title.isEmpty())   { binding.etTitle.setError("Bắt buộc"); return; }
        if (address.isEmpty()) { binding.etAddress.setError("Bắt buộc"); return; }
        if (price.isEmpty())   { binding.etPrice.setError("Bắt buộc"); return; }
        if (area.isEmpty())    { binding.etArea.setError("Bắt buộc"); return; }
        if (phone.isEmpty())   { binding.etPhone.setError("Bắt buộc"); return; }

        // Gom các field cần update
        Map<String, Object> updates = new HashMap<>();
        updates.put("title",       title);
        updates.put("address",     address);
        updates.put("price",       Double.parseDouble(price));
        updates.put("area",        Double.parseDouble(area));
        updates.put("description", binding.etDescription.getText().toString().trim());
        updates.put("amenities",   getSelectedAmenities());

        binding.btnPost.setEnabled(false);
        binding.btnPost.setText("Đang lưu...");

        roomViewModel.updateRoom(roomId, updates).observe(getViewLifecycleOwner(), result -> {
            if (result.status == Resource.Status.SUCCESS) {
                // Update phone trong User nếu có thay đổi
                String currentUid = com.google.firebase.auth.FirebaseAuth
                        .getInstance().getCurrentUser().getUid();
                Map<String, Object> userUpdates = new HashMap<>();
                userUpdates.put("phone", phone);
                new FirestoreUserSource().updateUser(currentUid, userUpdates);

                Toast.makeText(getContext(), "Đã cập nhật!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
            } else if (result.status == Resource.Status.ERROR) {
                binding.btnPost.setEnabled(true);
                binding.btnPost.setText("Lưu thay đổi");
                Toast.makeText(getContext(), "Lỗi: " + result.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<String> getSelectedAmenities() {
        List<String> list = new ArrayList<>();
        if (binding.cbWifi.isChecked())     list.add("wifi");
        if (binding.cbAc.isChecked())       list.add("ac");
        if (binding.cbParking.isChecked())  list.add("parking");
        if (binding.cbSecurity.isChecked()) list.add("security");
        if (binding.cbFridge.isChecked())   list.add("fridge");
        if (binding.cbWasher.isChecked())   list.add("washer");
        if (binding.cbWc.isChecked())       list.add("wc");
        if (binding.cbKitchen.isChecked())  list.add("kitchen");
        return list;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}