package com.example.homely.ui.auth.register;

import static com.example.homely.ui.common.Resource.Status.LOADING;

import android.os.Bundle;

import androidx.annotation.*;
import androidx.core.view.*;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.*;

import android.util.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.graphics.Rect;

import com.example.homely.R;
import com.example.homely.databinding.*;
import com.example.homely.ui.auth.viewmodel.*;
import com.example.homely.ui.common.*;

/**
 * Fragment xử lý đăng ký tài khoản mới.
 * Sử dụng View Binding để truy cập các view, LiveData để quan sát kết quả đăng ký.
 */

public class RegisterFragment extends BaseFragment {
    // Binding cho layout fragment_register.xml, tự động sinh bởi View Binding
    private FragmentRegisterBinding binding;
    private AuthViewModel viewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo ViewModel (dùng chung với AuthActivity để chia sẻ dữ liệu)
        viewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        setupListeners();
        observeRegisterResult();
        setupScrollToFocusedInput();
    }

    /**
     * Tự động scroll đến ô input khi user tap vào.
     */
    private void setupScrollToFocusedInput() {
        View.OnFocusChangeListener scrollToView = (v, hasFocus) -> {
            if (!hasFocus) return;

            // Chờ bàn phím hiện lên và ScrollView resize xong
            v.postDelayed(() -> {
                if (binding == null) return;
                
                // Lấy view cha (TextInputLayout) để scroll được cả vùng input
                View viewToScroll = v;
                if (v.getParent() != null && v.getParent().getParent() instanceof com.google.android.material.textfield.TextInputLayout) {
                    viewToScroll = (View) v.getParent().getParent();
                }

                // Cuộn view vào vùng nhìn thấy của ScrollView
                Rect rect = new Rect(0, 0, viewToScroll.getWidth(), viewToScroll.getHeight() + 100);
                viewToScroll.requestRectangleOnScreen(rect, false);
            }, 500);
        };

        binding.edtRegisterUsername.setOnFocusChangeListener(scrollToView);
        binding.edtRegisterEmail.setOnFocusChangeListener(scrollToView);
        binding.edtRegisterPhone.setOnFocusChangeListener(scrollToView);
        binding.edtRegisterPassword.setOnFocusChangeListener(scrollToView);
    }

    /**
     * Gán sự kiện click cho các nút trong giao diện.
     */

    private void setupListeners() {
        // Nút đăng ký
        binding.btnRegister.setOnClickListener(v -> performRegister());
        // Link "Đã có tk? đăng nhập" -> Quay lại màn hình đăng nhập
        binding.tvLoginLink.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    /**
     * Lấy dữ liệu từ form, validate, và gọi ViewModel để đăng ký.
     */
    private void performRegister() {
        // Lấy dữ liệu từ các trường nhập liệu
        String name = binding.edtRegisterUsername.getText().toString().trim();
        String email = binding.edtRegisterEmail.getText().toString().trim();
        String phone = binding.edtRegisterPhone.getText().toString().trim();
        String password = binding.edtRegisterPassword.getText().toString().trim();

        // Reset các thông báo lỗi cũ
        binding.tilRegisterUsername.setError(null);
        binding.tilRegisterEmail.setError(null);
        binding.tilRegisterPhone.setError(null);
        binding.tilRegisterPassword.setError(null);

        // VALIDATE từng trường
        if(name.isEmpty()){
            binding.tilRegisterUsername.setError("Vui lòng nhập họ tên");
            return;
        }

        if(email.isEmpty()){
            binding.tilRegisterEmail.setError("Vui lòng nhập email");
            return;
        }

        // Kiểm tra định dạng email bằng Pattern có sẵn
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.tilRegisterEmail.setError("Email không hợp lệ!");
            return;
        }

        if(phone.isEmpty()){
            binding.tilRegisterPhone.setError("Vui lòng nhập số điện thoại");
            return;
        }

        if(password.isEmpty()){
            binding.tilRegisterPassword.setError("Vui lòng nhập mật khẩu");
            return;
        }

        if(password.length() < 6){
            binding.tilRegisterPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            return;
        }

        // Xác định vai trò: mặc định là "tenant" (người thuê)
        String role = "tenant";

        // Gọi viewmodel để thực hiện đăng ký
        viewModel.register(email, password, name, phone, role);

    }


    /**
     * Quan sát kết quả đăng ký từ ViewModel, cập nhật giao diện dựa trên trạng thái.
     * - LOADING: hiển thị ProgressBar, vô hiệu hóa nút đăng ký.
     * - SUCCESS: ẩn ProgressBar, thông báo thành công, quay lại màn hình đăng nhập.
     * - ERROR: ẩn ProgressBar, hiển thị thông báo lỗi, kích hoạt lại nút.
     */
    private void observeRegisterResult() {
        viewModel.getRegisterResult().observe(getViewLifecycleOwner(), resource -> {
            if(resource == null) return; // Tránh null pointer

            switch (resource.status){
                case LOADING:
                    // Hiển thị vòng tròn loading và khóa nút để tránh spam
                    binding.progressBarRegister.setVisibility(View.VISIBLE);
                    binding.btnRegister.setEnabled(false);
                    break;

                case SUCCESS:
                    // Đăng ký thành công -> ẩn loading, bật nút, thông báo và quay lại login
                    binding.progressBarRegister.setVisibility(View.GONE);
                    binding.btnRegister.setEnabled(true);
                    Toast.makeText(getContext(), "Đăng ký tài khoản thành công! Vui lòng đăng nhập", Toast.LENGTH_LONG).show();
                    // Quay lại màn hình đăng nhập
                    requireActivity().onBackPressed();
                    break;

                case ERROR:
                    // Có lỗi xảy ra (email tồn tại, mất mạng...)
                    binding.progressBarRegister.setVisibility(View.GONE);
                    binding.btnRegister.setEnabled(true);
                    Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                    break;

            }
        });
    }

    /**
     * Xóa binding khi fragment bị destroy để tránh memory leak.
     */
    public void onDestroyView(){
        super.onDestroyView();
        binding = null;
    }
}