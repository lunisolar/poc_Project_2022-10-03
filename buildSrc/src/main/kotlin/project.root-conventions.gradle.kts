val embeddedMavenRepo = EmbeddedRepo.embeddedRepo(rootDir)

repositories {
    maven {
        url = uri(embeddedMavenRepo)
    }
    mavenLocal()

    gradlePluginPortal()
    mavenCentral()
}