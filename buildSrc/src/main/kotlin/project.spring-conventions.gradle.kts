plugins {
    id("project.root-conventions")
    id("project.jar-conventions")
    id("project.java-conventions")
    id("project.kotlin-conventions")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("plugin.spring")
}

configurations {
     all {
        exclude ("org.springframework.boot", "spring-boot-starter-logging")
    }
}

