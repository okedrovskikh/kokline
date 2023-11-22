plugins {
    kotlin("js")
}

// TODO посмотреть как все это настроить
// https://stackoverflow.com/questions/63602921/kotlin-js-different-output-js-files-for-different-modules
kotlin {
    js {
        moduleName = "users"
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
