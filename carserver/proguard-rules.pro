-keepnames class **$Companion { *; }
-dontwarn android.annotation.SystemApi
-keep class com.google.maps.automotive.proto.** { *; }

# Stub
-keep class android.** { *; }
-keep class com.android.car.** { *; }
-dontwarn *