package com.example.homely.ui.room;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homely.data.model.Room;
import com.example.homely.databinding.ItemListingBinding;

import java.text.NumberFormat;
import java.util.Locale;

public class RoomAdapter extends ListAdapter<Room, RoomAdapter.RoomViewHolder> {

    // callback khi click vào item
    public interface OnRoomClickListener {
        void onRoomClick(Room room);
    }

    private final OnRoomClickListener listener;

    public RoomAdapter(OnRoomClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    // DiffUtil — chỉ redraw item thay đổi, không redraw toàn bộ list
    private static final DiffUtil.ItemCallback<Room> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Room>() {
                @Override
                public boolean areItemsTheSame(@NonNull Room a, @NonNull Room b) {
                    return a.getRoomId().equals(b.getRoomId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Room a, @NonNull Room b) {
                    return a.getTitle().equals(b.getTitle())
                            && a.getPrice() == b.getPrice()
                            && a.getAddress().equals(b.getAddress());
                }
            };

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListingBinding binding = ItemListingBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new RoomViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder {
        private final ItemListingBinding binding;

        RoomViewHolder(ItemListingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Room room, OnRoomClickListener listener) {
            // tiêu đề
            binding.tvTitle.setText(room.getTitle());

            // địa chỉ
            binding.tvAddress.setText(room.getAddress());

            // format giá: 3500000 → "3.500.000 đ/tháng"
            NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
            binding.tvPrice.setText(fmt.format(room.getPrice()) + " đ/tháng");

            // ảnh thumbnail — load ảnh đầu tiên nếu có
            if (room.getImages() != null && !room.getImages().isEmpty()) {
                // TODO: dùng Glide load ảnh — implement ở bước sau
                // Glide.with(binding.getRoot())
                //      .load(room.getImages().get(0))
                //      .into(binding.ivThumbnail);
            }

            // click item → callback
            binding.getRoot().setOnClickListener(v -> listener.onRoomClick(room));
        }
    }
}