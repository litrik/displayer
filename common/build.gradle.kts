@file:Suppress("UnstableApiUsage")

import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

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

                // https://github.com/Kotlin/kotlinx.serialization/blob/master/CHANGELOG.md
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
                // https://github.com/Kotlin/kotlinx.collections.immutable/blob/master/CHANGELOG.md
                implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.5")
                // https://github.com/Kotlin/kotlinx-datetime/blob/master/CHANGELOG.md
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

                // https://github.com/alialbaali/Kamel/releases
                implementation("com.alialbaali.kamel:kamel-image:0.4.0")

                // https://github.com/ktorio/ktor/blob/main/CHANGELOG.md
                implementation("io.ktor:ktor-client-cio:" + extra["dep.ktor.version"] as String)
                implementation("io.ktor:ktor-client-content-negotiation:" + extra["dep.ktor.version"] as String)
                implementation("io.ktor:ktor-serialization-kotlinx-json:" + extra["dep.ktor.version"] as String)

                implementation("io.ktor:ktor-server-core:" + extra["dep.ktor.version"] as String)
                implementation("io.ktor:ktor-server-cio:" + extra["dep.ktor.version"] as String)
                implementation("org.slf4j:slf4j-nop:1.7.21")

                // https://github.com/russhwolf/multiplatform-settings/blob/master/CHANGELOG.md
                implementation("com.russhwolf:multiplatform-settings-no-arg:1.0.0-RC")

                // https://github.com/InsertKoinIO/koin/blob/main/CHANGELOG.md
                implementation("io.insert-koin:koin-core:" + extra["dep.koin.version"] as String)

                // https://github.com/touchlab/Kermit/releases
                api("co.touchlab:kermit:1.2.2")

                // https://github.com/comahe-de/i18n4k/releases
                implementation("de.comahe.i18n4k:i18n4k-core:0.5.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                // https://github.com/InsertKoinIO/koin/blob/main/CHANGELOG.md
                api("io.insert-koin:koin-android:" + extra["dep.koin.version"] as String)
            }
        }
        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13")
            }
        }
        val desktopMain by getting {
            dependencies {
                api(compose.preview)
            }
        }
        val desktopTest by getting
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
dependencies {
    implementation("androidx.compose.ui:ui:1.3.2")
}
