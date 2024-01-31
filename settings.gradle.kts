pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    plugins {
        kotlin("multiplatform").version("1.9.21")
        kotlin("plugin.serialization").version("1.9.21")
        kotlin("android").version("1.9.21")
        id("com.android.application").version("8.2.2")
        id("com.android.library").version("8.2.2")
        id("org.jetbrains.compose").version("1.5.11")
    }
}

rootProject.name = "Displayer"

include(":android")
include(":desktop")
include(":common")
