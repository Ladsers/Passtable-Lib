import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
}
group = "com.ladsers.passtable"
version = "22.10.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.bouncycastle:bcpkix-jdk15on:1.66")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Jar>() {
    archiveFileName.set("passtable-lib-${project.version}.jar")
    from(sourceSets.main.get().allSource)

    manifest {
        attributes(
            mapOf(
                "Implementation-Title" to project.name,
                "Kotlin-Version" to kotlin.coreLibrariesVersion,
                "Implementation-Version" to project.version,
                "Build-Jdk" to java.targetCompatibility
            )
        )
    }

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") && it.name.contains("bcp") }
            .map { zipTree(it) }
    }) {
        exclude("META-INF/*.SF")
        exclude("META-INF/*.DSA")
        exclude("META-INF/*.RSA")
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}