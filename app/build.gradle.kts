import com.android.tools.r8.internal.ml
import org.gradle.kotlin.dsl.implementation

plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.kotlin.compose)
	id("com.google.devtools.ksp")
}

android {
	namespace = "com.example.csce546_project"
	compileSdk = 35

	defaultConfig {
		applicationId = "com.example.csce546_project"
		minSdk = 25
		targetSdk = 35
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	kotlinOptions {
		jvmTarget = "11"
	}
	buildFeatures {
		compose = true
	}
}

dependencies {
	implementation (libs.firebase.ml.vision.v2403)
//	implementation(platform("com.google.firebase:firebase-bom:26.8.0"))
//
//	// Firebase ML Vision
//	implementation("com.google.firebase:firebase-ml-vision:24.1.0")
//	implementation("com.google.firebase:firebase-ml-vision-face-model:20.0.2")
//
//	// Explicitly declare Google Vision libraries to avoid duplicates
//	implementation("com.google.android.gms:play-services-vision:20.1.3")
//	implementation("com.google.android.gms:play-services-vision-common:19.1.3")

	// CameraX, Room, Compose, etc...
	implementation(libs.androidx.camera.view)
	implementation(libs.androidx.camera.core)
	implementation(libs.androidx.camera.camera.camera2)
	implementation(libs.androidx.camera.lifecycle)
	implementation(libs.accompanist.permissions)
	implementation(libs.androidx.lifecycle.viewmodel.compose)
	implementation(libs.androidx.room.runtime)
	implementation(libs.androidx.room.ktx)
	ksp(libs.androidx.room.compiler)
	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.lifecycle.runtime.ktx)
	implementation(libs.androidx.activity.compose)
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.ui)
	implementation(libs.androidx.ui.graphics)
	implementation(libs.androidx.ui.tooling.preview)
	implementation(libs.androidx.material3)
	implementation(libs.coil.compose)

	// Tests
	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(platform(libs.androidx.compose.bom))
	androidTestImplementation(libs.androidx.ui.test.junit4)
	debugImplementation(libs.androidx.ui.tooling)
	debugImplementation(libs.androidx.ui.test.manifest)
}