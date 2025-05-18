import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.kotlinCocoapods)
}

kotlin {
//    cocoapods {
//        // Required properties
//        // Specify the required Pod version here
//        // Otherwise, the Gradle project version is used
//        version = "1.0"
//        summary = "Some description for a Kotlin/Native module"
//        homepage = "Link to a Kotlin/Native module homepage"
//
//        // Optional properties
//        // Configure the Pod name here instead of changing the Gradle project name
//        name = "MyCocoaPod"
//
//        // pod("MapLibre", "6.13.0")
//
//        framework {
//            // Required properties
//            // Framework name configuration. Use this property instead of deprecated 'frameworkName'
//            baseName = "ComposeApp"
//
//            // Optional properties
//            // Specify the framework linking type. It's dynamic by default.
//            isStatic = false
//            // Dependency export
//            // Uncomment and specify another project module if you have one:
//            // export(project(":<your other KMP module>"))
//            transitiveExport = false // This is default.
//        }
//
////        // Maps custom Xcode configuration to NativeBuildType
////        xcodeConfigurationToNativeBuildType["CUSTOM_DEBUG"] = NativeBuildType.DEBUG
////        xcodeConfigurationToNativeBuildType["CUSTOM_RELEASE"] = NativeBuildType.RELEASE
//    }

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            // ktor - Network connectivity - Android
            implementation(libs.ktor.client.android)

            implementation(libs.koin.android)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            // ktor - Network connectivity
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.content.negotiation)

            // kotlinx serialization
            implementation(libs.kotlinx.serialization.core)
            implementation(libs.kotlinx.serialization.json)

            // Voyager navigation
            implementation(libs.navigator)
            implementation(libs.navigator.koin)
            implementation(libs.navigator.screen.model)
            implementation(libs.navigator.transitions)
            implementation(libs.voyager.tabNavigator)

            // koin - Dependency Injection
            implementation(libs.koin.core)

            // Stately Common - state management
            implementation(libs.stately.common)

            // Coroutines - kotlinx.coroutines library for non-blocking asynchronous computations
            implementation(libs.kotlin.coroutines)

            //Google Auth
            implementation(libs.kmpauth.google)
            implementation(libs.kmpauth.uihelper)

            // Room - Local SQL Database
            implementation(libs.androidx.room.runtime)
            implementation(libs.sqlite.bundled)

            // MapLibre - Map rendering
            implementation(libs.maplibre.compose)
        }
        iosMain.dependencies {
            // ktor - Network connectivity - iOS
            implementation(libs.ktor.client.darwin)
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

android {
    namespace = "io2.hobbymatch"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "io2.hobbymatch"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.foundation.layout.android)
    debugImplementation(compose.uiTooling)
    ksp(libs.androidx.room.compiler)
}

