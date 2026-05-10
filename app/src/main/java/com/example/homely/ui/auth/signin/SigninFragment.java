package com.example.homely.ui.auth.signin;

import android.content.Intent;
import android.content.pm.*;
import android.os.Bundle;
import android.util.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.*;

import com.example.homely.MainActivity;
import com.example.homely.R;
import com.example.homely.data.remote.firebase.auth.FirebaseAuthSource;
import com.example.homely.data.remote.firebase.firestore.FirestoreUserSource;
import com.example.homely.data.repository.AuthRepository;
import com.example.homely.databinding.FragmentSigninBinding;
import com.example.homely.ui.auth.forgotpassword.ForgotPasswordFragment;
import com.example.homely.ui.auth.register.RegisterFragment;
import com.example.homely.ui.auth.viewmodel.AuthViewModel;
import com.facebook.*;
import com.facebook.appevents.*;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

public class SigninFragment extends Fragment {

    private FragmentSigninBinding binding;
    private AuthViewModel authViewModel;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN_GOOGLE = 100;
    private CallbackManager facebookCallbackManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSigninBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo dependency
        FirebaseAuthSource authSource = new FirebaseAuthSource();
        FirestoreUserSource userSource = new FirestoreUserSource();
        AuthRepository repository = new AuthRepository(authSource, userSource);

        ViewModelProvider.Factory factory = new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                if (modelClass.isAssignableFrom(AuthViewModel.class)) {
                    return (T) new AuthViewModel(repository);
                }
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
        };
        authViewModel = new ViewModelProvider(requireActivity(), factory).get(AuthViewModel.class);

        // Cấu hình Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso); // THÊM DÒNG NÀY

        // Cấu hình Facebook
        facebookCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }
            @Override
            public void onCancel() { }
            @Override
            public void onError(@NonNull FacebookException e) {
                Toast.makeText(getContext(), "Lỗi Facebook: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        setupListeners();
        observeLoginResult();
        setupScrollToFocusedInput();
    }

    private void setupScrollToFocusedInput() {
        View.OnFocusChangeListener scrollToView = (v, hasFocus) -> {
            if (!hasFocus) return;
            v.postDelayed(() -> {
                if (binding == null) return;
                View viewToScroll = v;
                if (v.getParent() != null && v.getParent().getParent() instanceof com.google.android.material.textfield.TextInputLayout) {
                    viewToScroll = (View) v.getParent().getParent();
                }
                android.graphics.Rect rect = new android.graphics.Rect(0, 0, viewToScroll.getWidth(), viewToScroll.getHeight() + 100);
                viewToScroll.requestRectangleOnScreen(rect, false);
            }, 500);
        };
        binding.edtEmailPhone.setOnFocusChangeListener(scrollToView);
        binding.edtPassword.setOnFocusChangeListener(scrollToView);
    }

    private void observeLoginResult() {
        authViewModel.getLoginResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;
            switch (resource.status) {
                case LOADING:
                    binding.progressBarSignin.setVisibility(View.VISIBLE);
                    binding.btnLogin.setEnabled(false);
                    break;
                case SUCCESS:
                    binding.progressBarSignin.setVisibility(View.GONE);
                    binding.btnLogin.setEnabled(true);
                    Toast.makeText(getContext(), "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    requireActivity().finish();
                    break;
                case ERROR:
                    binding.progressBarSignin.setVisibility(View.GONE);
                    binding.btnLogin.setEnabled(true);
                    Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void setupListeners() {
        binding.btnLogin.setOnClickListener(v -> performLogin());
        binding.tvRegister.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment_auth, new RegisterFragment())
                    .addToBackStack(null)
                    .commit();
        });
        binding.tvForgotPassword.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment_auth, new ForgotPasswordFragment())
                    .addToBackStack(null)
                    .commit();
        });
        binding.btnLoginGoogle.setOnClickListener(v -> signInWithGoogle());
        binding.btnLoginFacebook.setOnClickListener(v -> signInWithFacebook());
    }

    private void signInWithGoogle() {
        startActivityForResult(googleSignInClient.getSignInIntent(), RC_SIGN_IN_GOOGLE);
    }

    private void signInWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        authViewModel.signInWithCredential(credential);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Chuyển kết quả cho Facebook
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        // Xử lý kết quả Google
        if (requestCode == RC_SIGN_IN_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                authViewModel.signInWithCredential(credential);
            } catch (ApiException e) {
                Toast.makeText(getContext(), "Đăng nhập Google thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void performLogin() {
        String email = binding.edtEmailPhone.getText().toString().trim();
        String password = binding.edtPassword.getText().toString().trim();
        binding.tilEmailPhone.setError(null);
        binding.tilPassword.setError(null);

        if (email.isEmpty()) {
            binding.tilEmailPhone.setError("Vui lòng nhập email");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmailPhone.setError("Email không hợp lệ");
            return;
        }
        if (password.isEmpty()) {
            binding.tilPassword.setError("Vui lòng nhập mật khẩu");
            return;
        }
        authViewModel.login(email, password);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}