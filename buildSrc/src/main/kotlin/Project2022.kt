import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType

class DepsVs(private val vs: VersionCatalog) {

    fun library(name: String) = library(vs, name)

    private fun library(vc: VersionCatalog, name: String): Provider<MinimalExternalModuleDependency> =
        vc.findLibrary(name).orElseThrow { RuntimeException("Library not found: $name") }

    operator fun get(name: String) = library(name)

}

class Deps(private val project: Project) {

    val libs get() = DepsVs(project.extensions.getByType<VersionCatalogsExtension>().named("libs"))

}

object Project2022 {

    val javaVersion = JavaVersion.toVersion("17")

    val publicationName = "Project2022Publication"

}