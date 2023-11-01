plugins {
    id("project.java-conventions")
}

dependencies {
    val deps = Deps(project)

    implementation(deps.libs["lava-lang"])

    testImplementation(deps.libs["magma-asserts"])

}
