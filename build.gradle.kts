val kotlinVersion: String by project
val logbackVersion: String by project

plugins {
    kotlin("jvm") version "1.9.10"
}

allprojects {
    group = "kek.team"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    dependencies {
        implementation(group = "ch.qos.logback", name = "logback-classic", version = logbackVersion)

        testImplementation(group = "org.jetbrains.kotlin", name = "kotlin-test-junit", version = kotlinVersion)
        testImplementation(group = "io.kotest", name = "kotest-assertions-core", version = "4.6.2")
    }
}