plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.weatherapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.weatherapp"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    dependencies {
        implementation ("com.github.akarnokd:rxjava3-retrofit-adapter:3.0.0")
        implementation ("androidx.room:room-rxjava3:2.6.1")
        implementation ("androidx.room:room-runtime:2.6.1")
        annotationProcessor ("androidx.room:room-compiler:2.6.1")
        implementation ("com.github.bumptech.glide:glide:4.16.0")
        implementation ("com.squareup.retrofit2:retrofit:2.7.2")
        implementation ("com.squareup.retrofit2:converter-gson:2.7.2")
        implementation ("com.squareup.okhttp3:okhttp:3.14.7")
        implementation("androidx.core:core-ktx:1.12.0")
        implementation("androidx.appcompat:appcompat:1.6.1")
        implementation("com.google.android.material:material:1.11.0")
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        testImplementation("junit:junit:4.13.2")
        androidTestImplementation("androidx.test.ext:junit:1.1.5")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
        annotationProcessor ("com.github.bumptech.glide:compiler:4.13.0")
        implementation ("com.google.code.gson:gson:2.9.0")
        implementation ("androidx.room:room-ktx:2.6.1")
        implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")
        implementation ("androidx.lifecycle:lifecycle-extensions:2.2.0")
        implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1")
        kapt ("androidx.room:room-compiler:2.6.1")
    }
}