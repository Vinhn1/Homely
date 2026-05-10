package com.example.homely;

import android.os.Bundle;
import android.util.*;
import android.view.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.*;
import androidx.navigation.*;
import androidx.navigation.fragment.*;
import androidx.navigation.ui.*;

public class AuthActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auth);

        // Lấy NavHostFragment từ FragmentManager
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_auth);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

        } else {
            // Ghi log lỗi chi tiết để debug
            throw new IllegalStateException("NavHostFragment not found");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}