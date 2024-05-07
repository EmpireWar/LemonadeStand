plugins {
    // Apply the java Plugin to add support for Java.
    java
    id("org.jetbrains.kotlin.jvm")
    id("io.github.goooler.shadow")
    id("com.diffplug.spotless")
}

spotless {
    java {
        endWithNewline()
        removeUnusedImports()
        trimTrailingWhitespace()
        targetExclude("build/generated/**/*")
    }

    kotlinGradle {
        endWithNewline()
        indentWithSpaces(4)
        trimTrailingWhitespace()
    }
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") // Paper
    maven("https://repo.convallyria.com/snapshots/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

project.version = "1.0.1-SNAPSHOT"

dependencies {
    constraints {
        // Define dependency versions as constraints
        compileOnly("net.kyori:adventure-api:4.16.0")
        testImplementation("net.kyori:adventure-api:4.16.0")
    }

    implementation("io.javalin:javalin:5.3.2")
    implementation("club.minnced:discord-webhooks:0.8.2")
    compileOnly("org.spongepowered:configurate-yaml:4.1.2")
    testImplementation("org.spongepowered:configurate-yaml:4.1.2")
    compileOnly("net.kyori:adventure-api")
    testImplementation("net.kyori:adventure-api")
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use JUnit Jupiter test framework
            useJUnitJupiter("5.10.1")
        }
    }
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveBaseName.set("lemonadestand-${project.name}")
        archiveClassifier.set("")
    }
}