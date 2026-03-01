plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")

}

android {
    namespace = "com.nexushardware.app"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.nexushardware.app"
        minSdk = 24
        targetSdk = 36
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

    //agrego para el binding
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    implementation(libs.glide)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.play.services.auth)

    implementation("com.google.android.gms:play-services-maps:18.2.0")

    implementation("com.google.firebase:firebase-firestore")
    implementation("com.airbnb.android:lottie:6.4.0")

    implementation("me.relex:circleindicator:2.1.6")
    //google maps
    implementation("com.google.android.gms:play-services-location:21.0.1")

    implementation("com.google.android.libraries.places:places:3.4.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")






//    //Glide
//    implementation("com.github.bumptech.glide:glide:4.16.0")
//
//    // Para implementar viewModelScope
//    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.0")
//
//    // Firebase BOM
//    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
//
//    // Firebase Auth
//    implementation("com.google.firebase:firebase-auth-ktx")
//
//    // Google Sign In
//    implementation("com.google.android.gms:play-services-auth:21.0.0")
}