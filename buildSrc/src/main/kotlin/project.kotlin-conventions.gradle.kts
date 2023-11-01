import org.gradle.kotlin.dsl.kotlin

plugins {
    id("project.root-conventions")
    id("project.java-conventions")
    kotlin("jvm")
}

dependencies {
    val deps = Deps(project)
    
    api(deps.libs["log4j-api-kotlin"])
}