@file:Suppress("UnstableApiUsage")

plugins {
    id("org.jetbrains.compose")
    id("com.android.application")
    kotlin("android")
}

repositories {
    jcenter()
}

dependencies {
    implementation(project(":common"))

    // https://developer.android.com/jetpack/androidx/releases/activity
    implementation("androidx.activity:activity-compose:1.6.1")

}

fun isBuildServer() = System.getenv().containsKey("CI") || System.getenv().containsKey("BUILD_NUMBER")

android {

    compileSdk = (extra["app.compileSdk"] as String).toInt()

    defaultConfig {
        applicationId = "com.displayer"
        namespace = "com.displayer.android"
        minSdk = (extra["app.minSdk"] as String).toInt()
        targetSdk = (extra["app.targetSdk"] as String).toInt()
        versionCode = (extra["app.versionCode"] as String).toInt()
        versionName = extra["app.versionName"] as String

        resourceConfigurations += "en"

        if (isBuildServer()) {
            setProperty("archivesBaseName", "$namespace-$versionName-$versionCode")
        }
    }

    buildTypes {
        named("debug").configure {
            isMinifyEnabled = false
        }
        named("release").configure {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
        freeCompilerArgs = listOf(
            "-Xallow-unstable-dependencies"
        )
    }

    lint {
        abortOnError = false
    }
}