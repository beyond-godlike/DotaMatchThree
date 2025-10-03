plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("com.google.devtools.ksp")
    id ("dagger.hilt.android.plugin")
}


kotlin {
    sourceSets {
        debug {
            kotlin.srcDir("build/generated/ksp/debug/kotlin")
        }
        release {
            kotlin.srcDir("build/generated/ksp/release/kotlin")
        }
    }
}

android {
    namespace = "com.example.dotamatchthree"
    compileSdk = 34

    testOptions {
        unitTests {
            all {
                it.enabled = true
            }
        }
        unitTests.isReturnDefaultValues = true
        unitTests.isIncludeAndroidResources = true
    }

    defaultConfig {
        applicationId = "com.example.dotamatchthree"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }
    packaging {
        resources {
            excludes += "META-INF/*"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.vectordrawable:vectordrawable:1.1.0")
    implementation("androidx.test:monitor:1.6.1")
    implementation("androidx.test:core-ktx:1.5.0")

    // hilt
    val daggerVersion = "2.48"

    val mockkVersion = "1.13.10"
    val mockitoVersion = "5.3.0"
    val multidex_version = "2.0.1"

    implementation ("com.google.dagger:hilt-android:$daggerVersion")
    implementation("com.google.android.engage:engage-core:1.4.0")
    implementation ("androidx.hilt:hilt-navigation-compose:1.2.0")
    ksp("com.google.dagger:hilt-android-compiler:$daggerVersion")

    implementation ("androidx.multidex:multidex:$multidex_version")

    implementation("io.coil-kt:coil:2.5.0")
    implementation("io.coil-kt:coil-compose:1.4.0")

    // Room
    val roomVersion = "2.6.0"

    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("androidx.room:room-runtime:$roomVersion")
    //noinspection GradleDependency
    ksp("androidx.room:room-compiler:$roomVersion")




    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation ("org.mockito:mockito-core:${mockitoVersion}")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.9")
    testImplementation("io.mockk:mockk:${mockkVersion}")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")

    androidTestImplementation ("com.google.dagger:hilt-android-testing:${daggerVersion}")
    kspAndroidTest("com.google.dagger:hilt-android-comiler:${daggerVersion}")

    androidTestImplementation ("org.mockito:mockito-android:${mockitoVersion}")
    androidTestImplementation ("io.mockk:mockk-android:${mockkVersion}")
    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.14")
}