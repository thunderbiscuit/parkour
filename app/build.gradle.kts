plugins {
    id("com.android.application") version "8.10.1"
    id("org.jetbrains.kotlin.android") version "2.2.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.0"
}

android {
    namespace = "org.bitcoinopentools.parkour"
    compileSdk = 36

    defaultConfig {
        applicationId = "org.bitcoinopentools.parkour"
        minSdk = 34
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    // implementation(libs.androidx.material3)
    implementation("androidx.compose.material3:material3:1.4.0-alpha16")
    // implementation("androidx.compose.material:material-icons-core:1.4.0-alpha16")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Navigation
    implementation("androidx.navigation3:navigation3-runtime:1.0.0-alpha04")
    implementation("androidx.navigation3:navigation3-ui:1.0.0-alpha01")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.8.1")

    // Lucide icons
    implementation("com.composables:icons-lucide:1.1.0")

    // Required for the uniffi library
    implementation("net.java.dev.jna:jna:5.17.0@aar")
}
