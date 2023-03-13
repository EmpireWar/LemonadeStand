plugins {
    java
    kotlin("jvm") version "1.8.10"
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "8.1.0"
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")
    implementation("io.javalin:javalin:5.3.2")
    implementation("club.minnced:discord-webhooks:0.8.2")
}

group = "org.empirewar.lemonadestand"
version = "1.0.1-SNAPSHOT"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    compileJava {
        options.encoding = "UTF-8"
    }

    processResources {
        filesMatching("plugin.yml") {
            expand("version" to version)
        }
    }

    shadowJar {
        archiveClassifier.set("")
    }
}

publishing {
    publications {
        create<MavenPublication>("lemonadestand") {
            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "snapshots"
            url = uri("https://repo.convallyria.com/snapshots/")
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
}