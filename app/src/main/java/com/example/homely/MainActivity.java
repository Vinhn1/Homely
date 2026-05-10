package com.example.homely;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.*;
import androidx.navigation.fragment.*;
import androidx.navigation.ui.*;

import com.example.homely.databinding.*;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private NavController navController;

    private boolean isNavigating = false; // flag chống loop

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_main);
        navController = navHostFragment.getNavController();


        // Ẩn placeholder trước
        binding.bottomNav.getMenu().findItem(R.id.placeholder).setEnabled(false);

        // Tự set listener, bỏ qua placeholder, dùng NavigationUI để navigate
        binding.bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.placeholder) return false;
            if (isNavigating) return true; // chặn loop
            return NavigationUI.onNavDestinationSelected(item, navController);
        });

        // Đồng bộ highlight BottomNav khi back stack thay đổi
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destId = destination.getId();

            if (binding.bottomNav.getMenu().findItem(destId) != null
                    && destId != R.id.placeholder) {
                isNavigating = true;
                binding.bottomNav.setSelectedItemId(destId);
                isNavigating = false;
            }
        });

        // FAB → navigate đến PostFragment
        binding.fabPost.setOnClickListener(v ->
                navController.navigate(R.id.postFragment)
        );
    }
}