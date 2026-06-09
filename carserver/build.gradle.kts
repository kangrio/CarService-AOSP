import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

val localProperties = rootProject.extra["LOCAL_PROPERTIES"] as Properties

android {
    namespace = "com.kangrio.carserver"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
//        applicationId = "com.android.car"
        applicationId = "com.kangrio.carserver"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    signingConfigs {
        create("release") {
            val keystorePath = localProperties.getProperty("KEYSTORE_PATH") ?: ""
            storeFile = file(keystorePath)
            storePassword = localProperties.getProperty("KEYSTORE_PASSWORD") ?: ""
            keyAlias = localProperties.getProperty("KEY_ALIAS") ?: ""
            keyPassword = localProperties.getProperty("KEY_PASSWORD") ?: ""
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        create("profile") {
            initWith(getByName("release"))
        }
        debug {
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true
        aidl = true
    }
}

dependencies {
    compileOnly(files("libs/bydsdk.jar"))
    implementation(files("libs/android.car.jar"))
    implementation("org.lsposed.hiddenapibypass:hiddenapibypass:6.1")

    implementation("com.google.protobuf:protobuf-java-util:4.35.0")

    implementation("androidx.work:work-runtime:2.11.2")
    implementation("androidx.datastore:datastore-preferences:1.2.1")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.material)
}
