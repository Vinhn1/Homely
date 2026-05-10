package com.example.homely;

import android.app.*;
import android.content.pm.*;
import android.os.*;
import android.util.*;
import android.view.*;

import androidx.annotation.*;

import com.facebook.*;
import com.facebook.appevents.*;

public class HomelyApplication extends Application {



    @Override
    public void onCreate() {
        super.onCreate();

        // Log Key Hash để cấu hình Facebook Console
        logKeyHash();

        AppEventsLogger.activateApp(this);
    }


    private void logKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES
            );
            for (Signature signature : info.signatures) {
                java.security.MessageDigest md =
                        java.security.MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String keyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.d("KeyHash", "Key Hash: " + keyHash);
            }
        } catch (Exception e) {
            Log.e("KeyHash", "Error: " + e.getMessage());
        }
    }
}
