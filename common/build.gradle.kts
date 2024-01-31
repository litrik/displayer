@file:Suppress("UnstableApiUsage")

import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter;

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
    id("com.android.library")
    id("de.comahe.i18n4k") version "0.5.0"
    id("com.codingfeline.buildkonfig") version "0.13.3"
}

group = "com.displayer"
version = extra["app.versionName"] as String

i18n4k {
    sourceCodeLocales = listOf("en", "nl")
}

buildkonfig {
    packageName = "com.displayer"
    defaultConfigs {
        buildConfigField(STRING, "APP_VERSON_NAME", extra["app.versionName"] as String)
        buildConfigField(STRING, "BUILD_TIME", DateTimeFormatter.ISO_INSTANT.format(OffsetDateTime.now()) as String)
    }
}

kotlin {
    android()
    jvm("desktop")
    js(IR) {
        browser{
            runTask {
                outputFileName = "displayer.js"
            }
            webpackTask {
                outputFileName = "displayer.js"
            }
        }
        binaries.executable()
    }

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
        val androidTest by getting {
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
        val jsMain by getting {
            dependencies {
                implementation(compose.web.core)
                implementation(libs.i18n4k.core.js)
                implementation(libs.ktor.client.js)
                implementation(libs.okio.nodefilesystem)
            }
        }
    }
}

android {
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    compileSdk = (extra["app.compileSdk"] as String).toInt()

    defaultConfig {
        minSdk = (extra["app.minSdk"] as String).toInt()
        targetSdk = (extra["app.targetSdk"] as String).toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
    }
}

// a temporary workaround for a bug in jsRun invocation - see https://youtrack.jetbrains.com/issue/KT-48273
afterEvaluate {
    rootProject.extensions.configure<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension> {
        versions.webpackDevServer.version = "4.0.0"
        versions.webpackCli.version = "4.9.0"
        nodeVersion = "16.0.0"
    }
}

// TODO: remove when https://youtrack.jetbrains.com/issue/KT-50778 fixed
project.tasks.withType(org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile::class.java).configureEach {
    kotlinOptions.freeCompilerArgs += listOf(
        "-Xir-dce-runtime-diagnostic=log"
    )
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
