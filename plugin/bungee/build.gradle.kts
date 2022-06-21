val ktor_version: String by project

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("net.minecrell.plugin-yml.bungee") version "0.5.2"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
}

dependencies {
    compileOnly("net.md-5:bungeecord-api:1.18-R0.1-SNAPSHOT")
    implementation(project(":shared"))
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-client-websockets:$ktor_version")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bungeecord-api:2.2.0")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bungeecord-core:2.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")
    implementation("com.github.oshi:oshi-core:6.1.6")
    implementation("com.tinder.statemachine:statemachine:0.2.0")
}

bungee {
    name = "McdServiceConnector"
    author = "devwckd"
    main = "me.devwckd.mcd_service.bungee.McdServicePlugin"
}