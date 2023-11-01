import java.io.File
import java.nio.file.Path

object EmbeddedRepo {

    fun embeddedRepo(rootDir: Path): String = "file:///${rootDir.toString().replace("\\", "/")}/mavenRepo"
    fun embeddedRepo(rootDir: File): String = embeddedRepo(rootDir.toPath())


}