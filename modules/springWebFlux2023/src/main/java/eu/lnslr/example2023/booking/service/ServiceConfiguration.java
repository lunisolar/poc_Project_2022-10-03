package eu.lnslr.example2023.booking.service;

import eu.lnslr.example2023.booking.model.Guest;
import eu.lnslr.example2023.booking.resolver.BookingResolver;
import eu.lunisolar.lava.lang.seq.Seq;
import eu.lunisolar.magma.func.supplier.LSupplier;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.List;

import static org.springframework.http.MediaType.TEXT_HTML;

@Configuration
public class ServiceConfiguration {

    @Bean List<Guest> strangelyConstantGuestList() {
        return Seq.ofDbl(23, 45, 155, 374, 22, 99.99, 100, 101, 115, 209)
                .mapToObj(i -> Guest.guest().preferredPrice(i).build())
                .toList();
    }

    @Bean RouterFunction<ServerResponse> tasksRouter(@NonNull ResolverHandler handler) {
        return RouterFunctions.route()
                .GET("/booking", handler::resolve)
                .build();
    }

    @Bean ResolverHandler resolverHandler(@NonNull BookingResolver bookingResolver, @NonNull List<Guest> strangelyConstantGuestList) {
        return new ResolverHandler(bookingResolver, strangelyConstantGuestList);
    }

}
