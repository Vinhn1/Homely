package com.example.homely.viewmodel;

import androidx.lifecycle.*;

import com.example.homely.data.repository.*;

// Lớp AuthViewModel: Cầu nối giữa giao diện (UI) và dữ liệu (Repository)
// ViewModel giúp giữ lại dữ liệu ngay cả khi điện thoại xoay màn hình.
public class AuthViewModel extends ViewModel {
    // Khai báo Repository để gọi các hàm xử lý với Firebase
    private AuthRepository repository;

    // Hiển thị trạng thái đang xử lý
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    // LiveData dùng để báo cho UI biết việc đăng nhập thành công hay chưa
    // Fragment sẽ "lắng nghe" (observe) biến này để chuyển màn hình
    public MutableLiveData<Boolean> isSuccess = new MutableLiveData<>();

    // LiveData chứa thông báo lỗi (ví dụ: "Sai mật khẩu", "Email không tồn tại")
    // Fragment sẽ lắng nghe biến này để hiển thị thông báo (Toast/Dialog) cho người dùng
    public MutableLiveData<String> errorMessage = new MutableLiveData<>();

    // Hàm khởi tạo ViewModel
    public AuthViewModel(){
        // Khởi tạo Repository để sẵn sàng sử dụng
        repository = new AuthRepository();
    }

    //=====================================================================
    // Hàm thực hiện đăng nhập
    public void login(String email, String password){
        // Gọi hàm login từ Repository và lắng nghe kết quả trả về từ Firebase
        repository.loginUser(email, password).addOnCompleteListener(task -> {
            // Kiểm tra xem phản hồi từ Firebase có thành công không
            if(task.isSuccessful()){
                // Nếu thành công, cập nhật biến isSuccess thành true
                isSuccess.setValue(true);
            }else {
                // Nếu thất bại, lấy thông báo lỗi từ Exception của Firebase và gửi về UI
                errorMessage.setValue(task.getException().getMessage());
            }
        });
    }
    //=====================================================================
    // Hàm thực hiện đăng ký
    public void register(String email, String password){
        // Bật trạng thái đang xử lý (Hiện ProgressBar trên UI)
        isLoading.setValue(true);

        repository.registerUser(email, password).addOnCompleteListener(task -> {
            // Tắt trạng thái xử lý khi có kết quả (Ẩn ProgressBar)
            isLoading.setValue(false);
           if(task.isSuccessful()){
               isSuccess.setValue(true);
           }else {
               // Lấy lỗi trực tiếp từ Firebase (vd: Email đã tồn tại)
               errorMessage.setValue(task.getException().getMessage());
           }
        });
    }
    //=====================================================================
}
