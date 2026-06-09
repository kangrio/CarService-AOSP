import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
}

val localProperties = gradleLocalProperties(rootDir, providers)
extra["LOCAL_PROPERTIES"] = localProperties