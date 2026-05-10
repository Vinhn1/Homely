package com.example.homely.data.repository;

import android.util.*;

import androidx.lifecycle.*;

import com.example.homely.data.model.*;
import com.example.homely.data.remote.firebase.auth.*;
import com.example.homely.data.remote.firebase.firestore.*;
import com.example.homely.ui.common.*;
import com.google.android.gms.tasks.*;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;

import java.util.*;

/**
 * Repository xử lý xác thực: đăng nhập, đăng ký, logout, lấy profile.
 */
public class AuthRepository {

    // Hai luồng dữ liệu
    // 1. Xử lý đăng nhập/đăng ký
    // 2. Cất giữ thông tin chi tiết (Firestore)
    private final FirebaseAuthSource authSource;
    private final FirestoreUserSource userSource;

    public AuthRepository(FirebaseAuthSource authSource, FirestoreUserSource userSource) {
        this.authSource = authSource;
        this.userSource = userSource;
        listenToCurrentUserProfile();
    }

    // LiveData dùng để lưu và phát dữ liệu profile của user hiện tại cho toàn bộ UI.
    private final MutableLiveData<User> currentUserProfile = new MutableLiveData<>();

    // Hàm khởi tạo: Thiết lập kết nối với nguồn dữ liệu Firebase
    public AuthRepository(){
        this.authSource = new FirebaseAuthSource();
        this.userSource = new FirestoreUserSource();

        // Ngay khi Repository được tạo, nó sẽ kiểm tra xem có ai đang đăng nhập không để lắng nghe dữ liệu ngay
        listenToCurrentUserProfile();
    }

    private void listenToCurrentUserProfile() {
        FirebaseUser firebaseUser = authSource.getCurrentUser();
        if(firebaseUser != null){
            // Lấy "tọa độ" của user trên db
            DocumentReference docRef = userSource.getUserDocument(firebaseUser.getUid());

            // Gắn máy lắng nghe (Realtime Update)
            docRef.addSnapshotListener((snapshot, error) -> {
                if(error != null){
                    Log.e("AuthRepository", "Listen user profile failed", error);
                    return;
                }

                if(snapshot != null && snapshot.exists()){
                    // Dịch dữ liệu thô từ Firestore thành object User trong Java
                    User user = snapshot.toObject(User.class);

                    // Đẩy dữ liệu mới nhất lên "kênh phát sóng" LiveData
                    currentUserProfile.postValue(user);
                }
            });
        }
    }


    /**
     * Đăng ký tài khoản mới, sau đó lưu thông tin user vào Firestore.
     * @param email    Email
     * @param password Mật khẩu
     * @param user     Đối tượng User (đã có name, phone, role)
     * @return LiveData<Resource<User>>
     */
    public LiveData<Resource<User>> register(String email, String password, User user){
        // Tạo một biến kết quả có bọc class Resource (để quản lý 3 trạng thái: Loading, Success, Error)
        MutableLiveData<Resource<User>> result = new MutableLiveData<>();

        // Báo cho UI biết là: "Đang xử lý nhé, hiện progress bar lên"
        result.postValue(Resource.loading());

        // B1: Gọi Auth để tạo tài khoản bằng email/password
        authSource.signUp(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();

                    if(firebaseUser == null){
                        result.postValue(Resource.error("Không thể lấy thông tin người dùng"));
                        return;
                    }

                    // B2: Nếu Auth thành công, lấy UID do Auth cấp để gán vào obj User
                    String uid = firebaseUser.getUid();
                    user.setUid(uid);
                    user.setEmail(email);
                    user.setCreatedAt(new Date());

                    // B3. Lưu obj User (đã có đủ UID, Email) lên Firestore
                    userSource.saveUser(user)
                            .addOnSuccessListener(aVoid -> {
                                // Thành công -> Báo success kèm thông tin user
                                result.postValue(Resource.success(user));
                            })
                            .addOnFailureListener(e -> {
                                // Auth tạo được, nhưng không cất vào db đc
                                result.postValue(Resource.error("Lưu thông tin thất bại: " + e.getMessage()));
                            });

                })
                .addOnFailureListener(e -> {
                    // Lỗi ngay từ vòng gửi xe (Ví dụ: email đã tồn tại, mật khẩu quá ngắn...)
                    result.postValue(Resource.error("Đăng ký thất bại: " + e.getMessage()));
                });

        // Trả về LiveData ngay lập tức
        return result;
    }



    /**
     * Đăng nhập bằng email/password.
     * Sau khi đăng nhập thành công, kiểm tra và lưu user vào Firestore nếu lần đầu.
     * @param email    Email
     * @param password Mật khẩu
     * @return LiveData<Resource<User>> (Loading, Success, Error)
     */
    public LiveData<Resource<User>> login(String email, String password){

        MutableLiveData<Resource<User>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());

        // B1. Gọi Auth để kiểm tra xem email/pass có đúng không
        authSource.signIn(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();

                    if(firebaseUser == null){
                        result.postValue(Resource.error("Không thể lấy thông tin người dùng"));
                        return;
                    }

                    String uid = firebaseUser.getUid();

                    // B2. Auth đúng, vào Firestore để lấy thông tin chi tiết (tên, avatar)
                    userSource.getUserDocument(uid).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if(documentSnapshot.exists()){
                                    // Có dữ liệu -> Đăng nhập hoàn toàn thành công
                                    User user = documentSnapshot.toObject(User.class);
                                    result.postValue(Resource.success(user));
                                }else {
                                    // Nếu bên Auth có tài khoản nhưng bên Database lại mất tích (có thể do lỗi mạng lúc đăng ký trước đó)
                                    // Ta chủ động tạo lại một object trống và nhét vào database để đồng bộ dữ liệu.
                                    User newUser = new User();
                                    newUser.setUid(uid);
                                    newUser.setEmail(email);
                                    newUser.setCreatedAt(new Date());

                                    userSource.saveUser(newUser)
                                            .addOnSuccessListener(aVoid -> result.postValue(Resource.success(newUser)))
                                            .addOnFailureListener(e -> result.postValue(Resource.error("Lỗi đồng bộ User")));
                                }
                            })
                            .addOnFailureListener(e -> result.postValue(Resource.error("Lấy thông tin user thất bại")));

                })
                .addOnFailureListener(e -> {
                    result.postValue(Resource.error("Sai email hoặc mật khẩu"));
                });

        return result;
    }



    /**
     * Đăng xuất, xóa dữ liệu cục bộ.
     */
    public void logout() {
        // Xóa thông tin đăng nhập ở Auth
        authSource.signOut();
        // Xóa thông tin đang hiển thị
        currentUserProfile.postValue(null);
    }


    /**
     * Lấy profile user realtime từ Firestore (có snapshot listener).
     * @return LiveData<User> tự cập nhật khi có thay đổi
     */
    public LiveData<User> getCurrentUserProfile() {
        return currentUserProfile;
    }


    /**
     *  Hàm dùng để đăng nhập với các nhà cung cấp bên thứ ba (Google, Facebook).
     * Nó nhận AuthCredential để xác thực và kiểm tra/lưu thông tin người dùng vào Firestore.
     *
     * @param credential AuthCredential lấy được từ Google/Facebook SDK.
     * @return LiveData chứa thông tin User, kết quả trả về sẽ có dạng Loading, Success hoặc Error.
     */
    public LiveData<Resource<User>> signInWithCredential(AuthCredential credential) {
        MutableLiveData<Resource<User>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());

        authSource.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if(firebaseUser == null){
                        result.postValue(Resource.error("Lỗi xác thực, không thể lấy thông tin người dùng"));
                        return;
                    }

                    String uid = firebaseUser.getUid();
                    userSource.getUserDocument(uid).get()
                            .addOnSuccessListener(snapshot -> {
                                User user;
                                if(snapshot.exists()){
                                    user = snapshot.toObject(User.class);
                                    // Cập nhật email hoặc name nếu thay đổi từ provider
                                }else{
                                    // Tạo user mới từ thông tin của Google/Facebook
                                    user = new User();
                                    user.setUid(uid);
                                    user.setEmail(firebaseUser.getEmail());
                                    user.setName(firebaseUser.getDisplayName());
                                    user.setRole("tenant"); // Mặc định vai trò là người thuê
                                    user.setCreatedAt(new Date());
                                    userSource.saveUser(user); // Lưu vào FireStore
                                }
                                result.postValue(Resource.success(user));
                            })
                                .addOnFailureListener(e -> result.postValue(Resource.error("Lỗi lấy thông tin người dùng" + e.getMessage()))
                            );
                })
                    .addOnFailureListener(e -> result.postValue(Resource.error("Đăng nhập thất bại: " + e.getMessage()))
                );
        return  result;
    }
}
