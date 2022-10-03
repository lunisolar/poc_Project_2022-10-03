import eu.lunisolar.lava.lang.seq.Seq
import eu.lunisolar.lava.rdf.api.lang.SerializationType
import eu.lunisolar.lava.rdf.to.java.GenerateClass
import java.io.InputStream
import java.io.OutputStream
import java.util.stream.Stream

plugins {
    id("project.jar-conventions")
    id("project.java-conventions")
    id("project.lava.rdf-conventions")
    id("project.maven-publishing")
}

println("JAVA: $rootDir")

buildscript {
    repositories {

//        maven {
//            url = uri(EmbededRepo.embededRepo(rootDir))
//        }
        mavenLocal()

        gradlePluginPortal()
        mavenCentral()
    }

    dependencies {
        classpath(Meta.Deps.lavaLang)
        classpath(Meta.Deps.lavaRdfJena)
        classpath(Meta.Deps.lavaPomJena)
        classpath(Meta.Deps.lavaRdfToJava)
    }
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}


val generatedSchemas = "$buildDir/generated-schemas/main/java"
val targetPackage = "eu.lnslr.example.schema"
val targetRelativeDir = targetPackage.replace('.', '/')
val targetDir = File("$generatedSchemas/$targetRelativeDir")


val ttlTask = tasks.register<DefaultTask>("processTtlFiles") {

    outputs.dir(generatedSchemas)

    doLast {

        delete(fileTree(targetDir).include("/"))
        val ttlFiles = fileTree("src/main/resources").matching { include("**/*.ttl").exclude("**/Example.ttl") }.files.forEach { file ->
            val name = file.name.replace("\\.[^\\.]+$".toRegex(), "")

            Seq.of(GenerateClass.FileType.NS, GenerateClass.FileType.RS).forEach {
                val outFile = File(targetDir, name + it.fileNameSuffix() + ".java")
                println("Going to process: " + file.path + "\n              --> " + outFile.path)

                println("              ==> " + outFile.parent)
                File(outFile.parent).mkdirs();


                var outStr: java.io.OutputStream?  = null
                var inStr: java.io.InputStream?    = null

                try {
                    outFile.createNewFile();
                    outStr = outFile.outputStream()
                    inStr = file.inputStream()
                    val className = GenerateClass.execute(inStr, SerializationType.TTL, outStr, targetPackage, null, it, true);
                    outStr.close()
                    outStr = null
                    val newFile = File(targetDir, className + ".java")
                    outFile.renameTo(newFile)

                    println("              ==> " + newFile.path + "\n")
                } catch (e: Exception) {
                    inStr?.close()
                    outStr?.close()
                    throw e;
                }
            }
        }
    }
}


sourceSets["main"].java.srcDirs(
    ttlTask
)