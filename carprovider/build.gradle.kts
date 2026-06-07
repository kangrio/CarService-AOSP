plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.kangrio.carservice"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.kangrio.carservice"
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
        aidl = true
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
//    implementation("com.google.protobuf:protobuf-javalite:4.35.0")
    implementation("com.google.protobuf:protobuf-java-util:4.35.0")

    compileOnly(files("libs/bydsdk.jar"))
}