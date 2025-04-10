import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.realm.plugin)
    alias(libs.plugins.kotlinCocoapods)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    // For now skipping this
    cocoapods {
        // === Wymagana linia ===
        version = "1.0.0" // Ustaw wersję swojego modułu KMP (np. "0.1.0", "1.0.0")
        ios.deploymentTarget = "15.3"
//        // === Zalecane (opcjonalne) pola ===
//        summary = "Shared module for HobbyMatch app" // Krótki opis Twojego modułu
//        homepage = "link/do/twojego/repozytorium/lub/projektu" // Opcjonalnie
//
//        // === Konfiguracja Frameworka (opcjonalne) ===
//        framework {
//            baseName = "composeApp" // Nazwa frameworka generowanego dla Xcode (można zmienić np. na "Shared")
//            // isStatic = false // Domyślnie jest dynamiczny, zmień na true jeśli potrzebujesz statycznego
//        }

        pod("MapLibre", "6.9.0")
    }

    sourceSets {

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            // ktor - Network connectivity - Android
            implementation(libs.ktor.client.android)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
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

            // Realm - Local NoSQL Database
            implementation(libs.mongodb.realm)

            // Coroutines - kotlinx.coroutines library for non-blocking asynchronous computations
            implementation(libs.kotlin.coroutines)

            // Google Auth
            implementation("io.github.mirzemehdi:kmpauth-google:2.0.0")
            implementation("io.github.mirzemehdi:kmpauth-uihelper:2.0.0")

            // Map Libre Compose
            // implementation(libs.maplibre.compose)
        }
        androidMain.dependencies {
            // ktor - Network connectivity - Android
            implementation(libs.ktor.client.android)
        }
        iosMain.dependencies {
            // ktor - Network connectivity - iOS
            implementation(libs.ktor.client.darwin)
        }
    }
}

android {
    namespace = "io2.hobbymatch"
    compileSdk =
        libs.versions.android.compileSdk
            .get()
            .toInt()

    defaultConfig {
        applicationId = "io2.hobbymatch"
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
        targetSdk =
            libs.versions.android.targetSdk
                .get()
                .toInt()
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
}
