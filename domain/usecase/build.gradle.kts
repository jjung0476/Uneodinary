plugins {
    alias(libs.plugins.generic.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.generic.hilt)
}

android {
    namespace = "org.bin.demo.uneodinary.domain"
}

dependencies {
    implementation(project(":common:utils"))
    implementation(project(":data:repository"))

    implementation(libs.com.google.code.gson)
}