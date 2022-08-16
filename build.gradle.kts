import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
}
group = "com.ladsers.passtable"
version = "22.8.0"

repositories {
    mavenCentral()
}

dependencies{
    implementation("org.bouncycastle:bcpkix-jdk15on:1.66")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Jar>() {
    archiveFileName.set("passtable-lib-${version}.jar")
}