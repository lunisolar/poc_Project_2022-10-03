import org.gradle.api.JavaVersion

plugins {
    id("project.root-conventions")
    id("project.lombok-conventions")
    java
}

java {
    modularity.inferModulePath.set(false)

    sourceCompatibility = Project2022.javaVersion
    targetCompatibility = Project2022.javaVersion
}

tasks.compileJava {
    modularity.inferModulePath.set(false)
}
