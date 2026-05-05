package com.example.homely.data.repository;

import androidx.lifecycle.*;

import com.example.homely.data.model.*;
import com.example.homely.data.remote.firebase.auth.*;
import com.google.android.gms.tasks.*;
import com.google.firebase.auth.*;

/**
 * Repository xử lý xác thực: đăng nhập, đăng ký, logout, lấy profile.
 */
public class AuthRepository {
    // Khai báo đối tượng lấy dữ liệu trực tiếp từ Firebase
    private FirebaseAuthSource authSource;

    // Hàm khởi tạo: Thiết lập kết nối với nguồn dữ liệu Firebase
    public AuthRepository(){
        this.authSource = new FirebaseAuthSource();
    }

    /**
     * Đăng nhập bằng email/password.
     * Sau khi đăng nhập thành công, kiểm tra và lưu user vào Firestore nếu lần đầu.
     * @param email    Email
     * @param password Mật khẩu
     * @return LiveData<Resource<User>> (Loading, Success, Error)
     */
    public Task<AuthResult> loginUser(String email, String password){
        // Gọi hàm signIn từ lớp FirebaseAuthSource để xác thực với hệ thống Firebase
        return authSource.signIn(email, password);
    }

    /**
     * Đăng ký tài khoản mới, sau đó lưu thông tin user vào Firestore.
     * @param email    Email
     * @param password Mật khẩu
     * @param user     Đối tượng User (đã có name, phone, role)
     * @return LiveData<Resource<User>>
     */
    public Task<AuthResult> registerUser(String email, String password){
        // Gọi hàm signUp từ lớp FirebaseAuthSource để yêu cầu Firebase tạo người dùng mới
        return authSource.signUp(email, password);
    }

    /**
     * Đăng xuất, xóa dữ liệu cục bộ.
     */
    public void logout() {
        // TODO: implement
    }

    /**
     * Lấy profile user realtime từ Firestore (có snapshot listener).
     * @return LiveData<User> tự cập nhật khi có thay đổi
     */
    public LiveData<User> getCurrentUserProfile() {
        // TODO: implement
        return null;
    }
}
