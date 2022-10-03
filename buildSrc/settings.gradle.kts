import java.nio.file.Path

//<editor-fold desc="header of sorts">
fun present(text: String) {
    println("buildSrc/settings.gradle.kts: $text")
}

present("workdir: ${Path.of("").toAbsolutePath()}")
present("rootDir: ${rootDir}")
//</editor-fold>

val indent = "    "
var generatedMeta = "build/generated-meta/src/main"

val generatedMetaKotlinDir = prepareDir("$generatedMeta/kotlin")
val generatedMetaResourcesDir = prepareDir("$generatedMeta/resources")

val metaJava = "17"
val metaKotlin = "1.7.20"
val metaSpringBoot = "2.7.4"
val metaSpringDepMgmt = "1.0.14.RELEASE"
val metaMagma = "4.0.0-SNAPSHOT"
val metaLava = "0.2-SNAPSHOT"

val pluginsMeta = linkedMapOf(
//    "spring_boot" to "org.springframework.boot:spring-boot-gradle-plugin:\${Version.spring_boot}",
//    "kotlin" to "org.jetbrains.kotlin:kotlin-allopen:1.7.20:\${Version.kotlin}"

    "kotlin" to "org.jetbrains.kotlin:kotlin-gradle-plugin:$metaKotlin",
    "kotlinAllOpen" to "org.jetbrains.kotlin:kotlin-allopen:$metaKotlin",
    "lombok" to "io.freefair.gradle:lombok-plugin:6.5.0.3",
    "springBoot" to "org.springframework.boot:spring-boot-gradle-plugin:$metaSpringBoot",
    "springDepMgt" to "io.spring.gradle:dependency-management-plugin:$metaSpringDepMgmt",
)

createMetaFile(
    generatedMetaKotlinDir,
    "Meta.kt",
    """
        import org.gradle.api.JavaVersion
    """.trimIndent(),
    Pair(
        "Version", linkedMapOf(
            "java" to metaJava,
            "kotlin" to metaKotlin,

            "spring_boot" to metaSpringBoot,
            "spring_dependencyManagement" to metaSpringDepMgmt,

            "lava" to metaLava,

            )
    ),
    Pair(
        "Deps", linkedMapOf(
            "lavaLang" to "eu.lunisolar.lava:lava-lang:$metaLava",
            "lavaRdfJena" to "eu.lunisolar.lava:lava-rdf-jena:$metaLava",
            "lavaRdfShaclJena" to "eu.lunisolar.lava:lava-rdf-shacl-jena:$metaLava",
            "lavaRdfReactor" to "eu.lunisolar.lava:lava-rdf-reactor:$metaLava",
            "lavaPomJena" to "eu.lunisolar.lava:pom-jena:$metaLava",
            "lavaLangTest" to "eu.lunisolar.lava:lava-lang-test:$metaLava",
            "lavaPomTest" to "eu.lunisolar.lava:pom-test:$metaLava",
            "lavaRdfSpringStarter" to "eu.lunisolar.lava:lava-rdf-spring-starter:$metaLava",
            "lavaRdfToJava" to "eu.lunisolar.lava:lava-rdf-to-java:$metaLava",
            "magmaAsserts" to "eu.lunisolar.magma:magma-asserts:$metaMagma",
            "log4j2kotlin" to "org.apache.logging.log4j:log4j-api-kotlin:1.2.0"
        )
    ),
    Pair(
        "Plugins", pluginsMeta
    ),
    Pair(
        "Build", linkedMapOf(
            "publicationName" to "MavenJava"
        )
    ),

    custom = """
        object Java {
            val versionStr = Meta.Version.java
            val version = JavaVersion.toVersion(versionStr)
        }
    """.trimIndent()
)

val pluginListFile = File("$generatedMetaResourcesDir/plugin.list")
pluginListFile.writeText(pluginsMeta.asSequence().map { entry -> entry.value }.joinToString("\n"))

present("Created file: $pluginListFile")


//<editor-fold desc="Meta">

fun prepareDir(relativepath: String): File {
    val dir = rootDir.resolve(relativepath)

    if (dir.exists()) dir.deleteRecursively()
    if (dir.exists()) throw IllegalStateException("Unsuccessful delete: $dir")

    dir.mkdirs();
    if (!dir.exists()) throw IllegalStateException("Unsuccessful mkdir: $dir")

    return dir;
}


fun valueObject(indent: String, objectName: String, vals: Map<String, String>): String {
    return "${indent}object $objectName {\n${vals.asSequence().map { entry -> "$indent   val ${entry.key} = \"${entry.value}\"" }.joinToString("\n")}\n$indent}"
}

fun createMetaFile(dir: File, fileName: String, imports: String = "", vararg sections: Pair<String, Map<String, String>>, custom: String = ""): File {
    if (dir.isFile) throw IllegalStateException("$dir is not directory.")
    var text = "$imports\nobject Meta {\n\n";

    sections.forEach {
        text += valueObject(indent, it.first, it.second)
        text += "\n\n"
    }

    text += custom.split("\n").asSequence().map { line -> indent + line }.joinToString("\n")
    text += "\n}"

    val file = File("$dir/$fileName")
    file.writeText(text)

    present("Created file: $file")

    return file;
}


//</editor-fold>