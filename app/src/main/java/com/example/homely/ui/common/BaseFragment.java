package com.example.homely.ui.common;

import android.os.*;
import android.view.*;
import android.widget.*;

import androidx.annotation.*;
import androidx.fragment.app.*;

/**
 * Fragment cơ sở, cung cấp các hàm tiện ích hiển thị loading, toast.
 */

public abstract class BaseFragment extends Fragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // TODO: setup view binding, observe common UI states
    }

    protected void showLoading() {
        // TODO: hiển thị ProgressBar
    }

    protected void hideLoading() {
        // TODO: ẩn ProgressBar
    }

    protected void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    protected void showError(String error) {
        // TODO: hiển thị lỗi (Snackbar hoặc Toast)
        showMessage(error);
    }
}
