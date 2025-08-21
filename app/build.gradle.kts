plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    id("androidx.navigation.safeargs.kotlin")
    id("com.squareup.sqldelight")
    id("kotlin-parcelize")
}

android {
    namespace = "com.ursolgleb.pockemon"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ursolgleb.pockemon"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
}



dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Room (база данных Room)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // Hilt (внедрение зависимостей)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Lifecycle (архитектурные компоненты)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // WorkManager (только вариант ktx)
    implementation(libs.androidx.work.runtime.ktx)

    // Сеть/Networking
    implementation(libs.okhttp)
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation(libs.retrofit)

    // Moshi (KSP)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.retrofit.converter.moshi)
    ksp(libs.moshi.kotlin.codegen)

    // UI (пользовательский интерфейс)
    implementation(libs.glide)
    ksp(libs.compiler)
    implementation(libs.androidx.emoji2.bundled)
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Прочее
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.cache4k)
    implementation(libs.sqlDelightAndroidDriver)
    implementation(libs.sqlDelightCoroutines)
    implementation("androidx.core:core-splashscreen:1.0.1")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}


