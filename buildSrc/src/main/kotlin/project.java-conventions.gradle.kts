
plugins {
    id("project.root-conventions")
    java
}

java {
    modularity.inferModulePath.set(false)
    sourceCompatibility = Meta.Java.version
    targetCompatibility = Meta.Java.version
}

tasks.compileJava {
    modularity.inferModulePath.set(false)
}
