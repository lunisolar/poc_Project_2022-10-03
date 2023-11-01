
plugins {
    java
    id("project.java-conventions")
}


tasks.jar {

    manifest {
        attributes(
            mapOf(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version,
                "Implementation-Vendor" to "Lunisolar"
            )
        )
    }

    // Fixes issue that generated JAR does not include pom.xml (for documentation purposes)
    val publicationTaskName = "generatePomFileFor${Project2022.publicationName}Publication";
    tasks.findByName(publicationTaskName)?.let { task ->
        // By default, JAR does not include pom.
        into("META-INF/maven/${project.group}/${project.name}") {
            from(task)
            rename { fileName: String -> fileName.replace("pom-default.xml", "pom.xml") }
        }
    }


}