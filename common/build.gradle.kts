@file:Suppress("UnstableApiUsage")

import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter;

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
    id("com.android.library")
    id("de.comahe.i18n4k") version "0.7.0"
    id("com.codingfeline.buildkonfig") version "0.13.3"
}

group = "com.displayer"
version = "0.1"

i18n4k {
    sourceCodeLocales = listOf("en", "nl")
}

buildkonfig {
    packageName = "com.displayer"
    defaultConfigs {
        buildConfigField(STRING, "APP_VERSON_NAME", version as String)
        buildConfigField(STRING, "BUILD_TIME", DateTimeFormatter.ISO_INSTANT.format(OffsetDateTime.now()) as String)
    }
}

kotlin {
    android()
    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {

                api(compose.runtime)
                api(compose.foundation)

                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.collections.immutable)
                implementation(libs.kotlinx.datetime)

                implementation(libs.kamel.image)

                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)

                implementation(libs.multiplatform.settings.no.arg)

                implementation(libs.koin.core)

                api(libs.kermit)

                implementation(libs.i18n4k.core)

                implementation(libs.mp.stools)

                implementation(libs.okio)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            kotlin.srcDirs("src/jvmMain/kotlin")
            dependencies {
                api(libs.koin.android)
                implementation(libs.ktor.client.cio)
                implementation(libs.ktor.server.core)
                implementation(libs.ktor.server.cio)
                implementation(libs.slf4j.slf4j.nop)
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.junit)
            }
        }
        val desktopMain by getting {
            kotlin.srcDirs("src/jvmMain/kotlin")
            dependencies {
                api(compose.preview)
                implementation(libs.ktor.client.cio)
                implementation(libs.ktor.server.core)
                implementation(libs.ktor.server.cio)
                implementation(libs.slf4j.slf4j.nop)
            }
        }
        val desktopTest by getting
    }
}

android {
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    compileSdk = AndroidSdk.compile

    defaultConfig {
        minSdk = AndroidSdk.min
        namespace = "com.displayer"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
    }
}

compose.experimental {
    web.application {}
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-opt-in=kotlin.RequiresOptIn"
        )
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    implementation(libs.compose.ui)
}
