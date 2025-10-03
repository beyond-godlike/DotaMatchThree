# Compose
-keep class androidx.compose.** { *; }
-keep class kotlin.Metadata { *; }

# Keep all Dagger/Hilt generated classes
-keep class dagger.** { *; }
-keep class hilt_*.** { *; }
-keep class com.example.dotamatchthree.** { *; }

# Keep data classes serialization (если есть)
-keepclassmembers class * { <init>(...); }

#Gson — сериализация через reflection
-keep class com.example.dotamatchthree.data.** { *; }

#Room
-keep class androidx.room.** { *; }
-keepclasseswithmembers class * {
    @androidx.room.* <fields>;
}