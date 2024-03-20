// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
}
//ext {
//    // App dependencies
//    androidXAnnotations = '1.3.0'
//    appCompatVersion = '1.4.0'
//    archLifecycleVersion = '2.4.0'
//    coroutinesVersion = '1.5.2'
//    cardVersion = '1.0.0'
//    dexMakerVersion = '2.12.1'
//    fragmentKtxVersion = '1.4.0'
//    materialVersion = '1.4.0'
//    recyclerViewVersion = '1.2.1'
//    rulesVersion = '1.0.1'
//    swipeRefreshLayoutVersion = '1.1.0'
//    timberVersion = '4.7.1'
//    truthVersion = '1.1.2'
//}

buildscript {
    repositories {
        google()
    }
    dependencies {
        val nav_version = "2.7.7"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
    }
}
