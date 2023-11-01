package eu.lnslr.example2023;

import eu.lunisolar.magma.func.supplier.LSupplier;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.TEXT_HTML;

@Configuration
public class RestConfiguration {

    @Bean
    public LSupplier<String> helloMessage() {
        return () -> """
                     <h1>Hello from 2023</h1>
                     """;
    }

    @Bean
    public RouterFunction<ServerResponse> route(@NonNull LSupplier<String> helloMessage) {
        return RouterFunctions.route().GET("/", request -> {
            return ServerResponse.ok().contentType(TEXT_HTML).bodyValue(helloMessage.get());
        }).build();
    }
}
