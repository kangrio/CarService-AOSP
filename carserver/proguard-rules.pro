-keepnames class **$Companion { *; }
-dontwarn android.annotation.SystemApi
-keep class com.google.maps.automotive.proto.** { *; }

# Stub
-keep class android.** { *; }
-keep class com.android.car.** { *; }
-dontwarn android.media.AudioDevicePort
-dontwarn android.media.AudioPatch
-dontwarn android.media.AudioPort
-dontwarn android.media.AudioPortConfig
-dontwarn android.os.ServiceManager
-dontwarn android.util.IntArray
-dontwarn android.view.DisplayAddress$Physical
-dontwarn android.view.DisplayAddress
-dontwarn com.android.internal.annotations.GuardedBy
-dontwarn com.android.internal.annotations.VisibleForTesting
-dontwarn com.android.internal.util.Preconditions
-dontwarn libcore.io.IoUtils