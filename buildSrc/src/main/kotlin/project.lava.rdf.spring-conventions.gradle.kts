
plugins {
    id("project.lava.rdf-conventions")
}

dependencies {
    val deps = Deps(project)

    implementation(deps.libs["lava-rdf-spring-starter"]) {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }

}
