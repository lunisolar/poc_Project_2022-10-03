import java.io.File
import java.nio.file.Path

object EmbededRepo {

    fun embededRepo(rootDir: Path): String = "file:///${rootDir.toString().replace("\\", "/")}/mavenRepo"
    fun embededRepo(rootDir: File): String = embededRepo(rootDir.toPath())


}