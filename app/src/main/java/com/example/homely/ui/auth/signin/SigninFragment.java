package com.example.homely.ui.auth.signin;

import android.content.*;
import android.os.Bundle;

import androidx.annotation.*;
import androidx.fragment.app.*;
import androidx.lifecycle.*;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.example.homely.R;
import com.example.homely.databinding.*;
import com.example.homely.ui.auth.register.*;
import com.example.homely.ui.auth.viewmodel.*;


public class SigninFragment extends Fragment {

    private FragmentSigninBinding binding;
    private AuthViewModel authViewModel;


    public SigninFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSigninBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo viewmodel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        // Lắng nghe kết quả từ viewmodel (Observe)
        observeViewModel();

        // Sự kiện Click nút đăng nhập
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.edtEmailPhone.getText().toString().trim();
            String password = binding.edtPassword.getText().toString().trim();

            if(!email.isEmpty() && !password.isEmpty()){
                authViewModel.login(email, password);
            }else {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            }
        });

        // Sự kiện click vào textview đăng ký
        binding.tvRegister.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            // Dùng R.id.fragment_container (ID của FrameLayout trong XML của bạn)
            transaction.replace(R.id.fragment_container, new RegisterFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });


    }
    // ========================================================
    private void observeViewModel() {
        // Khi đăng nhập thành công
        authViewModel.isSuccess.observe(getViewLifecycleOwner(), success -> {
            if(success){
                Toast.makeText(getContext(), "Đăng nhập thành công!", Toast.LENGTH_LONG);
                // Chuyển sang màn hình Main
                Intent intent = new Intent(getActivity(), Magnifier.class);
                // Xóa sạch bộ nhớ các màn hình trước (Login) để khi nhấn Back không quay lại nữa
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                // Đóng Activity hiện tại
                if(getActivity() != null){
                    getActivity().finish();
                }

            }
        });

        // Khi có lỗi (sai Pass, email không tồn tại)
        authViewModel.errorMessage.observe(getViewLifecycleOwner(), error -> {
            if(error != null){
                Toast.makeText(getContext(), "Lỗi: " + error, Toast.LENGTH_LONG);
            }
        });

    }
}