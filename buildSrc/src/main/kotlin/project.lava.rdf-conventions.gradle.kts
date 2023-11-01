
plugins {
    id("project.lava-conventions")
}

dependencies {

    val deps = Deps(project)

    implementation(deps.libs["lava-rdf-jena"])
    implementation(deps.libs["lava-rdf-shacl-jena"])

}
