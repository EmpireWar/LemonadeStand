pluginManagement {
    // Include 'plugins build' to define convention plugins.
    includeBuild("build-logic")

    repositories {
        maven("https://repo.spongepowered.org/repository/maven-public/")
    }
}

plugins {
    // Apply the foojay-resolver plugin to allow automatic download of JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "LemonadeStand"
include("common", "paper", "api:paper-api", "sponge", "api:sponge-api")
