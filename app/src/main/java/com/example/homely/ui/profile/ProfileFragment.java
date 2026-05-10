package com.example.homely.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.homely.AuthActivity;
import com.example.homely.R;
import com.example.homely.data.model.User;
import com.example.homely.databinding.FragmentProfileBinding;
import com.example.homely.ui.common.BaseFragment;
import com.example.homely.ui.common.Resource;

public class ProfileFragment extends BaseFragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK
                                && result.getData() != null
                                && result.getData().getData() != null) {
                            Uri selectedUri = result.getData().getData();
                            viewModel.setPendingImageUri(selectedUri);
                            Glide.with(this)
                                    .load(selectedUri)
                                    .circleCrop()
                                    .into(binding.imgAvatar);
                        }
                    }
            );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        setupObservers();
        setupClickListeners();
        viewModel.loadCurrentUser();
    }

    // ===================== Observers =====================

    private void setupObservers() {
        viewModel.getUserLiveData().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;
            switch (resource.status) {
                case LOADING:
                    showLoading(true);
                    break;
                case SUCCESS:
                    showLoading(false);
                    if (resource.data != null) populateUserData(resource.data);
                    break;
                case ERROR:
                    showLoading(false);
                    showToast("Lỗi tải dữ liệu: " + resource.message);
                    break;
            }
        });

        viewModel.getUpdateResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;
            switch (resource.status) {
                case LOADING:
                    binding.btnSave.setEnabled(false);
                    binding.btnSave.setText("Đang lưu...");
                    break;
                case SUCCESS:
                    binding.btnSave.setEnabled(true);
                    binding.btnSave.setText("Lưu thay đổi");
                    showToast("Cập nhật thành công!");
                    break;
                case ERROR:
                    binding.btnSave.setEnabled(true);
                    binding.btnSave.setText("Lưu thay đổi");
                    showToast("Lỗi: " + resource.message);
                    break;
            }
        });

        viewModel.getDeleteResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;
            switch (resource.status) {
                case LOADING:
                    showLoading(true);
                    break;
                case SUCCESS:
                    showLoading(false);
                    showToast("Tài khoản đã được xoá");
                    navigateToAuth();
                    break;
                case ERROR:
                    showLoading(false);
                    showToast("Xoá thất bại: " + resource.message);
                    break;
            }
        });

        viewModel.getIsEditMode().observe(getViewLifecycleOwner(), isEdit -> {
            if (Boolean.TRUE.equals(isEdit)) {
                switchToEditMode();
            } else {
                switchToViewMode();
            }
        });
    }

    // ===================== Click Listeners =====================

    private void setupClickListeners() {
        binding.btnEdit.setOnClickListener(v -> viewModel.toggleEditMode());

        binding.btnCancel.setOnClickListener(v -> {
            viewModel.cancelEdit();
            viewModel.loadCurrentUser();
        });

        binding.btnSave.setOnClickListener(v -> {
            String name  = binding.etName.getText()  != null ? binding.etName.getText().toString()  : "";
            String phone = binding.etPhone.getText() != null ? binding.etPhone.getText().toString() : "";
            String currentAvatarUrl = binding.imgAvatar.getTag() != null
                    ? (String) binding.imgAvatar.getTag() : "";
            viewModel.saveProfile(name, phone, currentAvatarUrl);
        });

        binding.imgAvatar.setOnClickListener(v -> {
            if (Boolean.TRUE.equals(viewModel.getIsEditMode().getValue())) {
                openImagePicker();
            }
        });

        binding.btnSignOut.setOnClickListener(v -> showSignOutDialog());

        binding.btnDeleteAccount.setOnClickListener(v -> showDeleteAccountDialog());
    }

    // ===================== UI Population =====================

    private void populateUserData(User user) {
        // Header
        binding.tvName.setText(user.getName() != null ? user.getName() : "—");
        binding.tvRole.setText(formatRole(user.getRole()));

        // Card - view mode
        binding.tvEmail.setText(user.getEmail() != null ? user.getEmail() : "—");
        binding.tvNameDisplay.setText(user.getName() != null ? user.getName() : "—");
        binding.tvPhone.setText(user.getPhone() != null ? user.getPhone() : "Chưa cập nhật");

        // Card - edit inputs (prefill)
        binding.etName.setText(user.getName());
        binding.etPhone.setText(user.getPhone());

        // Lưu avatarUrl vào tag để dùng khi save không đổi ảnh
        binding.imgAvatar.setTag(user.getAvatarUrl());

        if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
            Glide.with(this)
                    .load(user.getAvatarUrl())
                    .circleCrop()
                    .placeholder(R.drawable.ic_user)
                    .error(R.drawable.ic_user)
                    .into(binding.imgAvatar);
        } else {
            binding.imgAvatar.setImageResource(R.drawable.ic_user);
        }
    }

    // ===================== View / Edit Mode Toggle =====================

    private void switchToViewMode() {
        // Header name (luôn hiện)
        binding.tvName.setVisibility(View.VISIBLE);

        // Card: hiện view rows, ẩn input layouts
        binding.layoutViewName.setVisibility(View.VISIBLE);
        binding.layoutViewPhone.setVisibility(View.VISIBLE);
        binding.layoutEditName.setVisibility(View.GONE);
        binding.layoutEditPhone.setVisibility(View.GONE);

        // Buttons
        binding.btnEdit.setVisibility(View.VISIBLE);
        binding.btnCancel.setVisibility(View.GONE);
        binding.btnSave.setVisibility(View.GONE);

        // Avatar edit icon
        binding.iconEditAvatar.setVisibility(View.GONE);
    }

    private void switchToEditMode() {
        // Card: ẩn view rows, hiện input layouts
        binding.layoutViewName.setVisibility(View.GONE);
        binding.layoutViewPhone.setVisibility(View.GONE);
        binding.layoutEditName.setVisibility(View.VISIBLE);
        binding.layoutEditPhone.setVisibility(View.VISIBLE);

        // Buttons
        binding.btnEdit.setVisibility(View.GONE);
        binding.btnCancel.setVisibility(View.VISIBLE);
        binding.btnSave.setVisibility(View.VISIBLE);

        // Avatar edit icon
        binding.iconEditAvatar.setVisibility(View.VISIBLE);
    }

    // ===================== Dialogs =====================

    private void showSignOutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (d, w) -> {
                    viewModel.signOut();
                    navigateToAuth();
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void showDeleteAccountDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xoá tài khoản")
                .setMessage("Hành động này không thể hoàn tác. Toàn bộ dữ liệu sẽ bị xoá vĩnh viễn.")
                .setPositiveButton("Xoá tài khoản", (d, w) -> viewModel.deleteAccount())
                .setNegativeButton("Huỷ", null)
                .show();
    }

    // ===================== Helpers =====================

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void navigateToAuth() {
        Intent intent = new Intent(requireActivity(), AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private String formatRole(String role) {
        if (role == null) return "—";
        switch (role) {
            case "landlord": return "Chủ nhà trọ";
            case "tenant":   return "Người thuê";
            case "admin":    return "Quản trị viên";
            default:         return role;
        }
    }

    private void showLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.contentLayout.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}