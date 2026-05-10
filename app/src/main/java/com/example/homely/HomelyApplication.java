package com.example.homely;

import android.app.*;
import android.content.pm.*;
import android.os.*;
import android.util.*;
import android.util.Base64;
import android.view.*;

import androidx.annotation.*;

import com.cloudinary.android.*;
import com.facebook.*;
import com.facebook.appevents.*;

import java.util.*;

public class HomelyApplication extends Application {



    @Override
    public void onCreate() {
        super.onCreate();

        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dp8neyfcu");
        config.put("api_key", "615689798969528");
        config.put("api_secret", "ilILxecPczP6pLnoefoXqBPFook");


        MediaManager.init(this, config);

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
