plugins {
    alias(libs.plugins.generic.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.generic.hilt)
}

android {
    namespace = "org.bin.demo.uneodinary.common.utils"
}

dependencies {
    implementation("com.scottyab:rootbeer-lib:0.1.0")
    implementation(libs.com.google.code.gson)

}