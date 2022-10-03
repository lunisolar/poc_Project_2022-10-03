import java.nio.file.Path

//<editor-fold desc="header of sorts">
fun present(text: String) {
    println("buildSrc/src/main/kotlin/project.root-conventions.gradle.kts: $text")
}

present("workdir: ${Path.of("").toAbsolutePath()}")
present("rootDir: ${rootDir}")
//</editor-fold>

val embededMavenRepo = EmbededRepo.embededRepo(rootDir)

present("Going to use maven repository in project: $embededMavenRepo")

println()

repositories {
//    maven {
//        url = uri(embededMavenRepo)
//    }
    mavenLocal()

    gradlePluginPortal()
    mavenCentral()
}