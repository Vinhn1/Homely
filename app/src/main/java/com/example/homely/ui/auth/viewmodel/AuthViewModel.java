package com.example.homely.ui.auth.viewmodel;

import androidx.lifecycle.*;

import com.example.homely.data.model.*;
import com.example.homely.data.repository.*;
import com.example.homely.ui.common.*;
import com.google.firebase.auth.*;

/**
 * ViewModel cho màn hình đăng nhập/đăng ký.
 */
public class AuthViewModel extends ViewModel {
    // Khai báo Repository để gọi các hàm xử lý với Firebase
    private AuthRepository repository;

    public AuthViewModel(AuthRepository repository) {
        this.repository = repository;
    }

    /**
     * Đăng nhập.
     * @param email    Email
     * @param password Mật khẩu
     */
    public void login(String email, String password) {
        // TODO: call repository.login
    }

    public LiveData<Resource<User>> getLoginResult() {
        // TODO: expose loginResult LiveData
        return null;
    }

    /**
     * Đăng ký.
     * @param email    Email
     * @param password Mật khẩu
     * @param name     Họ tên
     * @param phone    Số điện thoại
     * @param role     Vai trò ("tenant" hoặc "landlord")
     */
    public void register(String email, String password, String name, String phone, String role) {
        // TODO: create User object and call repository.register
    }

    public LiveData<Resource<User>> getRegisterResult() {
        return null;
    }

    public void logout() {
        repository.logout();
    }

    /**
     * Quan sát trạng thái đăng nhập (dùng cho Splash).
     * @return LiveData<FirebaseUser>
     */
    public LiveData<FirebaseUser> observeAuthState() {
        // TODO: implement
        return null;
    }
}
