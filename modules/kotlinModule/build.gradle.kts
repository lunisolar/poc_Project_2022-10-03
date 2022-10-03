import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("project.kotlin-conventions")
    id("project.maven-publishing")
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = Meta.Java.versionStr
}