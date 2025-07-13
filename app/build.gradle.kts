plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.nestor.nestor"
    compileSdk = 35

    defaultConfig {
        manifestPlaceholders += mapOf("YANDEX_CLIENT_ID" to "fb3c9fdcef574f8d8bc5955fa0bb11f9")
        applicationId = "com.nestor.nestor"
        // В Kotlin DSL нужно использовать minSdk, а не minSdkVersion()
        minSdk = rootProject.extra["defaultMinSdkVersion"] as Int
        targetSdk = 35
        versionCode = 7
        versionName = "1.31"

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
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.squareup.okhttp3:okhttp:4.10.0") // лучше оставить одну версию OkHttp
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("androidx.browser:browser:1.8.0")
    implementation("com.yandex.android:authsdk:3.1.3")
}
