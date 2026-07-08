import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
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
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
    }
}

kotlin {
    jvmToolchain(25)
}

dependencies {
    implementation(projects.composeApp)
    implementation(compose.runtime)
    implementation(compose.preview)
    implementation(libs.androidx.activity.compose)
    implementation(libs.koin.android)
    debugImplementation(compose.uiTooling)
}

fun prop(name: String): String? {
    System.getenv(name)?.let { return it }

    val file = rootProject.file("local.properties")
    if (!file.exists()) return null

    val props = Properties()
    file.inputStream().use { props.load(it) }

    return props.getProperty(name)
}
