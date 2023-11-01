package eu.lnslr.example2023.booking.service;

import eu.lnslr.example2023.booking.model.Accommodations;
import eu.lnslr.example2023.booking.model.BookingSummary;
import eu.lnslr.example2023.booking.model.Guest;
import eu.lnslr.example2023.booking.model.RoomTier;
import eu.lnslr.example2023.booking.resolver.BookingResolver;
import eu.lunisolar.magma.basics.exceptions.Handling;
import eu.lunisolar.magma.func.function.LFunction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.EntityResponse;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

import static eu.lunisolar.lava.lang.tasks.Result.onVoid;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ResolverHandler {

    private final BookingResolver bookingResolver;
    private final List<Guest>     strangelyConstantGuestList;

    public Mono<ServerResponse> resolve(ServerRequest request) {

        // localhost:8080/booking?premium=2&economy=2

        // TODO lava: why there is resultFrom(CompletableFuture) and not aty all resultFrom(Future) ?
        // TODO lava: transition Result-> Future
        // TODO lava: transition Result-> Mono

        var accommodations = accommodationsFromRequest(request);

        var result = bookingResolver.execute(accommodations, strangelyConstantGuestList)
                .replaceResult(onVoid(), () -> {
                    throw Handling.create(IllegalStateException::new, "Failed to find solution (and implementation failed to intercept this case).");
                });

        if (result.isFailure()) {
            return Mono.error(result.failure());
        } else {
            var inserter = BodyInserters.fromValue(result.get());
            return ServerResponse.ok().body(inserter);
        }

    }

    private static Accommodations accommodationsFromRequest(ServerRequest request) {
        var accommodations = Accommodations.accommodations();

        for (RoomTier roomTier : RoomTier.list()) {
            request.queryParam(roomTier.name().toLowerCase()).ifPresent(value -> {
                var rooms = LFunction.tryApplyThen(value, Integer::parseInt, e -> 0);
                accommodations.add(roomTier, Integer.parseInt(value));
            });
        }
        return accommodations.build();
    }

}
