plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.android.car"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.android.car"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
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
        aidl = true
    }
}

dependencies {
    implementation(files("libs/android.car.jar"))
    implementation("org.lsposed.hiddenapibypass:hiddenapibypass:6.1")

    implementation(libs.androidx.core.ktx)
}
