plugins {
    id("com.android.application")
}

android {
    namespace = "df.root"
    compileSdk = 36

    defaultConfig {
        applicationId = "df.root"
        minSdk = 32
        targetSdk = 36
        versionCode = 201
        versionName = "2.1"

        ndk {
            abiFilters += listOf("arm64-v8a")
        }
    }

    signingConfigs {
        create("release") {
            val keystorePath = System.getenv("KEYSTORE_FILE")
            val keystoreFile = when {
                !keystorePath.isNullOrBlank() -> file(keystorePath)
                file("release.jks").exists() -> file("release.jks")
                rootProject.file("release.jks").exists() -> rootProject.file("release.jks")
                else -> null
            }
            if (keystoreFile != null && keystoreFile.exists()) {
                storeFile = keystoreFile
                storePassword = System.getenv("KEYSTORE_PASSWORD").takeUnless { it.isNullOrBlank() } ?: "dirtyfrag"
                keyAlias = System.getenv("KEY_ALIAS").takeUnless { it.isNullOrBlank() } ?: "dirtyfrag"
                keyPassword = System.getenv("KEY_PASSWORD").takeUnless { it.isNullOrBlank() } ?: "dirtyfrag"
            }
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            val keystorePath = System.getenv("KEYSTORE_FILE")
            val keystoreFile = when {
                !keystorePath.isNullOrBlank() -> file(keystorePath)
                file("release.jks").exists() -> file("release.jks")
                rootProject.file("release.jks").exists() -> rootProject.file("release.jks")
                else -> null
            }
            signingConfig = if (keystoreFile != null && keystoreFile.exists()) {
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }
            isMinifyEnabled = false
        }
    }

    applicationVariants.all {
        outputs.all {
            (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName = "dirtyfrag.apk"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    lint {
        checkReleaseBuilds = false
    }

    buildFeatures {
        viewBinding = true
    }

    externalNativeBuild {
        cmake {
            path("src/main/jni/CMakeLists.txt")
        }
    }

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
}
