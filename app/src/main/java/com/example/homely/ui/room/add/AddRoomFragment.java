package com.example.homely.ui.room.add;

import android.net.*;
import android.os.Bundle;

import androidx.activity.result.*;
import androidx.activity.result.contract.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.*;
import androidx.navigation.*;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.example.homely.R;
import com.example.homely.data.model.*;
import com.example.homely.data.remote.firebase.firestore.*;
import com.example.homely.data.remote.firebase.storage.*;
import com.example.homely.data.repository.*;
import com.example.homely.databinding.*;
import com.example.homely.ui.common.*;
import com.example.homely.ui.room.viewmodel.*;
import com.google.firebase.auth.*;

import java.util.*;


public class AddRoomFragment extends Fragment {
    private FragmentAddRoomBinding binding;
    private RoomViewModel roomViewModel;

    // danh sách URI ảnh người dùng chọn từ thư viện
    private final List<Uri> selectedImages = new ArrayList<>();
    private ActivityResultLauncher<PickVisualMediaRequest> imagePickerLauncher;



    public AddRoomFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Đăng ký launcher chọn nhiều ảnh
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.PickMultipleVisualMedia(5),
                uris -> {
                    if (uris != null && !uris.isEmpty()) {
                        selectedImages.clear();
                        selectedImages.addAll(uris);

                        // Hiện RecyclerView ảnh preview
                        binding.rvImages.setVisibility(View.VISIBLE);
                        // TODO: cập nhật adapter preview ảnh nếu có

                        Toast.makeText(getContext(),
                                "Đã chọn " + uris.size() + " ảnh", Toast.LENGTH_SHORT).show();
                    }
                }
        );

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddRoomBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViewModel();
        setupListeners();
    }

    // Khởi tạo ViewModel với dependencies
    private void setupViewModel() {
        RoomRepository repository = new RoomRepository(
                new FirestoreRoomSource(),
                new StorageSource()
        );

        Map<Class<? extends androidx.lifecycle.ViewModel>, androidx.lifecycle.ViewModel> creator = new HashMap<>();
        creator.put(RoomViewModel.class, new RoomViewModel(repository));

        ViewModelFactory factory = new ViewModelFactory(creator);
        roomViewModel = new ViewModelProvider(this, factory).get(RoomViewModel.class);
    }

    // Gắn sự kiện cho các view
    private void setupListeners() {
        // nút back
        binding.ivBack.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.homeFragment));

        // Chọn ảnh từ thư viện
        binding.llPickImage.setOnClickListener(v -> pickImages());

        // Nút đăng tin
        binding.btnPost.setOnClickListener(v -> submitRoom());

    }

    private void submitRoom() {
        String title = binding.etTitle.getText().toString().trim();
        String address = binding.etAddress.getText().toString().trim();
        String priceStr = binding.etPrice.getText().toString().trim();
        String areaStr = binding.etArea.getText().toString().trim();
        String description = binding.etDescription.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();

        // validate các field bắt buộc
        if (title.isEmpty()) {
            binding.etTitle.setError("Vui lòng nhập tiêu đề");
            return;
        }
        if (address.isEmpty()) {
            binding.etAddress.setError("Vui lòng nhập địa chỉ");
            return;
        }
        if (priceStr.isEmpty()) {
            binding.etPrice.setError("Vui lòng nhập giá thuê");
            return;
        }
        if (areaStr.isEmpty()) {
            binding.etArea.setError("Vui lòng nhập diện tích");
            return;
        }
        if (phone.isEmpty()) {
            binding.etPhone.setError("Vui lòng nhập số điện thoại");
            return;
        }

        double price = Double.parseDouble(priceStr);
        double area  = Double.parseDouble(areaStr);

        // gom các tiện ích được chọn
        List<String> amenities = getSelectedAmenities();

        // lấy UID người dùng hiện tại
        String landlordId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // tạo object Room
        String roomId = UUID.randomUUID().toString();
        String roomType = "nhà trọ";
        Room room = new Room(roomId, landlordId, title, description,
                price, area, address, null, amenities, null, "available", roomType);

        // observe kết quả thêm phòng
        roomViewModel.addRoom(room, selectedImages)
                .observe(getViewLifecycleOwner(), result -> {
                    if (result.status == Resource.Status.LOADING) {
                        binding.btnPost.setEnabled(false);
                        binding.btnPost.setText("Đang đăng...");
                    } else if (result.status == Resource.Status.SUCCESS) {
                        // Lưu phone vào User nếu chưa có
                        Map<String, Object> userUpdates = new HashMap<>();
                        userUpdates.put("phone", phone);
                        new FirestoreUserSource().updateUser(landlordId, userUpdates);

                        // Gửi thông báo cho tất cả user
                        sendNewPostNotification(title, roomId, landlordId);

                        Toast.makeText(getContext(), "Đăng tin thành công!", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(requireView()).navigate(R.id.homeFragment);
                    } else {
                        binding.btnPost.setEnabled(true);
                        binding.btnPost.setText("Đăng tin");
                        Toast.makeText(getContext(), "Lỗi: " + result.message, Toast.LENGTH_SHORT).show();
                    }
                });



    }

    private void sendNewPostNotification(String title, String roomId, String landlordId) {
        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("users")
                .get()
                .addOnSuccessListener(snapshots -> {
                    NotificationRepository notifRepo = new NotificationRepository();

                    for (com.google.firebase.firestore.DocumentSnapshot doc : snapshots.getDocuments()) {
                        String uid = doc.getId();

                        // Không gửi cho chính chủ trọ
                        if (uid.equals(landlordId)) continue;

                        Notification n = new Notification();
                        n.setToUserId(uid);
                        n.setType("new_post");
                        n.setTitle("Phòng trọ mới");
                        n.setMessage("Có phòng trọ mới vừa được đăng: " + title);
                        n.setReferenceId(roomId);
                        n.setRead(false);
                        n.setCreatedAt(com.google.firebase.Timestamp.now());

                        notifRepo.sendNotification(n);
                    }
                });
    }

    private List<String> getSelectedAmenities() {
        List<String> amenities = new ArrayList<>();
        if (binding.cbWifi.isChecked())     amenities.add("wifi");
        if (binding.cbAc.isChecked())       amenities.add("ac");
        if (binding.cbParking.isChecked())  amenities.add("parking");
        if (binding.cbSecurity.isChecked()) amenities.add("security");
        if (binding.cbFridge.isChecked())   amenities.add("fridge");
        if (binding.cbWasher.isChecked())   amenities.add("washer");
        if (binding.cbWc.isChecked())       amenities.add("wc");
        if (binding.cbKitchen.isChecked())  amenities.add("kitchen");
        return amenities;
    }

    private void pickImages() {
        imagePickerLauncher.launch(
                new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build()
        );
    }

    // Giải phóng binding khi fragment bị destroy
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}