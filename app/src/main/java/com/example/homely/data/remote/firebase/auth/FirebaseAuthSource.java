package com.example.homely.data.remote.firebase.auth;

import com.google.android.gms.tasks.*;
import com.google.firebase.auth.*;

// Nguồn dữ liệu xác thực Firebase Auth.
// Chịu trách nhiệm gọi trực tiếp Firebase Authentication SDK.
// Các phương thức trả về Task để Repository xử lý bất đồng bộ
public class FirebaseAuthSource {
    private FirebaseAuth mAuth;

    public FirebaseAuthSource(){
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Đăng ký tài khoản mới bằng email và mật khẩu.
     * @param email    Email chưa tồn tại trong hệ thống
     * @param password Mật khẩu (tối thiểu 6 ký tự)
     * @return Task<AuthResult> chứa kết quả đăng ký hoặc lỗi (nếu email đã tồn tại)
     */
    public Task<AuthResult> signUp(String email, String password){
        return mAuth.createUserWithEmailAndPassword(email, password);
    }

    /**
     * Đăng nhập bằng email và mật khẩu.
     * @param email    Email đã đăng ký
     * @param password Mật khẩu tương ứng
     * @return Task<AuthResult> chứa kết quả đăng nhập hoặc lỗi (sai email/mật khẩu)
     */
    public Task<AuthResult> signIn(String email, String password){
        return mAuth.signInWithEmailAndPassword(email, password);
    }

    /**
     * Đăng nhập bằng tài khoản Google (sử dụng credential từ Google Sign-In).
     * @param credential AuthCredential nhận được từ GoogleSignInAccount
     * @return Task<AuthResult> chứa kết quả đăng nhập
     */
    public Task<AuthResult> signInWithGoogle(AuthCredential credential){
        return mAuth.signInWithCredential(credential);
    }


    /**
     * Gửi email đặt lại mật khẩu đến địa chỉ đã đăng ký.
     * @param email Email đã đăng ký tài khoản
     * @return Task<Void> hoàn thành khi email được gửi (có thể kiểm tra lỗi nếu email không tồn tại)
     */
    public Task<Void> sendPasswordResetEmail(String email) {
        return mAuth.sendPasswordResetEmail(email);
    }

    /**
     * Đăng xuất người dùng hiện tại.
     * Xóa session FirebaseAuth.
     */
    public void signOut(){
        mAuth.signOut();
    }

    /**
     * Lấy thông tin người dùng hiện tại nếu đã đăng nhập.
     * @return FirebaseUser nếu đã đăng nhập, ngược lại trả về null
     */
    public FirebaseUser getCurrentUser(){
        return mAuth.getCurrentUser();
    }
}
