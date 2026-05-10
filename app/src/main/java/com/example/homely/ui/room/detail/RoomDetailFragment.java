package com.example.homely.ui.room.detail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.*;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.homely.R;
import com.example.homely.data.model.Room;
import com.example.homely.data.remote.firebase.auth.*;
import com.example.homely.data.remote.firebase.firestore.FirestoreRoomSource;
import com.example.homely.data.remote.firebase.firestore.FirestoreUserSource;
import com.example.homely.data.remote.firebase.storage.StorageSource;
import com.example.homely.data.repository.RoomRepository;
import com.example.homely.databinding.FragmentRoomDetailBinding;
import com.example.homely.ui.common.Resource;
import com.example.homely.ui.common.ViewModelFactory;
import com.example.homely.ui.room.viewmodel.RoomViewModel;
import com.google.firebase.auth.*;

import android.widget.LinearLayout;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RoomDetailFragment extends Fragment {

    private FragmentRoomDetailBinding binding;
    private RoomViewModel roomViewModel;
    private FirestoreUserSource userSource;
    private String roomId;
    private boolean isDescriptionExpanded = false;

    // Lifecycle

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRoomDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if (getArguments() != null) {
            roomId = getArguments().getString("roomId");
        }

        setupViewModel();
        setupToolbar();
        loadRoomDetail();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Setup

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

        userSource = new FirestoreUserSource();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(requireView()).navigateUp()
        );
    }

    // Load & Observe room

    private void loadRoomDetail() {
        if (roomId == null) {
            Toast.makeText(getContext(), "Không tìm thấy phòng", Toast.LENGTH_SHORT).show();
            return;
        }

        roomViewModel.getRoomDetail().observe(getViewLifecycleOwner(), result -> {
            if (result.status == Resource.Status.LOADING) {
                // TODO: hiện progressBar

            } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                // TODO: ẩn progressBar
                bindRoom(result.data);

            } else if (result.status == Resource.Status.ERROR) {
                Toast.makeText(getContext(),
                        "Lỗi: " + result.message, Toast.LENGTH_SHORT).show();
            }
        });

        roomViewModel.getRoomDetail(roomId);
    }

    // Bind Room data

    private void bindRoom(Room room) {

        // Tiêu đề
        binding.tvTitle.setText(room.getTitle());

        // Giá
        NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        binding.tvPrice.setText(fmt.format(room.getPrice()) + " đ/tháng");

        // Địa chỉ
        binding.tvAddress.setText(room.getAddress());

        // Diện tích — getArea() trả về double, cast sang int cho gọn
        binding.tvArea.setText((int) room.getArea() + " m²");

        // Kiểm tra chủ bài đăng
        String currentUid = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";
        boolean isOwner = currentUid.equals(room.getLandlordId());

        // Hiện ẩn nút onwer
        binding.llOwnerActions.setVisibility(isOwner ? View.VISIBLE : View.GONE);

        // Ẩn nút chat gọi nếu là chính mình
        binding.btnCall.setVisibility(isOwner ? View.GONE : View.VISIBLE);
        binding.btnContact.setVisibility(isOwner ? View.GONE : View.VISIBLE);
        binding.btnChat.setVisibility(isOwner ? View.GONE : View.VISIBLE);

        if (isOwner) {
            // Nút sửa → navigate sang EditRoomFragment
            binding.btnEdit.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putString("roomId", roomId);
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_roomDetailFragment_to_editRoomFragment, args);
            });

            // Nút xóa → hiện dialog xác nhận
            binding.btnDelete.setOnClickListener(v -> showDeleteDialog());
        }

        // Ngày đăng — getCreatedAt() trả về Date
        if (room.getCreatedAt() != null) {
            String dateStr = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .format(room.getCreatedAt());
            binding.tvDate.setText(dateStr);
        } else {
            binding.tvDate.setText("--");
        }

        // Trạng thái phòng
        bindStatus(room.getStatus());

        // Mô tả
        if (room.getDescription() != null && !room.getDescription().isEmpty()) {
            binding.tvDescription.setText(room.getDescription());
            setupReadMore();
        }

        // Tiện ích
        if (room.getAmenities() != null && !room.getAmenities().isEmpty()) {
            bindAmenities(room.getAmenities());
        }

        // Ảnh ViewPager2
        if (room.getImages() != null && !room.getImages().isEmpty()) {
            bindImages(room.getImages());
        }

        // Google Maps — dùng GeoPoint nếu có, fallback sang address
        binding.llOpenMap.setOnClickListener(v -> openGoogleMaps(room));

        // Nút Nhắn tin
        binding.btnContact.setOnClickListener(v -> {
            // TODO: navigate sang ChatDetailFragment
            // Bundle args = new Bundle();
            // args.putString("receiverId", room.getLandlordId());
            // Navigation.findNavController(requireView())
            //           .navigate(R.id.action_roomDetail_to_chatDetail, args);
            Toast.makeText(getContext(), "Tính năng nhắn tin", Toast.LENGTH_SHORT).show();
        });

        // Load thông tin chủ trọ (tên + số điện thoại → nút Gọi điện)
        loadLandlord(room.getLandlordId());


    }

    private void showDeleteDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Xóa bài đăng")
                .setMessage("Bạn có chắc muốn xóa bài đăng này không? Hành động này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> deleteRoom())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteRoom() {
        binding.btnDelete.setEnabled(false);
        binding.btnDelete.setText("Đang xóa...");

        roomViewModel.deleteRoom(roomId).observe(getViewLifecycleOwner(), result -> {
            if (result.status == Resource.Status.SUCCESS) {
                Toast.makeText(getContext(), "Đã xóa bài đăng", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
            } else if (result.status == Resource.Status.ERROR) {
                binding.btnDelete.setEnabled(true);
                binding.btnDelete.setText("Xóa bài");
                Toast.makeText(getContext(), "Lỗi: " + result.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //  Trạng thái phòng
    private void bindStatus(String status) {
        if (status == null) {
            binding.tvStatus.setVisibility(View.GONE);
            return;
        }
        switch (status) {
            case "available":
                binding.tvStatus.setText("● Còn trống");
                binding.tvStatus.setTextColor(0xFF2E7D32); // xanh lá
                binding.tvStatus.setVisibility(View.VISIBLE);
                break;
            case "rented":
                binding.tvStatus.setText("● Đã cho thuê");
                binding.tvStatus.setTextColor(0xFFE53935); // đỏ
                binding.tvStatus.setVisibility(View.VISIBLE);
                break;
            default:
                binding.tvStatus.setVisibility(View.GONE);
        }
    }

    //  Load chủ trọ từ Firestore

    private void loadLandlord(String landlordId) {
        if (landlordId == null || landlordId.isEmpty()) return;

        // Disable nút call trong lúc chờ
        binding.btnCall.setEnabled(false);
        binding.btnCall.setText("Đang tải...");

        userSource.getUserById(landlordId)
                .addOnSuccessListener(user -> {
                    if (user == null || binding == null) return;

                    // Tên chủ trọ — User.getName()
                    binding.tvLandlordName.setText(user.getName());

                    // Avatar — TODO khi tích hợp Glide
                    // Glide.with(this)
                    //      .load(user.getAvatarUrl())
                    //      .placeholder(R.drawable.ic_user)
                    //      .circleCrop()
                    //      .into(binding.ivLandlordAvatar);

                    // Phục hồi text nút
                    binding.btnCall.setText("Gọi điện");

                    // Số điện thoại — User.getPhone()
                    String phone = user.getPhone();
                    if (phone != null && !phone.isEmpty()) {
                        binding.btnCall.setEnabled(true);
                        binding.btnCall.setAlpha(1f);
                        binding.btnCall.setOnClickListener(v -> {
                            Intent intent = new Intent(Intent.ACTION_DIAL,
                                    Uri.parse("tel:" + phone));
                            startActivity(intent);
                        });
                    } else {
                        // Không có SĐT → disable hẳn
                        binding.btnCall.setEnabled(false);
                        binding.btnCall.setAlpha(0.5f);
                    }
                })
                .addOnFailureListener(e -> {
                    if (binding == null) return;
                    binding.btnCall.setText("Gọi điện");
                    binding.btnCall.setEnabled(false);
                    binding.btnCall.setAlpha(0.5f);
                    Toast.makeText(getContext(),
                            "Không tải được thông tin chủ trọ", Toast.LENGTH_SHORT).show();
                });
    }

    //  Google Maps

    private void openGoogleMaps(Room room) {
        Uri gmmIntentUri;

        if (room.getLocation() != null) {
            // Dùng tọa độ chính xác
            gmmIntentUri = Uri.parse(
                    "geo:" + room.getLocation().getLatitude()
                            + "," + room.getLocation().getLongitude()
                            + "?q=" + Uri.encode(room.getAddress()));
        } else {
            // Fallback: tìm theo địa chỉ
            gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(room.getAddress()));
        }

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            // Không có Google Maps → mở browser
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://maps.google.com/?q="
                            + Uri.encode(room.getAddress()))));
        }
    }

    // Xem thêm / Thu gọn mô tả

    private void setupReadMore() {
        binding.tvDescription.post(() -> {
            if (binding == null) return;
            if (binding.tvDescription.getLineCount() > 4) {
                binding.tvReadMore.setVisibility(View.VISIBLE);
            }
        });

        binding.tvReadMore.setOnClickListener(v -> {
            if (!isDescriptionExpanded) {
                binding.tvDescription.setMaxLines(Integer.MAX_VALUE);
                binding.tvDescription.setEllipsize(null);
                binding.tvReadMore.setText("Thu gọn");
            } else {
                binding.tvDescription.setMaxLines(4);
                binding.tvDescription.setEllipsize(android.text.TextUtils.TruncateAt.END);
                binding.tvReadMore.setText("Xem thêm");
            }
            isDescriptionExpanded = !isDescriptionExpanded;
        });
    }

    //  Tiện ích (FlexboxLayout)

    private void bindAmenities(List<String> amenities) {
        binding.flexboxAmenities.removeAllViews();

        for (String amenity : amenities) {
            TextView chip = new TextView(getContext());
            chip.setText(amenity);
            chip.setTextSize(12);
            chip.setTextColor(0xFF0B4FCC);
            chip.setBackgroundResource(R.drawable.bg_badge_blue);
            chip.setPadding(dp(10), dp(4), dp(10), dp(4));

            com.google.android.flexbox.FlexboxLayout.LayoutParams lp =
                    new com.google.android.flexbox.FlexboxLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
            lp.setMargins(0, 0, dp(8), dp(8));
            chip.setLayoutParams(lp);

            binding.flexboxAmenities.addView(chip);
        }
    }

    //  Ảnh ViewPager2

    private void bindImages(List<String> imageUrls) {
        // Gắn adapter vào ViewPager2
        ImagePagerAdapter pagerAdapter = new ImagePagerAdapter(imageUrls);
        binding.vpImages.setAdapter(pagerAdapter);

        // Cập nhật số ảnh "1/5"
        binding.tvImageCount.setText("1/" + imageUrls.size());

        // Lắng nghe vuốt → cập nhật số trang và indicator
        binding.vpImages.registerOnPageChangeCallback(new androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                binding.tvImageCount.setText((position + 1) + "/" + imageUrls.size());
                updateIndicators(position);
            }
        });

        // Tạo indicator dots
        setupIndicators(imageUrls.size());
    }

    // Tạo các chấm indicator
    private void setupIndicators(int count) {
        binding.llIndicators.removeAllViews();

        for (int i = 0; i < count; i++) {
            View dot = new View(getContext());
            int size = dp(8);
            int margin = dp(4);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
            lp.setMargins(margin, 0, margin, 0);
            dot.setLayoutParams(lp);

            // Dot đầu tiên active (trắng đục), còn lại mờ
            dot.setBackgroundResource(android.R.drawable.presence_online); // tạm dùng circle có sẵn
            dot.setAlpha(i == 0 ? 1f : 0.4f);

            binding.llIndicators.addView(dot);
        }
    }

    // Cập nhật dot active khi vuốt
    private void updateIndicators(int activePosition) {
        for (int i = 0; i < binding.llIndicators.getChildCount(); i++) {
            binding.llIndicators.getChildAt(i).setAlpha(i == activePosition ? 1f : 0.4f);
        }
    }

    //  Helper: dp → px

    private int dp(int value) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(value * density);
    }
}