import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":common"))
                implementation(compose.desktop.currentOs)

                // https://ajalt.github.io/clikt/changelog/
                implementation("com.github.ajalt.clikt:clikt:3.5.0")

                implementation("io.insert-koin:koin-core:" + extra["dep.koin.version"] as String)

                implementation("io.ktor:ktor-server-core:" + extra["dep.ktor.version"] as String)
                implementation("io.ktor:ktor-server-cio:" + extra["dep.ktor.version"] as String)
                implementation("org.slf4j:slf4j-nop:1.7.21")

            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Displayer"
            packageVersion = "1.0.0"
            linux {
                iconFile.set(project.file("icon.png"))
            }
        }
    }
}
