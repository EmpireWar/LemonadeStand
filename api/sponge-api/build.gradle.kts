plugins {
    id("buildlogic.java-publish-conventions")
}

dependencies {
    compileOnly("org.spongepowered:spongeapi:18.0.0-SNAPSHOT")
    api(project(":common"))
}
