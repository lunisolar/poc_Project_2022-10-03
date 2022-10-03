
plugins {
    id("project.lava.rdf-conventions")
}

dependencies {

    implementation(Meta.Deps.lavaRdfSpringStarter) {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
    
}
