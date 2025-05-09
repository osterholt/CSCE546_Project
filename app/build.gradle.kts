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
	// Tensor Flow
	implementation(libs.tensorflow.lite)
	implementation (libs.tensorflow.lite.api)
	implementation(libs.tensorflow.lite.gpu)
	implementation ("org.tensorflow:tensorflow-lite-gpu-api:+")
	implementation(libs.tensorflow.lite.support)
	implementation(libs.tensorflow.lite.gpu.delegate.plugin)
	implementation (libs.google.accompanist.permissions)

	// AppCompat (usually already present)
	implementation(libs.androidx.appcompat) // or latest

	// ML Kit Vision Common (InputImage)
	implementation(libs.vision.common)

	// ML Kit Face Detection
	implementation (libs.face.detection.v1617)

	// CameraX, Room, Compose, etc...
	implementation(libs.androidx.camera.view)
	implementation(libs.androidx.camera.core)
	implementation(libs.androidx.camera.camera.camera2)
	implementation(libs.androidx.camera.lifecycle)
	implementation(libs.accompanist.permissions)
	implementation(libs.androidx.lifecycle.viewmodel.compose)
	implementation(libs.androidx.room.runtime)
	implementation(libs.androidx.room.ktx)
    implementation(libs.runtime.livedata)
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