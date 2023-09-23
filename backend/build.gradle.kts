val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project

val postgresVersion: String by project
val exposedVersion: String by project
val hikaricpVersion: String by project

plugins {
    kotlin("jvm")
    id("io.ktor.plugin") version "2.3.4"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.10"
}

application {
    mainClass = "kek.team.kokline.MainKt"
}

dependencies {
    implementation("io.ktor:ktor-server-auth-jvm")
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-sessions-jvm")
    implementation("io.ktor:ktor-server-websockets-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-jackson-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    testImplementation("io.ktor:ktor-server-tests-jvm")

    implementation("org.liquibase:liquibase-core:4.23.2")
    implementation("com.zaxxer:HikariCP:$hikaricpVersion")
    runtimeOnly("org.postgresql:postgresql:$postgresVersion")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
}
