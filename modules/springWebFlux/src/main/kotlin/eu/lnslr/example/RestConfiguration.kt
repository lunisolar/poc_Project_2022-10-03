package eu.lnslr.example

import eu.lunisolar.magma.func.supplier.LSupplier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType.TEXT_HTML
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router


@Configuration
internal class RestConfiguration {

    @Bean
    fun helloMessage() = LSupplier {
        """
        <h1>Hello</h1>
        """.trimIndent()
    }

    @Bean
    fun route() = router {
        GET("/") {
            ServerResponse.ok().contentType(TEXT_HTML).bodyValue(helloMessage().get()!!) // helloMessage does not need to be a bean
        }
    }
}