package com.bin.ocr.convention

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor

@Suppress("EnumEntryName")
enum class FlavorDimension {
    contentType
}

// The content for the app can either come from local static data which is useful for demo
// purposes, or from a production backend server which supplies up-to-date, real content.
// These two product flavors reflect this behaviour.
@Suppress("EnumEntryName")
enum class ApplicationFlavor(
    val dimension: FlavorDimension,
    val applicationIdSuffix: String? = null
) {
}

fun appConfigureFlavors(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    flavorConfigurationBlock: ProductFlavor.(flavor: LibraryFlavor) -> Unit = {},
) {
    commonExtension.apply {
        FlavorDimension.values().forEach { flavorDimension ->
            flavorDimensions += flavorDimension.name
        }

        productFlavors {
            LibraryFlavor.values().forEach { libFlavor ->
                DataModelFlavor.values().forEach { dataFlavor ->
                    val combinedFlavor = "${libFlavor.name}${dataFlavor.name}"

                    register(combinedFlavor) {
                        dimension = libFlavor.dimension.name
                        flavorConfigurationBlock(this, libFlavor)
                    }
                }
            }

//            ApplicationFlavor.values().forEach { flavor ->
//                register(flavor.name) {
//                    dimension = flavor.dimension.name
//                    flavorConfigurationBlock(this, flavor)
//
////                    if (this@apply is ApplicationExtension && this is ApplicationProductFlavor) {
////                        if (flavor.applicationIdSuffix != null) {
////                            applicationIdSuffix = flavor.applicationIdSuffix
////                        }
////                    }
//                }
//            }
        }
    }
}