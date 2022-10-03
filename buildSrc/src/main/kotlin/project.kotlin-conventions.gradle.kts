import org.gradle.kotlin.dsl.kotlin

plugins {
    id("project.root-conventions")
    id("project.java-conventions")
    kotlin("jvm")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = Meta.Java.versionStr
}

dependencies {
    implementation (Meta.Deps.log4j2kotlin)
}