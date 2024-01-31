pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    plugins {
        kotlin("multiplatform").version("1.8.20")
        kotlin("plugin.serialization").version("1.8.20")
        kotlin("android").version("1.8.20")
        id("com.android.application").version("7.4.2")
        id("com.android.library").version("7.4.2")
        id("org.jetbrains.compose").version("1.4.0")
    }
}

rootProject.name = "Displayer"

include(":android")
include(":desktop")
include(":common")
