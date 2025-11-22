package com.bin.ocr.convention

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor

@Suppress("EnumEntryName")
enum class DataModelFlavorDimension {
    contentType
}

@Suppress("EnumEntryName")
enum class DataModelFlavor(val dimension: DataModelFlavorDimension) {
    common(DataModelFlavorDimension.contentType),
    hyundai(DataModelFlavorDimension.contentType),
}

fun dataModelConfigureFlavors(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    flavorConfigurationBlock: ProductFlavor.(flavor: LibraryFlavor) -> Unit = {},
) {
    commonExtension.apply {
        flavorDimensions += DataModelFlavorDimension.contentType.name

        productFlavors {
            LibraryFlavor.values().forEach { flavor ->
                DataModelFlavor.values().forEach { dataFlavor ->
                    val combinedFlavor = "${flavor.name}${dataFlavor.name}"

                    create(combinedFlavor) {
                        dimension = flavor.dimension.name
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
                        java.srcDirs("src/${combinedFlavor}/java")
                        if (dataFlavor.name == DataModelFlavor.hyundai.name) {
                            java.srcDirs("src/${dataFlavor.name}/java")
                        }  else {
                            java.srcDirs("src/default/java")
                        }
                    }
                }
            }
        }
    }
}