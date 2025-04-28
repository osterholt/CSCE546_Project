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
	//Add these dependencies for camera
	implementation(libs.androidx.camera.view)
	implementation(libs.androidx.camera.core)
	implementation(libs.androidx.camera.camera.camera2)
	implementation(libs.androidx.camera.lifecycle)

	//Add these dependencies for ML libraries
	implementation(libs.play.services.mlkit.text.recognition.common)
	implementation(libs.play.services.mlkit.text.recognition)

	//add this dependency to check/ask for permissions
	implementation(libs.accompanist.permissions)

	// Add this dependency for viewmodel stuff
	implementation(libs.androidx.lifecycle.viewmodel.compose)

	// Add these dependencies for Room functionality
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
    testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(platform(libs.androidx.compose.bom))
	androidTestImplementation(libs.androidx.ui.test.junit4)
	debugImplementation(libs.androidx.ui.tooling)
	debugImplementation(libs.androidx.ui.test.manifest)
}