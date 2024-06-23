import com.colisa.podplay.Configuration

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.jetbrains.kotlin.android)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.hilt)
  alias(libs.plugins.ksp)
}

android {
  namespace = Configuration.PACKAGE_NAME
  compileSdk = Configuration.COMPILE_SDK

  defaultConfig {
    applicationId = Configuration.PACKAGE_NAME
    minSdk = Configuration.MIN_SDK
    targetSdk = Configuration.TARGET_SDK
    versionCode = Configuration.VERSION_CODE
    versionName = Configuration.VERSION_NAME

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables {
      useSupportLibrary = true
    }

    ksp {
      arg("room.schemaLocation", "$projectDir/schemas")
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
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
    buildConfig = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = "1.5.11"
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}

dependencies {
  // Core
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)

  // Compose
  implementation(libs.androidx.activity.compose)
  val composeBoom = platform(libs.androidx.compose.bom)
  implementation(composeBoom)
  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.ui.tooling.preview)
  implementation(libs.androidx.material3)
  implementation(libs.androidx.material.icons.extended)

  // Compose Navigation
  implementation(libs.androidx.navigation.compose)

  // Compose Lifecycle
  implementation(libs.androidx.lifecycle.compose)

  // Timber
  implementation(libs.timber)

  // Hilt
  implementation(libs.hilt.android)
  implementation(libs.androidx.hilt.navigation.compose)
  ksp(libs.hilt.compiler)

  // Room
  implementation(libs.room.runtime)
  implementation(libs.room.ktx)
  ksp(libs.room.compiler)

  // Serialization
  implementation(libs.kotlinx.serialization)

  // Network
  implementation(libs.retrofit)
  implementation(libs.retrofit.kotlinx.converter)
  implementation(libs.okhttp)
  implementation(libs.logging.interceptor)

  // Coil
  implementation(libs.compose.coil)

  // RSS Parser
  implementation(libs.rssparser)

  // Testing
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.ui.test.junit4)
  debugImplementation(libs.androidx.ui.tooling)
  debugImplementation(libs.androidx.ui.test.manifest)
}