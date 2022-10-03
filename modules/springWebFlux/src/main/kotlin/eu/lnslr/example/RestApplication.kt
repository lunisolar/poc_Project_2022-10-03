package eu.lnslr.example

import eu.lnslr.example.executor.ExecutorConfiguration
import eu.lnslr.example.tasks.TasksConfiguration
import eu.lunisolar.lava.data.types.xsd.XS
import eu.lunisolar.lava.lang.utils.Collections4U
import eu.lunisolar.lava.meta.linked_data.SchemaLoader
import eu.lunisolar.lava.rdf.api.RdfManager
import eu.lunisolar.lava.rdf.spring.starter.RdfStarterProperties
import eu.lunisolar.magma.func.supp.Be
import eu.lunisolar.magma.func.supp.check.Checks.check
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.Import
import org.springframework.scheduling.annotation.EnableScheduling
import java.nio.file.Paths
import java.util.*

@SpringBootApplication
@Import(
    RestConfiguration::class,
    TasksConfiguration::class,
    ExecutorConfiguration::class
)
@EnableAspectJAutoProxy
@EnableScheduling
internal class RestApplication : Logging {


}

object log : Logging {

}

fun main(args: Array<String>) {

    SchemaLoader.initializeWithCcl();
    Collections4U.initializeWithCcl()
    RdfManager.initializeWithCcl()

    val path = Paths.get("").toAbsolutePath().toString()
    log.logger.info("Starting in: $path")

    val urlPath = path.replace("\\", "/")

    var dsUrl = "lava-rdf:jena:tdb2:file:///$urlPath/TDB2/"

    log.logger.info("Triplestore: $dsUrl")

    // just to load XS
    check(XS.DURATION.identity()).`must$`(Be::`equal$`, "http://www.w3.org/2001/XMLSchema#duration")

    // lava-rdf:jena:tdb2:file:///D:/p/.r/tech.lnkd.editor.intellij/

    runApplication<RestApplication>(*args) {
        val props = Properties()
        props[RdfStarterProperties.CONNECTION_URL] = dsUrl
        setDefaultProperties(props)
    }
}
