plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.homely"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.homely"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Thư viện hỗ trợ Fragment và giao diện của Navigation
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")
    // Quản lý và đồng bộ phiên bản các thư viện (tránh xung đột)
    implementation(platform("com.google.firebase:firebase-bom:34.11.0"))
    // Theo dõi hành vi và thống kê người dùng
    implementation("com.google.firebase:firebase-analytics")
    // Quản lý đăng ký, đăng nhập và bảo mật tài khoản.
    implementation("com.google.firebase:firebase-auth")
    // Lưu trữ file vật lý (ảnh phòng trọ, ảnh đại diện).
    implementation("com.google.firebase:firebase-storage")
    // Cơ sở dữ liệu lưu trữ thông tin (giá phòng, địa chỉ, profile).
    implementation("com.google.firebase:firebase-firestore")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.cardview:cardview:1.0.0")
}