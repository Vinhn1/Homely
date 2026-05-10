package com.example.homely.ui.auth.forgotpassword;

import android.graphics.Rect;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.example.homely.data.remote.firebase.auth.*;
import com.example.homely.databinding.FragmentForgotPasswordBinding;

public class ForgotPasswordFragment extends Fragment {

    private FragmentForgotPasswordBinding binding;
    private FirebaseAuthSource authSource;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authSource = new FirebaseAuthSource();

        binding.btnSendReset.setOnClickListener(v -> sendResetEmail());
        binding.tvBackToLogin.setOnClickListener(v-> requireActivity().onBackPressed());
        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void sendResetEmail() {

        String email = binding.edtRegisterEmail.getText().toString().trim();

        if(email.isEmpty()){
            binding.tilForgotEmail.setError("Vui lòng nhập email");
            return;
        }

        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.tilForgotEmail.setError("Email không hợp lệ");
            return;
        }

        binding.tilForgotEmail.setError(null);

        binding.progressBarForgot.setVisibility(View.VISIBLE);
        binding.btnSendReset.setEnabled(false);

        authSource.sendPasswordResetEmail(email)
                .addOnSuccessListener(aVoid -> {
                    binding.progressBarForgot.setVisibility(View.GONE);
                    binding.btnSendReset.setEnabled(true);
                    Toast.makeText(getContext(), "Đã gửi email đặt lại mật khẩu. Vui lòng kiểm tra hộp thư.", Toast.LENGTH_LONG).show();
                    // Quay lại màn hình đăng nhập
                    requireActivity().onBackPressed();
                })
                .addOnFailureListener(e -> {
                    binding.progressBarForgot.setVisibility(View.GONE);
                    binding.btnSendReset.setEnabled(true);
                    String errorMsg = e.getMessage();

                    if(errorMsg.contains("no user record")){
                        Toast.makeText(getContext(), "Email chưa được đăng ký", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getContext(),"Lỗi: " +  errorMsg, Toast.LENGTH_SHORT).show();
                    }
                });
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}