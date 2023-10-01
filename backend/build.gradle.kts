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
    implementation(project(":library"))

    implementation("io.ktor:ktor-server-auth-jvm")
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-sessions-jvm")
    implementation("io.ktor:ktor-server-websockets-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-jackson-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-openapi")
    testImplementation("io.ktor:ktor-server-tests-jvm")

    implementation("com.zaxxer:HikariCP:$hikaricpVersion")
    runtimeOnly("org.postgresql:postgresql:$postgresVersion")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-json:$exposedVersion")

    implementation("io.insert-koin:koin-ktor:3.5.1")
}
