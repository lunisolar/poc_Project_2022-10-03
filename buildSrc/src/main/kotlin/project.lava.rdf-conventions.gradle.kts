
plugins {
    id("project.lava-conventions")
}

dependencies {

    implementation(Meta.Deps.lavaRdfJena)
    implementation(Meta.Deps.lavaRdfShaclJena)

    implementation(Meta.Deps.lavaRdfSpringStarter) {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
    
}
