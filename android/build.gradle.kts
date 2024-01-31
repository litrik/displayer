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
    implementation(libs.androidx.activity.compose)
}

fun isBuildServer() = System.getenv().containsKey("CI") || System.getenv().containsKey("BUILD_NUMBER")

android {

    compileSdk = AndroidSdk.compile

    defaultConfig {
        applicationId = "com.displayer"
        namespace = "com.displayer.android"
        minSdk = AndroidSdk.min
        targetSdk = AndroidSdk.target
        versionCode = 1
        versionName = "0.1"

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