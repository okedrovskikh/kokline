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
        implementation("ch.qos.logback:logback-classic:$logbackVersion")

        testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
        testImplementation(group = "io.kotest", name = "kotest-assertions-core", version = "4.6.2")
    }
}