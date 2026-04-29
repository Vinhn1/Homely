package com.example.homely;

import android.os.Bundle;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.homely.databinding.*;
import com.example.homely.ui.auth.signin.*;
import com.google.firebase.auth.*;

// container chứa Fragment
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // 1. Khởi tạo Binding thay vì dùng setContentView(R.layout...)
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 2. Kiểm tra đăng nhập
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Hiển thị SigninFragment vào cái khung 'fragment_container'
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SigninFragment())
                    .commit();
        } else {
            // User đã đăng nhập, có thể chuyển sang HomeFragment hoặc Activity khác
            Toast.makeText(this, "Chào mừng quay trở lại!", Toast.LENGTH_SHORT).show();
        }

        // Xử lý Padding cho hệ thống (EdgeToEdge)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}