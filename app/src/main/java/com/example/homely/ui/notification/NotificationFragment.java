package com.example.homely.ui.notification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.homely.data.model.Notification;
import com.example.homely.databinding.FragmentNotificationBinding;
import com.example.homely.ui.common.BaseFragment;
import com.example.homely.ui.notification.NotificationAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationFragment extends BaseFragment {

    private FragmentNotificationBinding binding;
    private NotificationViewModel viewModel;
    private NotificationAdapter adapter;
    private boolean showOnlyUnread = false;
    private List<Notification> allNotifications = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNotificationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(NotificationViewModel.class);

        setupRecyclerView();
        setupTabs();
        setupMarkAllRead();
        observeData();
    }

    private void setupRecyclerView() {
        adapter = new NotificationAdapter();
        binding.rvNotifications.setLayoutManager(
                new LinearLayoutManager(requireContext()));
        binding.rvNotifications.setAdapter(adapter);

        adapter.setOnNotificationClickListener(notification -> {
            if (!notification.isRead()) {
                viewModel.markAsRead(notification.getId());
            }
            handleNotificationClick(notification);
        });
    }

    private void setupTabs() {
        binding.tabAll.setOnClickListener(v -> {
            showOnlyUnread = false;
            updateTabStyle(false);
            filterList();
        });

        binding.tabUnread.setOnClickListener(v -> {
            showOnlyUnread = true;
            updateTabStyle(true);
            filterList();
        });
    }

    private void updateTabStyle(boolean unreadSelected) {
        binding.tabAll.setAlpha(unreadSelected ? 0.5f : 1f);
        binding.tabUnread.setAlpha(unreadSelected ? 1f : 0.5f);
    }

    private void setupMarkAllRead() {
        binding.btnMarkAllRead.setOnClickListener(v -> {
            viewModel.markAllAsRead();
            Toast.makeText(requireContext(),
                    "Đã đánh dấu tất cả đã đọc", Toast.LENGTH_SHORT).show();
        });
    }

    private void observeData() {
        viewModel.getNotifications().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;
            switch (resource.status) {
                case LOADING:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    break;

                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    if (resource.data != null && !resource.data.isEmpty()) {
                        binding.layoutEmpty.setVisibility(View.GONE);
                        binding.rvNotifications.setVisibility(View.VISIBLE);
                        allNotifications = resource.data;
                        filterList();

                        long unread = resource.data.stream()
                                .filter(n -> !n.isRead()).count();
                        if (unread > 0) {
                            binding.tvUnreadCount.setVisibility(View.VISIBLE);
                            binding.tvUnreadCount.setText(String.valueOf(unread));
                        } else {
                            binding.tvUnreadCount.setVisibility(View.GONE);
                        }
                    } else {
                        binding.layoutEmpty.setVisibility(View.VISIBLE);
                        binding.rvNotifications.setVisibility(View.GONE);
                    }
                    break;

                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(),
                            resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void filterList() {
        if (showOnlyUnread) {
            adapter.submitList(allNotifications.stream()
                    .filter(n -> !n.isRead())
                    .collect(Collectors.toList()));
        } else {
            adapter.submitList(new ArrayList<>(allNotifications));
        }
    }

    private void handleNotificationClick(Notification notification) {
        if (notification.getReferenceId() == null) return;
        // TODO: navigate theo type
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}