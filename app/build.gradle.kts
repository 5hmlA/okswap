plugins {
    id("com.android.application")
    id("io.github.5hmlA.android.compose")
    id("io.github.5hmlA.protobuf")
}

android {
    defaultConfig {
        changeAPkName("newNaame")
        defineStr("strrr", "xxxxx")
        defineBool("boooo", false)
        vectorDrawables {
            useSupportLibrary = true
        }
//        signingConfig = signingConfigs.debug
    }

//    signingConfigs {
//        debug {
//            storeFile file('android.keystore')
//            storePassword 'jzb1234'
//            keyAlias 'spark'
//            keyPassword 'jzb1234'
//        }
//    }

    buildTypes {
        create("MyBuildType") {
            //子模块没有配置 MyBuildType 的时候默认用 debug
            matchingFallbacks.add("debug")
        }

        release {
            isMinifyEnabled = false
//            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),"proguard-rules.pro")
        }

        debug {
            defineInt("ces", 100)
            extra["alwaysUpdateBuildId"] = false
        }
    }

    //https://developer.android.google.cn/studio/build/build-variants
    flavorDimensions += "mode"
    productFlavors {
        create("demo") {
            // Assigns this product flavor to the "mode" flavor dimension.
            dimension = "mode"
        }

        create("full") {
            dimension = "mode"
        }
    }
//    sourceSets.forEach {
//        it.java.srcDirs(protobuf.generatedFilesBaseDir)
//        println(" source set ${it.name}")
//    }

//    sourceSets.getByName("main").java.srcDirs(protobuf.generatedFilesBaseDir)

    buildFeatures {
        viewBinding = true
    }

    androidComponents {
//        beforeVariants { variantBuilder ->
//            println("====================================== ${variantBuilder.name}")
//            variantBuilder.productFlavors.forEach { flavor ->
//                println("====================================== $flavor")
//            }
//        }
    }

    namespace = "osp.sparkj.more"
}

dependencies {
    implementation(project(mapOf("path" to ":okswap-ipc")))
    implementation(project(mapOf("path" to ":okswap")))
    implementation(project(mapOf("path" to ":okswap-bluetooth")))
}