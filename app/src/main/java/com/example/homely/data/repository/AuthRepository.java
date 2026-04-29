package com.example.homely.data.repository;

import com.example.homely.data.remote.firebase.auth.*;
import com.google.android.gms.tasks.*;
import com.google.firebase.auth.*;

// Trung gian giữa ViewModel và Firebase (Nhận lệnh từ viewmodel và gọi FirebaseAuthSource)
public class AuthRepository {
    // Khai báo đối tượng lấy dữ liệu trực tiếp từ Firebase
    private FirebaseAuthSource authSource;

    // Hàm khởi tạo: Thiết lập kết nối với nguồn dữ liệu Firebase
    public AuthRepository(){
        this.authSource = new FirebaseAuthSource();
    }


    public Task<AuthResult> loginUser(String email, String password){
        // Gọi hàm signIn từ lớp FirebaseAuthSource để xác thực với hệ thống Firebase
        return authSource.signIn(email, password);
    }

    public Task<AuthResult> registerUser(String email, String password){
        // Gọi hàm signUp từ lớp FirebaseAuthSource để yêu cầu Firebase tạo người dùng mới
        return authSource.signUp(email, password);
    }
}
