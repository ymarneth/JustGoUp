import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    listOf(
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
            implementation(libs.sqldelight.android)
            // --- Koin (Android) ---
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.kotlinx.datetime)
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)
            // --- Koin (common) ---
            implementation(libs.koin.core)
        }
        iosMain.dependencies {
            implementation(libs.sqldelight.native)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "org.clc.justgoup"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.clc.justgoup"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = System.getenv("VERSION_CODE")?.toInt() ?: 1
        versionName = System.getenv("VERSION_NAME") ?: "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    signingConfigs {
        create("ciRelease") {
            val keystore = prop("ANDROID_KEYSTORE_PATH")
            if (keystore != null) {
                storeFile = file(keystore)
                storePassword = prop("ANDROID_KEYSTORE_PASSWORD")
                keyAlias = prop("ANDROID_KEY_ALIAS")
                keyPassword = prop("ANDROID_KEY_PASSWORD")
            }
        }
    }
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("ciRelease")
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

sqldelight {
    databases {
        create("JustGoUpDatabase") {
            packageName.set("org.clc.justgoup.cache")
            verifyMigrations = true
            migrationOutputDirectory = file("src/commonMain/sqldelight/migrations")
        }
    }
}

composeCompiler {
    metricsDestination = layout.buildDirectory.dir("compose-metrics")
    reportsDestination = layout.buildDirectory.dir("compose-reports")
}

fun prop(name: String): String? {
    System.getenv(name)?.let { return it }

    val file = rootProject.file("local.properties")
    if (!file.exists()) return null

    val props = Properties()
    file.inputStream().use { props.load(it) }

    return props.getProperty(name)
}

