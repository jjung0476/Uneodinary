plugins {
    alias(libs.plugins.generic.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.generic.hilt)
    alias(libs.plugins.generic.android.room)
}

android {
    namespace = "org.bin.demo.uneodinary.di"
}

dependencies {
    implementation(project(":common:utils"))
    implementation(project(":domain:usecase"))
    implementation(project(":data:repository"))
    implementation(project(":data:datasource:remote"))
}