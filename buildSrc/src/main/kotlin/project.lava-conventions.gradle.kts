import gradle.kotlin.dsl.accessors._c447f7ce986766164e175091c72cd8d0.testImplementation

plugins {
    id("project.java-conventions")
}

dependencies {

    implementation(Meta.Deps.lavaLang)

    implementation(Meta.Deps.lavaPomJena)

    testImplementation(Meta.Deps.lavaLangTest) {
        exclude(group = "org.codehaus.groovy")
        exclude(group = "org.spockframework")
    }

}
