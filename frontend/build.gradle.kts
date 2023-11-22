val ktorVersion: String by project

plugins {
    kotlin("js")
}

kotlin {
    js {
        browser {
            commonWebpackConfig(Action {
                cssSupport {
                    enabled.set(true)
                }
            })
        }
        binaries.executable()
    }
}

dependencies {
    implementation(enforcedPlatform("org.jetbrains.kotlin-wrappers:kotlin-wrappers-bom:1.0.0-pre.430"))
    implementation("org.jetbrains.kotlin-wrappers:kotlin-react")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom")

    implementation("org.jetbrains.kotlin-wrappers:kotlin-emotion")

    implementation(group = "io.ktor", name = "ktor-client-core", version = ktorVersion)
    implementation(group = "io.ktor", name = "ktor-client-auth", version = ktorVersion)

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
}
