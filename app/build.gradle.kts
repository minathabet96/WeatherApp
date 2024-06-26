plugins {
    id("com.android.application")
    id("androidx.navigation.safeargs")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.weatherapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.weatherapp"
        minSdk = 33
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
    buildFeatures {
        viewBinding = true
    }
}


dependencies {
    implementation ("com.airbnb.android:lottie:3.4.0")
    implementation ("com.google.android.material:material:1.11.0")
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation ("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation ("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
        implementation ("org.osmdroid:osmdroid-android:6.1.14")
        implementation("androidx.datastore:datastore-preferences-core:1.0.0")
        implementation("androidx.datastore:datastore-preferences-rxjava2:1.0.0")
        implementation("androidx.datastore:datastore-preferences-rxjava3:1.0.0")
        implementation("com.google.android.gms:play-services-location:21.1.0")
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
        implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
        implementation ("androidx.lifecycle:lifecycle-extensions:2.2.0")
        implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1")
        implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.4.1")
        kapt ("androidx.room:room-compiler:2.6.1")
        implementation ("de.hdodenhof:circleimageview:3.1.0")
        implementation ("androidx.navigation:navigation-fragment:2.5.3")
        implementation ("androidx.navigation:navigation-ui:2.5.3")
        implementation ("com.google.android.material:material:1.11.0")
        testImplementation ("junit:junit:4.13.2")
        testImplementation ("org.hamcrest:hamcrest-all:1.3")
        testImplementation ("androidx.arch.core:core-testing:$2.1.0")
        testImplementation ("org.robolectric:robolectric:4.5.1")
        testImplementation ("androidx.test:core-ktx:1.4.0")
        //testImplementation "androidx.test.ext:junit:$androidXTestExtKotlinRunnerVersion"

        // AndroidX Test - Instrumented testing //Added Item here
        androidTestImplementation ("androidx.test:runner:1.1.3")
        androidTestImplementation ("androidx.test:rules:1.2.0")
        androidTestImplementation ("androidx.test.espresso:espresso-core:3.4.0")

        testImplementation ("org.hamcrest:hamcrest:2.2")
        testImplementation ("org.hamcrest:hamcrest-library:2.2")
        androidTestImplementation ("org.hamcrest:hamcrest:2.2")
        androidTestImplementation ("org.hamcrest:hamcrest-library:2.2")

        testImplementation ("androidx.test.ext:junit-ktx:1.1.3")
        testImplementation ("androidx.test:core-ktx:1.4.0")
        testImplementation ("org.robolectric:robolectric:4.8")

        // InstantTaskExecutorRule
        testImplementation ("androidx.arch.core:core-testing:2.1.0")
        androidTestImplementation ("androidx.arch.core:core-testing:2.1.0")

        testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.0")
        androidTestImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.0")
}