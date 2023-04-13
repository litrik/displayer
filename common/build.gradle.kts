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

                // https://github.com/Kotlin/kotlinx.serialization/blob/master/CHANGELOG.md
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
                // https://github.com/Kotlin/kotlinx.collections.immutable/blob/master/CHANGELOG.md
                implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.5")
                // https://github.com/Kotlin/kotlinx-datetime/blob/master/CHANGELOG.md
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

                // https://github.com/alialbaali/Kamel/pull/23
                implementation("io.github.luca992.com.alialbaali.kamel:kamel-image:0.5-darwinandjs0")

                // https://github.com/ktorio/ktor/blob/main/CHANGELOG.md
                implementation("io.ktor:ktor-client-content-negotiation:" + extra["dep.ktor.version"] as String)
                implementation("io.ktor:ktor-serialization-kotlinx-json:" + extra["dep.ktor.version"] as String)

                // https://github.com/russhwolf/multiplatform-settings/blob/master/CHANGELOG.md
                implementation("com.russhwolf:multiplatform-settings-no-arg:1.0.0-RC")

                // https://github.com/InsertKoinIO/koin/blob/main/CHANGELOG.md
                implementation("io.insert-koin:koin-core:" + extra["dep.koin.version"] as String)

                // https://github.com/touchlab/Kermit/releases
                api("co.touchlab:kermit:1.2.2")

                // https://github.com/comahe-de/i18n4k/releases
                implementation("de.comahe.i18n4k:i18n4k-core:0.5.0")

                // https://github.com/sergeych/mp_stools/releases
                implementation("net.sergeych:mp_stools:1.2.2")

                // https://square.github.io/okio/changelog/
                implementation("com.squareup.okio:okio:" + extra["dep.okio.version"] as String)
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
                // https://github.com/InsertKoinIO/koin/blob/main/CHANGELOG.md
                api("io.insert-koin:koin-android:" + extra["dep.koin.version"] as String)

                // https://github.com/ktorio/ktor/blob/main/CHANGELOG.md
                implementation("io.ktor:ktor-client-cio:" + extra["dep.ktor.version"] as String)

                implementation("io.ktor:ktor-server-core:" + extra["dep.ktor.version"] as String)
                implementation("io.ktor:ktor-server-cio:" + extra["dep.ktor.version"] as String)
                implementation("org.slf4j:slf4j-nop:1.7.21")

            }
        }
        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13")
            }
        }
        val desktopMain by getting {
            kotlin.srcDirs("src/jvmMain/kotlin")
            dependencies {
                api(compose.preview)

                // https://github.com/ktorio/ktor/blob/main/CHANGELOG.md
                implementation("io.ktor:ktor-client-cio:" + extra["dep.ktor.version"] as String)

                implementation("io.ktor:ktor-server-core:" + extra["dep.ktor.version"] as String)
                implementation("io.ktor:ktor-server-cio:" + extra["dep.ktor.version"] as String)
                implementation("org.slf4j:slf4j-nop:1.7.21")

            }
        }
        val desktopTest by getting
        val jsMain by getting {
            dependencies {
                implementation(compose.web.core)

                // https://github.com/comahe-de/i18n4k/releases
                implementation("de.comahe.i18n4k:i18n4k-core-js:" + extra["dep.i18n4k.version"] as String)

                // https://github.com/ktorio/ktor/blob/main/CHANGELOG.md
                implementation("io.ktor:ktor-client-js:" + extra["dep.ktor.version"] as String)

                // https://square.github.io/okio/changelog/
                implementation("com.squareup.okio:okio-nodefilesystem:" + extra["dep.okio.version"] as String)

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
        jvmTarget = "11"
    }
}

dependencies {
    implementation("androidx.compose.ui:ui:1.3.2")
}
