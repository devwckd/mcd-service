val ktor_version: String by project

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("maven-publish")
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.18.1-R0.1-SNAPSHOT")
    implementation(project(":shared"))
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.2.0")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")
    implementation("com.github.oshi:oshi-core:6.1.6")
    implementation("de.halfbit:comachine-jvm:1.0-SNAPSHOT")
}

bukkit {
    name = "McdServiceConnector"
    author = "devwckd"
    main = "me.devwckd.mcd_service.spigot.McdServicePlugin"
    apiVersion = "1.18"
}

publishing {
    publications {
        create<MavenPublication>("spigot") {
            groupId = "me.devwckd.mcd_service.plugin"
            artifactId = "spigot"
            version = rootProject.version as String

            from(components["java"])
        }
    }
}