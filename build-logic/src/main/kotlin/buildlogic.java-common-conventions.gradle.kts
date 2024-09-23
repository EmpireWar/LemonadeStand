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
}

project.version = "1.0.1-SNAPSHOT"

dependencies {
    implementation("io.javalin:javalin:6.2.0") {
        exclude("org.slf4j")
    }
    implementation("club.minnced:discord-webhooks:0.8.2") {
        exclude("org.slf4j")
    }
    compileOnly("org.slf4j:slf4j-api:2.0.12")
    testImplementation("org.slf4j:slf4j-api:2.0.12")
    compileOnly("com.google.code.gson:gson:2.10.1")
    testImplementation("com.google.code.gson:gson:2.10.1")
    compileOnly("org.spongepowered:configurate-yaml:4.1.2")
    testImplementation("org.spongepowered:configurate-yaml:4.1.2")
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
        mergeServiceFiles()
        archiveBaseName.set("lemonadestand-${project.name}")
        archiveClassifier.set("")
        relocate("club.minnced", "lemonadestand.libs.discord")
        relocate("io.javalin", "lemonadestand.libs.javalin")
        relocate("jakarta.servlet", "lemonadestand.libs.jakarta")
        relocate("javax.servlet", "lemonadestand.libs.javax")
        relocate("kotlin", "lemonadestand.libs.kotlin")
        relocate("okhttp3", "lemonadestand.libs.okhttp3")
        relocate("okio", "lemonadestand.libs.okio")
        relocate("org.eclipse.jetty", "lemonadestand.libs.jetty")
        relocate("org.intellij.lang.annotations", "lemonadestand.libs.annotations")
        relocate("org.jetbrains.annotations", "lemonadestand.libs.jannotations")
        relocate("org.json", "lemonadestand.libs.json")
    }
}