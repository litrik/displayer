pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    plugins {
        kotlin("multiplatform").version(extra["dep.kotlin.version"] as String)
        kotlin("plugin.serialization").version(extra["dep.kotlin.version"] as String)
        kotlin("android").version(extra["dep.kotlin.version"] as String)
        id("com.android.application").version(extra["dep.agp.version"] as String)
        id("com.android.library").version(extra["dep.agp.version"] as String)
        id("org.jetbrains.compose").version(extra["dep.compose.version"] as String)
    }
}

rootProject.name = "Displayer"

include(":android", ":desktop", ":common")
