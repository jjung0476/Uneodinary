package com.bin.ocr.convention

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor

@Suppress("EnumEntryName")
enum class LibFlavorDimension {
    contentType
}

@Suppress("EnumEntryName")
enum class LibraryFlavor(val dimension: LibFlavorDimension, val licenseDate: String) {
    base(LibFlavorDimension.contentType, ""),
    detectionOnly(LibFlavorDimension.contentType, "20250630"),
    detectionAndRecognition(LibFlavorDimension.contentType, ""),
}

fun libConfigureFlavors(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    flavorConfigurationBlock: ProductFlavor.(flavor: LibraryFlavor) -> Unit = {},
) {
    commonExtension.apply {
        flavorDimensions += LibFlavorDimension.contentType.name

        productFlavors {
            LibraryFlavor.values().forEach { flavor ->
                DataModelFlavor.values().forEach { dataFlavor ->
                    val combinedFlavor = "${flavor.name}${dataFlavor.name}"

                    create(combinedFlavor) {
                        dimension = flavor.dimension.name

                        buildConfigField("String", "LICENSE_DATE", "\"${flavor.licenseDate}\"")
                        flavorConfigurationBlock(this, flavor)
                    }
                }
            }
        }
        sourceSets {
            LibraryFlavor.values().forEach { libFlavor ->
                DataModelFlavor.values().forEach { dataFlavor ->
                    val combinedFlavor = "${libFlavor.name}${dataFlavor.name}"

                    getByName(combinedFlavor) {
                        assets.srcDirs("src/${libFlavor.name}/assets")
                        if (libFlavor.name in listOf(LibraryFlavor.detectionOnly.name, LibraryFlavor.detectionAndRecognition.name)) {
                            java.srcDirs("src/${libFlavor.name}/java")
                            assets.srcDirs("src/${libFlavor.name}/assets", "src/default/assets")
                        } else {
                            java.srcDirs("src/default/java")
                        }
                    }
                }
            }
        }
    }
}