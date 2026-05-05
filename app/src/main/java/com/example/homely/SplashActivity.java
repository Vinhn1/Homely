package com.example.homely;

import android.content.*;
import android.os.*;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.homely.data.remote.firebase.auth.*;
import com.example.homely.utils.*;


// MÀN HÌNH KHỞI ĐỘNG, KIỂM TRA TRẠNG THÁI ĐĂNG NHẬP VÀ ĐIỀU HƯỚNG PHÙ HỢP

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuthSource authSource;
    private static final int SPLASH_DELAY_MS = 1500; // 1.5s


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        // Khởi tạo FirebaseAuthSource
        authSource = new FirebaseAuthSource();

        // Kiểm tra kết nối mạng
        if(!NetworkUtils.isNetworkAvailable(this)){
            Toast.makeText(this, "Không có kết nối mạng. Vui lòng kiểm tra Wi-Fi hoặc 4G!", Toast.LENGTH_LONG).show();
            // Có thể hiển thị dialog và cho phép thoát hoặc thử lại
            // Ở đây ta vẫn cho chuyển tiếp, nhưng nếu muốn chờ mạng thì thêm logic khác.
        }

        // Xác định Activity đích dựa trên trạng thái đăng nhập
        final Class<?> targetActivity;
        if(authSource.getCurrentUser() != null){
            // Đã đăng nhập -> vào màn hình chính
            targetActivity = MainActivity.class;
        }else{
            // Chưa đăng nhập -> Vào màn hình đăng nhập
            targetActivity = AuthActivity.class;
        }

        // Delay và chuyển màn hình
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, targetActivity);
            startActivity(intent);
            finish(); // Đóng SplashActivity, không cho quay lại
        }, SPLASH_DELAY_MS);
    }
}