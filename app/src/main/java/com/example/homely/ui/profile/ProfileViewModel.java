package com.example.homely.ui.profile;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.homely.data.model.User;
import com.example.homely.data.repository.UserRepository;
import com.example.homely.ui.common.Resource;

public class ProfileViewModel extends ViewModel {

    private final UserRepository userRepository;

    private final MutableLiveData<Resource<User>> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<Resource<Void>> updateResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<Void>> deleteResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isEditMode = new MutableLiveData<>(false);

    private Uri pendingImageUri = null;

    // ✅ Fix: Giữ reference đến LiveData nguồn và observer để có thể remove sau này
    private LiveData<Resource<User>> currentUserSource = null;
    private Observer<Resource<User>> currentUserObserver = null;

    private LiveData<Resource<Void>> updateSource = null;
    private Observer<Resource<Void>> updateObserver = null;

    private LiveData<Resource<Void>> deleteSource = null;
    private Observer<Resource<Void>> deleteObserver = null;

    public ProfileViewModel() {
        this.userRepository = new UserRepository();
    }

    // ===================== Getters LiveData =====================

    public LiveData<Resource<User>> getUserLiveData() { return userLiveData; }
    public LiveData<Resource<Void>> getUpdateResult() { return updateResult; }
    public LiveData<Resource<Void>> getDeleteResult() { return deleteResult; }
    public LiveData<Boolean> getIsEditMode() { return isEditMode; }
    public Uri getPendingImageUri() { return pendingImageUri; }
    public void setPendingImageUri(Uri uri) { this.pendingImageUri = uri; }

    // ===================== Actions =====================

    /**
     * Load thông tin user hiện tại từ Repository.
     * ✅ Fix: Remove observer cũ trước khi gắn observer mới → tránh memory leak
     */
    public void loadCurrentUser() {
        // Gỡ observer cũ ra nếu đang observe
        if (currentUserSource != null && currentUserObserver != null) {
            currentUserSource.removeObserver(currentUserObserver);
        }

        currentUserObserver = resource -> userLiveData.setValue(resource);
        currentUserSource = userRepository.getCurrentUser();
        currentUserSource.observeForever(currentUserObserver);
    }

    public void toggleEditMode() {
        Boolean current = isEditMode.getValue();
        isEditMode.setValue(current == null || !current);
    }

    public void setEditMode(boolean editMode) {
        isEditMode.setValue(editMode);
    }

    /**
     * Lưu profile.
     * ✅ Fix: Tương tự loadCurrentUser — remove observer cũ trước khi gắn mới
     */
    public void saveProfile(String name, String phone, String currentAvatarUrl) {
        if (name == null || name.trim().isEmpty()) {
            // ✅ Fix: Dùng đúng 1-arg Resource.error()
            updateResult.setValue(Resource.error("Tên không được để trống"));
            return;
        }

        // Gỡ observer update cũ nếu còn
        if (updateSource != null && updateObserver != null) {
            updateSource.removeObserver(updateObserver);
        }

        if (pendingImageUri != null) {
            updateSource = userRepository.updateProfileWithAvatar(name.trim(), phone, pendingImageUri);
        } else {
            updateSource = userRepository.updateProfile(name.trim(), phone, currentAvatarUrl);
        }

        updateObserver = resource -> {
            updateResult.setValue(resource);
            if (resource != null && resource.status == Resource.Status.SUCCESS) {
                pendingImageUri = null;
                isEditMode.setValue(false);
                loadCurrentUser();
            }
        };

        updateSource.observeForever(updateObserver);
    }

    /**
     * Xoá tài khoản.
     * ✅ Fix: Remove observer cũ trước khi gắn mới
     */
    public void deleteAccount() {
        if (deleteSource != null && deleteObserver != null) {
            deleteSource.removeObserver(deleteObserver);
        }

        deleteObserver = resource -> deleteResult.setValue(resource);
        deleteSource = userRepository.deleteAccount();
        deleteSource.observeForever(deleteObserver);
    }

    public void signOut() {
        userRepository.signOut();
    }

    public void cancelEdit() {
        pendingImageUri = null;
        isEditMode.setValue(false);
    }

    /**
     * ✅ Fix: Cleanup toàn bộ observeForever khi ViewModel bị destroy
     * Không có điều này → memory leak vĩnh viễn vì ViewModel giữ reference đến LiveData
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        if (currentUserSource != null && currentUserObserver != null) {
            currentUserSource.removeObserver(currentUserObserver);
        }
        if (updateSource != null && updateObserver != null) {
            updateSource.removeObserver(updateObserver);
        }
        if (deleteSource != null && deleteObserver != null) {
            deleteSource.removeObserver(deleteObserver);
        }
    }
}