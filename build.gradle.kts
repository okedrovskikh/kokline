plugins {
    kotlin("jvm") version "1.9.10" apply false
    kotlin("js") version "1.9.10" apply false
}

allprojects {
    group = "kek.team"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}
