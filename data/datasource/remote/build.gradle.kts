plugins {
    alias(libs.plugins.generic.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.generic.hilt)
}

android {
    namespace = "org.bin.demo.uneodinary.data.remote"
}

dependencies {
    implementation(project(":common:utils"))
    implementation(project(":data:repository"))
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    implementation("androidx.datastore:datastore-preferences:1.0.0")
}