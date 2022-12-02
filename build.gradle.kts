plugins {
    // trick: for the same plugin versions in all sub-modules
    id("com.android.library").version("7.3.1").apply(false)
    kotlin("multiplatform").version("1.7.10").apply(false)
    id("org.jetbrains.kotlin.jvm") version "1.7.20" apply false
    id("org.jetbrains.kotlin.android") version "1.7.20" apply false

    id(BuildPlugins.ktLint) version Versions.ktLint
    id(BuildPlugins.detekt) version (Versions.detekt)
}

subprojects {
    apply(plugin = BuildPlugins.ktLint)
    ktlint {
        debug.set(true)
        verbose.set(true)
        android.set(false)
        outputToConsole.set(true)
        outputColorName.set("RED")
        filter {
            enableExperimentalRules.set(true)
            exclude { projectDir.toURI().relativize(it.file.toURI()).path.contains("/generated/") }
            include("**/kotlin/**")
        }
    }

    apply(plugin = BuildPlugins.detekt)
    detekt {
        parallel = true
        config = files("${project.rootDir}/config/detekt/detekt.yml")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
