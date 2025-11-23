plugins {
    alias(libs.plugins.generic.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.generic.hilt)
}

android {
    namespace = "org.bin.demo.uneodinary.data.repository"
}

dependencies {
    implementation(project(":common:utils"))
    api ("com.squareup.retrofit2:retrofit:2.9.0")
    api ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
}