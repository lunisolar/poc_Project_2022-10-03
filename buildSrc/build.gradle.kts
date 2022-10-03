plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

object Java {
    val versionStr = "17"
    val version = JavaVersion.toVersion(versionStr)
}

var generatedMeta = "build/generated-meta/src/main"

dependencies {

    val pluginListFile = File("$rootDir/$generatedMeta/resources/plugin.list")
    if (!pluginListFile.exists()) throw IllegalStateException("plugin.list does not exists: $pluginListFile")

    pluginListFile.forEachLine {
        implementation(it)
    }

}

java {
    modularity.inferModulePath.set(false)
    sourceCompatibility = Java.version
    targetCompatibility = Java.version
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = Java.versionStr
}

sourceSets["main"].java { srcDir("$generatedMeta/kotlin") }
