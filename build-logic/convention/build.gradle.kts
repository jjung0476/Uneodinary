import org.jetbrains.kotlin.gradle.dsl.JvmTarget

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)

    compileOnly(libs.android.tools.build.gradle)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.compose.compiler.extension)

    compileOnly(libs.android.tools.build.gradle)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.compose.compiler.extension)
}

gradlePlugin {
    plugins {
        register("Hilt") {
            id = "generic.hilt"
            implementationClass = "HiltConventionPlugin"
        }
        register("Feature") {
            id = "generic.feature"
            implementationClass = "FeatureConventionPlugin"
        }
        register("Application") {
            id = "generic.application"
            implementationClass = "ApplicationConventionPlugin"
        }
        register("Library") {
            id = "generic.library"
            implementationClass = "LibraryConventionPlugin"
        }
        register("LibraryCompose") {
            id = "generic.library.compose"
            implementationClass = "LibraryComposeConventionPlugin"
        }
        register("ApplicationCompose") {
            id = "generic.application.compose"
            implementationClass = "ApplicationComposeConventionPlugin"
        }
        register("androidRoom") {
            id = "generic.android.room"
            implementationClass = "AndroidRoomConventionPlugin"
        }
        register("androidAppFlavors") {
            id = libs.plugins.koi.android.application.flavors.get().pluginId
            implementationClass = "AndroidApplicationFlavorsConventionPlugin"
        }

        register("androidLibFlavors") {
            id = libs.plugins.koi.android.library.flavors.get().pluginId
            implementationClass = "AndroidLibraryFlavorsConventionPlugin"
        }

        register("androidDataModelFlavors") {
            id = libs.plugins.koi.android.datamodel.flavors.get().pluginId
            implementationClass = "AndroidDataModelFlavorsConventionPlugin"
        }
    }
}