package com.example.homely.ui.auth.viewmodel;

import android.util.*;

import androidx.lifecycle.*;

import com.example.homely.data.model.*;
import com.example.homely.data.repository.*;
import com.example.homely.ui.common.*;
import com.google.firebase.auth.*;

/**
 * ViewModel cho màn hình đăng nhập/đăng ký.
 * Quản lý logic giao diện, gác cổng dữ liệu trước khi đưa xuống Repository
 */
public class AuthViewModel extends ViewModel {
    // Khai báo Repository để gọi các hàm xử lý với Firebase
    private AuthRepository repository;
    
    // Kênh phát sóng nội bộ để báo kết quả cho UI (để UI biết khi nào xoay vòng vòng, khi nào hiện lỗi)
    private final MutableLiveData<Resource<User>> loginResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<User>> registerResult = new MutableLiveData<>();

    public AuthViewModel(AuthRepository repository) {
        this.repository = repository;
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
        // Validate: Kiểm tra đầu vào
        // Lỗi chỗ nào thì hiển thị lên UI chỗ đó, không cần gọi Repository
        if(email == null || email.trim().isEmpty()){
            registerResult.setValue(Resource.error("Email không được để trống"));
            return;
        }

        if(password == null || password.length() < 6){
            registerResult.setValue((Resource.error("Mật khẩu phải có ít nhất 6 ký tự")));
            return;
        }

        if(name == null || name.trim().isEmpty()){
            registerResult.setValue(Resource.error("Họ tên không được để trống"));
            return;
        }

        if(phone == null || phone.trim().isEmpty()){
            registerResult.setValue(Resource.error("Số điện thoại không được để trống"));
            return;
        }

        // Đầu vào chuẩn xác -> Đóng gói thành obj User
        User user = new User();
        user.setName(name);
        user.setPhone(phone);
        user.setRole(role); // "tenant" (người thuê) hoặc "landlord" (Chủ trọ)

        // Đưa xuống cho Repository xử lý và nhận về cái đài (LiveData) theo dõi tiến độ
        LiveData<Resource<User>> repoResult = repository.register(email, password, user);

        // Chuyển tiếp (Copy) kết quả từ đài của Repository sang đài của ViewModel để UI nghe.
        // Dùng observeForever ở đây dễ gây Memory Leak (rò rỉ bộ nhớ) nếu user bấm nút nhiều lần.
        repoResult.observeForever(resource -> {
            registerResult.setValue(resource);
            if(resource.status == Resource.Status.SUCCESS){
                Log.d("AuthViewModel", "Đăng ký thành công tài khoản: " + resource.data.getEmail());
            }else if(resource.status == Resource.Status.ERROR){
                Log.e("AuthViewModel", "Lỗi đăng ký: " + resource.message);
            }
        });

    }

    // Giao diện (Activity/Fragment) gọi hàm này để lắng nghe kết quả Đăng ký
    public LiveData<Resource<User>> getRegisterResult() {
        return registerResult;
    }


    /**
     * Đăng nhập.
     * @param email    Email
     * @param password Mật khẩu
     */
    public void login(String email, String password) {
        // TODO: call repository.login
        // Validate
        if(email == null || email.trim().isEmpty()){
            loginResult.setValue(Resource.error("Email không được để trống"));
            return;
        }

        if(password == null || password.trim().isEmpty()){
            loginResult.setValue(Resource.error("Mật khẩu không được để trống"));
            return;
        }

        // Gọi Repository xử lý đăng nhập và chuyển tiếp kết quả lên UI
        LiveData<Resource<User>> repoResult = repository.login(email, password);
        repoResult.observeForever(resource -> loginResult.setValue(resource));
    }

    // Giao diện gọi hàm này để lắng nghe kết quả Đăng nhập
    public LiveData<Resource<User>> getLoginResult() {
        // TODO: expose loginResult LiveData
        return loginResult;
    }




    public void logout() {
        // Ra lệnh cho Repository xóa phiên đăng nhập hiện tại
        repository.logout();
    }

    public LiveData<User> getCurrentUserProfile(){
        // Lấy thông tin chi tiết user (để hiện lên Header/Profile)
        return  repository.getCurrentUserProfile();
    }
    /**
     * Quan sát trạng thái đăng nhập (dùng cho Splash).
     * Hàm dùng lúc vừa mở app (Splash Screen) để xem có ai đang đăng nhập sẵn chưa
     * @return LiveData<FirebaseUser>
     */
    public LiveData<FirebaseUser> observeAuthState() {
        // TODO: implement
        MutableLiveData<FirebaseUser> authState = new MutableLiveData<>();
        // Tạm thời lấy User trực tiếp (nếu có) phát lên đài để UI chuyển thẳng vào màn hình chính
        authState.setValue(new com.example.homely.data.remote.firebase.auth.FirebaseAuthSource().getCurrentUser());
        return authState;
    }


    /**
     * Hàm nhận AuthCredential từ Fragment
     * (ví dụ credential lấy từ kết quả đăng nhập Google/Facebook)
     * và chuyển xuống Repository để xử lý.
     *
     * @param credential Đối tượng AuthCredential lấy từ Google/Facebook SDK.
     */
    public void signInWithCredential(AuthCredential credential) {
        // Gọi hàm tương ứng trong Repository
        repository.signInWithCredential(credential).observeForever(resource -> {
            // Cập nhật loginResult để Fragment lắng nghe
            loginResult.setValue(resource);
        });
    }
}
