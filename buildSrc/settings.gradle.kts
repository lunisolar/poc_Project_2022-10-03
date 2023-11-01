rootProject.name = "Project_2022-10-03-BuildSrc"

pluginManagement {
    repositories {
        mavenLocal()  // for local builds of lunisolar-lava
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://cache-redirector.jetbrains.com/intellij-repository/snapshots") }
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()  // for local builds of lunisolar-lava
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://cache-redirector.jetbrains.com/intellij-repository/snapshots") }
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}