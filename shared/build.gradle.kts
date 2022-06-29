plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("maven-publish")
}

group = "gg.heimdall"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.3")
}

publishing {
    publications {
        create<MavenPublication>("shared") {
            groupId = "me.devwckd.mcd_service"
            artifactId = "shared"
            version = project.version as String?

            from(components["java"])
        }
    }
}