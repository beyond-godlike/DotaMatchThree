# ----------------------------
# Kotlin & Compose
# ----------------------------
-keep class kotlin.Metadata { *; }
-keep class androidx.compose.runtime.** { *; }
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

# ----------------------------
# Dagger/Hilt
# ----------------------------
# Keep only generated Hilt classes
-keep class hilt_*.** { *; }
-keep class dagger.hilt.android.internal.** { *; }

# ----------------------------
# Gson (reflection)
# ----------------------------
# Keep only fields in data classes that are serialized
-keepclassmembers class com.example.dotamatchthree.data.** {
    <fields>;
}

# ----------------------------
# Room
# ----------------------------
# Keep Room entities & DAO members
-keep class androidx.room.** { *; }
-keepclassmembers class * {
    @androidx.room.* <fields>;
}

# ----------------------------
# General
# ----------------------------
# Keep all constructors (needed for reflection)
-keepclassmembers class * {
    <init>(...);
}