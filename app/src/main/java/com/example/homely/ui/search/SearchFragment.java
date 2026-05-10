package com.example.homely.ui.search;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.homely.*;
import com.example.homely.data.model.Province;
import com.example.homely.data.model.Room;
import com.example.homely.data.remote.firebase.firestore.FirestoreRoomSource;
import com.example.homely.data.remote.firebase.storage.StorageSource;
import com.example.homely.data.repository.RoomRepository;
import com.example.homely.databinding.FragmentSearchBinding;
import com.example.homely.ui.common.BaseFragment;
import com.example.homely.ui.common.ViewModelFactory;
import com.example.homely.ui.room.RoomAdapter;
import com.example.homely.ui.room.viewmodel.RoomViewModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class SearchFragment extends BaseFragment {

    private static final String TAG = "SearchFragment";
    private static final String PROVINCE_API_URL = "https://provinces.open-api.vn/api/?depth=1";

    private FragmentSearchBinding binding;

    private RoomViewModel roomViewModel;
    private RoomAdapter roomAdapter;
    private List<Room> allRooms = new ArrayList<>();
    private List<Province> provinces = new ArrayList<>();

    // ── Trạng thái bộ lọc ──────────────────────────
    private String selectedProvince = null;
    private String selectedRoomType = null; // null = tất cả
    private float priceMin = 0f;            // triệu đồng
    private float priceMax = 20f;           // triệu đồng (20 = không giới hạn)

    private ExecutorService executor;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // ─────────────────────────────────────────────
    // Lifecycle
    // ─────────────────────────────────────────────

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        executor = Executors.newSingleThreadExecutor();

        initViewModel();
        setupRecyclerView();
        setupProvinceDropdown();
        setupRoomTypeFilter();
        setupPriceFilter();
        loadAllRooms();
        fetchProvinces();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
        }
        binding = null;
    }

    // ─────────────────────────────────────────────
    // ViewModel
    // ─────────────────────────────────────────────

    private void initViewModel() {
        RoomRepository repository = new RoomRepository(
                new FirestoreRoomSource(),
                new StorageSource()
        );
        RoomViewModel vm = new RoomViewModel(repository);

        Map<Class<? extends androidx.lifecycle.ViewModel>, androidx.lifecycle.ViewModel> creators = new HashMap<>();
        creators.put(RoomViewModel.class, vm);

        roomViewModel = new ViewModelProvider(this, new ViewModelFactory(creators))
                .get(RoomViewModel.class);
    }

    // ─────────────────────────────────────────────
    // RecyclerView
    // ─────────────────────────────────────────────

    private void setupRecyclerView() {
        roomAdapter = new RoomAdapter(room -> {
            Bundle args = new Bundle();
            args.putString("roomId", room.getRoomId());
            NavHostFragment.findNavController(this)
                    .navigate(R.id.roomDetailFragment, args);
        });

        binding.rvSearchResults.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvSearchResults.setAdapter(roomAdapter);
        binding.rvSearchResults.setHasFixedSize(true);
    }

    // ─────────────────────────────────────────────
    // Load phòng từ Firestore
    // ─────────────────────────────────────────────

    private void loadAllRooms() {
        showLoading(true);

        roomViewModel.getAllRooms().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            switch (resource.status) {
                case LOADING:
                    showLoading(true);
                    break;

                case SUCCESS:
                    showLoading(false);
                    allRooms = resource.data != null ? resource.data : new ArrayList<>();
                    applyFilters();
                    break;

                case ERROR:
                    showLoading(false);
                    showEmpty("Không thể tải dữ liệu. Vui lòng thử lại.");
                    Toast.makeText(requireContext(),
                            "Lỗi: " + resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    // ─────────────────────────────────────────────
    // Bộ lọc loại phòng (ChipGroup)
    // ─────────────────────────────────────────────

    private void setupRoomTypeFilter() {
        binding.chipGroupRoomType.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty() || checkedIds.contains(R.id.chip_type_all)) {
                selectedRoomType = null;
            } else {
                int id = checkedIds.get(0);
                if (id == R.id.chip_type_nha_tro)       selectedRoomType = "nhà trọ";
                else if (id == R.id.chip_type_chung_cu) selectedRoomType = "chung cư";
                else if (id == R.id.chip_type_nguyen_can) selectedRoomType = "nguyên căn";
                else if (id == R.id.chip_type_studio)   selectedRoomType = "studio";
                else                                     selectedRoomType = null;
            }
            updateClearChip();
            applyFilters();
        });
    }

    // ─────────────────────────────────────────────
    // Bộ lọc khoảng giá (RangeSlider)
    // ─────────────────────────────────────────────

    private void setupPriceFilter() {
        binding.sliderPrice.setStepSize(0.5f);
        // Hiển thị giá trị ban đầu
        updatePriceLabel(0f, 20f);

        binding.sliderPrice.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            priceMin = values.get(0);
            priceMax = values.get(1);
            updatePriceLabel(priceMin, priceMax);
            updateClearChip();
            applyFilters();
        });
    }

    private void updatePriceLabel(float min, float max) {
        if (binding == null) return;
        String minText = formatPrice(min);
        String maxText = (max >= 20f) ? "20+ triệu" : formatPrice(max);
        binding.tvPriceMin.setText(minText);
        binding.tvPriceMax.setText(maxText);
    }

    private String formatPrice(float value) {
        if (value == Math.floor(value)) {
            return (int) value + " triệu";
        }
        return value + " triệu";
    }

    // ─────────────────────────────────────────────
    // API tỉnh thành
    // ─────────────────────────────────────────────

    private void fetchProvinces() {
        executor.execute(() -> {
            List<Province> result = new ArrayList<>();
            try {
                URL url = new URL(PROVINCE_API_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(8000);
                conn.setReadTimeout(8000);

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();
                conn.disconnect();

                JSONArray arr = new JSONArray(sb.toString());
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Province p = new Province();
                    p.setCode(obj.optInt("code"));
                    p.setName(obj.optString("name"));
                    p.setCodename(obj.optString("codename"));
                    p.setDivision_type(obj.optString("division_type"));
                    p.setPhone_code(obj.optString("phone_code"));
                    result.add(p);
                }
            } catch (Exception e) {
                Log.e(TAG, "fetchProvinces error: " + e.getMessage());
            }

            mainHandler.post(() -> onProvincesLoaded(result));
        });
    }

    private void onProvincesLoaded(List<Province> result) {
        if (binding == null) return;

        provinces = result;

        ArrayAdapter<Province> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                provinces
        );
        binding.actvProvince.setAdapter(adapter);
        binding.actvProvince.setThreshold(1);

        binding.actvProvince.setOnItemClickListener((parent, v, position, id) -> {
            Province chosen = (Province) parent.getItemAtPosition(position);
            selectedProvince = chosen.getName();
            updateClearChip();
            applyFilters();
            binding.actvProvince.dismissDropDown();
        });

        binding.chipClearFilter.setOnCloseIconClickListener(v -> clearAllFilters());
    }

    // ─────────────────────────────────────────────
    // Áp dụng tất cả bộ lọc
    // ─────────────────────────────────────────────

    private void applyFilters() {
        if (binding == null) return;

        List<Room> filtered = allRooms.stream()
                .filter(this::matchesProvince)
                .filter(this::matchesRoomType)
                .filter(this::matchesPrice)
                .collect(Collectors.toList());

        updateResultCount(filtered.size());

        if (filtered.isEmpty()) {
            showEmpty(buildEmptyMessage());
        } else {
            showResults(filtered);
        }
    }

    // ── Điều kiện lọc tỉnh (chuẩn hóa tiếng Việt) ──
    private boolean matchesProvince(Room r) {
        if (selectedProvince == null || selectedProvince.isEmpty()) return true;
        return r.getAddress() != null
                && normalize(r.getAddress()).contains(normalize(selectedProvince));
    }

    // ── Điều kiện lọc loại phòng ──
    private boolean matchesRoomType(Room r) {
        if (selectedRoomType == null) return true;
        return r.getRoomType() != null
                && normalize(r.getRoomType()).contains(normalize(selectedRoomType));
    }

    // ── Điều kiện lọc giá (Room.getPrice() đơn vị: đồng) ──
    private boolean matchesPrice(Room r) {
        // Chuyển triệu → đồng để so sánh
        // Nếu getPrice() trả về triệu, bỏ phần *1_000_000
        double priceInMillions = r.getPrice() / 1_000_000.0;
        boolean aboveMin = priceInMillions >= priceMin;
        boolean belowMax = (priceMax >= 20f) || (priceInMillions <= priceMax);
        return aboveMin && belowMax;
    }

    // ── Chuẩn hóa tiếng Việt ──
    private String normalize(String input) {
        if (input == null) return "";
        String nfd = Normalizer.normalize(input, Normalizer.Form.NFD);
        return nfd.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replace("đ", "d").replace("Đ", "D")
                .toLowerCase();
    }

    // ─────────────────────────────────────────────
    // TextWatcher tỉnh thành
    // ─────────────────────────────────────────────

    private void setupProvinceDropdown() {
        binding.actvProvince.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0 && selectedProvince != null) {
                    selectedProvince = null;
                    updateClearChip();
                    applyFilters();
                }
            }
        });
    }

    // ─────────────────────────────────────────────
    // Chip "Xóa bộ lọc"
    // ─────────────────────────────────────────────

    /** Hiện chip nếu có bất kỳ bộ lọc nào đang được áp dụng */
    private void updateClearChip() {
        if (binding == null) return;
        boolean hasFilter = selectedProvince != null
                || selectedRoomType != null
                || priceMin > 0f
                || priceMax < 20f;
        binding.chipClearFilter.setVisibility(hasFilter ? View.VISIBLE : View.GONE);

        // Cập nhật text mô tả bộ lọc đang chọn
        StringBuilder sb = new StringBuilder("Đang lọc");
        if (selectedProvince != null) sb.append(": ").append(selectedProvince);
        if (selectedRoomType != null) sb.append(" · ").append(capitalize(selectedRoomType));
        if (priceMin > 0f || priceMax < 20f) {
            sb.append(" · ").append(formatPrice(priceMin))
                    .append("–").append(priceMax >= 20f ? "20+ triệu" : formatPrice(priceMax));
        }
        binding.chipClearFilter.setText(sb.toString());
    }

    private void clearAllFilters() {
        selectedProvince = null;
        selectedRoomType = null;
        priceMin = 0f;
        priceMax = 20f;

        binding.actvProvince.setText("");
        binding.chipGroupRoomType.check(R.id.chip_type_all);
        binding.sliderPrice.setValues(0f, 20f);
        updatePriceLabel(0f, 20f);
        binding.chipClearFilter.setVisibility(View.GONE);
        applyFilters();
    }

    // ─────────────────────────────────────────────
    // UI helpers
    // ─────────────────────────────────────────────

    private void updateResultCount(int count) {
        if (binding == null) return;
        boolean hasFilter = selectedProvince != null || selectedRoomType != null
                || priceMin > 0f || priceMax < 20f;
        if (!hasFilter) {
            binding.tvResultCount.setText("Tất cả bài đăng (" + allRooms.size() + ")");
        } else {
            binding.tvResultCount.setText("Tìm thấy " + count + " phòng");
        }
    }

    private String buildEmptyMessage() {
        if (selectedProvince != null) return "Không có phòng trọ tại\n" + selectedProvince;
        if (selectedRoomType != null) return "Không có " + selectedRoomType + " phù hợp";
        return "Không có phòng trọ trong khoảng giá này";
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private void showResults(List<Room> rooms) {
        binding.rvSearchResults.setVisibility(View.VISIBLE);
        binding.layoutEmpty.setVisibility(View.GONE);
        roomAdapter.submitList(new ArrayList<>(rooms));
    }

    private void showEmpty(String message) {
        binding.rvSearchResults.setVisibility(View.GONE);
        binding.layoutEmpty.setVisibility(View.VISIBLE);
        binding.tvEmptyMessage.setText(message);
    }

    private void showLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}