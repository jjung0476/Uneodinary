import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    alias(libs.plugins.generic.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.generic.application.compose)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.generic.hilt)
}

android {
    namespace = "org.bin.demo.uneodinary"
    compileSdk = 35

    applicationVariants.all {
        outputs.forEach { output ->
            val apkOutput = output as BaseVariantOutputImpl
            apkOutput.outputFileName = "app_prod_ver_${versionCode}_${buildType.name}.apk"
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file("/Users/bin/Documents/key/key.jks")
            storePassword = "584545"
            keyAlias = "key"
            keyPassword = "584545"
        }
    }


    bundle {
        language {
            enableSplit = false
        }
    }

    aaptOptions {
        noCompress("bin", "task")
    }

    defaultConfig {
        applicationId = "org.demo.uneodinary"
        minSdk = 26
        targetSdk = 35
        versionCode = 300
        versionName = "3.0"

        testInstrumentationRunner   = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    buildFeatures {
        //noinspection DataBindingWithoutKapt
        dataBinding = true
        viewBinding = true
        buildConfig = true
        compose = true
    }

    kotlinOptions {
        jvmTarget = "21"
    }
}

dependencies {
    // ML Kit Text Recognition (기본 모델)
    implementation("com.google.mlkit:text-recognition-korean:16.0.1")
    implementation("com.google.mlkit:translate:17.0.3")
    // ML Kit Translation
//    implementation("com.google.mlkit:translation:17.0.0") // 최신 버전 확인
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    implementation(project(":domain:usecase"))
    implementation(project(":common:utils"))
    implementation(project(":di"))
    implementation(project(":core:camerax"))
    implementation(project(":data:repository"))
    implementation("com.google.mediapipe:tasks-genai:0.10.27")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation(libs.runtime.livedata)
    implementation(libs.translate)
    implementation(libs.androidx.foundation.layout)
    implementation(libs.androidx.compose.ui.text)


    val work_version = "2.10.0"
    implementation("androidx.work:work-runtime-ktx:$work_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.3")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.material)
    implementation(libs.androidx.core.ktx)
    implementation(libs.activity.ktx)
    implementation(libs.fragment.ktx)

    implementation(libs.androidx.appcompat)
    implementation(libs.com.google.code.gson)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.foundation.android)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation("androidx.core:core-splashscreen:1.0.1")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // Hilt Navigation Compose
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
}