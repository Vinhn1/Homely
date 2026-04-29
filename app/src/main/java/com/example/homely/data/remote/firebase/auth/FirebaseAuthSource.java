package com.example.homely.data.remote.firebase.auth;

import com.google.android.gms.tasks.*;
import com.google.firebase.auth.*;

// Giao tiếp trực tiếp với Firebase SDK
public class FirebaseAuthSource {
    private FirebaseAuth mAuth;

    public FirebaseAuthSource(){
        mAuth = FirebaseAuth.getInstance();
    }

    // Đăng ký
    public Task<AuthResult> signUp(String email, String password){
        return mAuth.createUserWithEmailAndPassword(email, password);
    }

    // Đăng nhập
    public Task<AuthResult> signIn(String email, String password){
        return mAuth.signInWithEmailAndPassword(email, password);
    }

    // Đăng nhập bằng Google (Cần truyền vào Credential)
    public Task<AuthResult> signInWithGoogle(AuthCredential credential){
        return mAuth.signInWithCredential(credential);
    }
}
