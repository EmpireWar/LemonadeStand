import org.spongepowered.gradle.plugin.config.PluginLoaders
import org.spongepowered.plugin.metadata.model.PluginDependency

plugins {
    id("buildlogic.java-common-conventions")
    id("org.spongepowered.gradle.plugin") version("2.2.0")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":api:sponge-api"))
}

sponge {
    apiVersion("11.0.0-SNAPSHOT")
    loader {
        name(PluginLoaders.JAVA_PLAIN)
        version("1.0.0-SNAPSHOT")
    }
    plugin("lemonadestand") {
        displayName("LemonadeStand")
        entrypoint("org.empirewar.lemonadestand.sponge.LemonadeStandSponge")
        description("Accept incoming Ko-Fi webhooks and allow further processing.")
        license("GPL-3.0")
        version(project.version.toString())
        dependency("spongeapi") {
            loadOrder(PluginDependency.LoadOrder.AFTER)
            optional(false)
        }
        contributors {
            contributor("SamB440") {}
            contributor("StealWonders") {}
        }
    }
}
