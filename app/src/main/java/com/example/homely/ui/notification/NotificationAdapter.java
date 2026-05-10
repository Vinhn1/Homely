package com.example.homely.ui.notification;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homely.R;
import com.example.homely.data.model.Notification;
import com.example.homely.databinding.ItemNotificationBinding;
import com.example.homely.utils.DateUtils;

public class NotificationAdapter extends ListAdapter<Notification, NotificationAdapter.ViewHolder> {

    private OnNotificationClickListener listener;

    public NotificationAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Notification> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Notification>() {
                @Override
                public boolean areItemsTheSame(@NonNull Notification o, @NonNull Notification n) {
                    return o.getId().equals(n.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Notification o, @NonNull Notification n) {
                    return o.isRead() == n.isRead();
                }
            };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNotificationBinding binding = ItemNotificationBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final  ItemNotificationBinding binding;

        ViewHolder(ItemNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Notification notification) {
            binding.tvTitle.setText(notification.getTitle());
            binding.tvMessage.setText(notification.getMessage());

            // Thời gian tương đối
            if (notification.getCreatedAt() != null) {
                binding.tvTime.setText(
                        DateUtils.getRelativeTime(notification.getCreatedAt().toDate()));
            }

            // Trạng thái đọc/chưa đọc
            if (notification.isRead()) {
                binding.dotUnread.setVisibility(View.INVISIBLE);
                binding.getRoot().setAlpha(0.75f);
                binding.tvTitle.setTypeface(null, Typeface.NORMAL);
            } else {
                binding.dotUnread.setVisibility(View.VISIBLE);
                binding.getRoot().setAlpha(1f);
                binding.tvTitle.setTypeface(null, Typeface.BOLD);
            }

            // Icon theo loại thông báo
            switch (notification.getType() != null ? notification.getType() : "") {
                case "new_message":
                    binding.imgTypeIcon.setImageResource(R.drawable.ic_chat);
                    break;
                case "new_review":
                    binding.imgTypeIcon.setImageResource(R.drawable.ic_heart);
                    break;
                case "new_report":
                    binding.imgTypeIcon.setImageResource(R.drawable.ic_info);
                    break;
                case "new_post":                                          // ← thêm
                    binding.imgTypeIcon.setImageResource(R.drawable.ic_home);
                    break;
                default:
                    binding.imgTypeIcon.setImageResource(R.drawable.ic_bell);
            }

            // Click
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onClick(notification);
            });
        }
    }

    public void setOnNotificationClickListener(OnNotificationClickListener listener) {
        this.listener = listener;
    }

    public interface OnNotificationClickListener {
        void onClick(Notification notification);
    }
}
