package eu.lnslr.example2023.booking.resolver;

import eu.lnslr.example2023.booking.model.Accommodations;
import eu.lnslr.example2023.booking.model.BookingSummary;
import eu.lnslr.example2023.booking.model.Guest;
import eu.lunisolar.lava.lang.tasks.Result;
import lombok.NonNull;

import java.util.List;

public interface BookingResolver {

    @NonNull Result<BookingSummary> execute(@NonNull Accommodations accommodations, @NonNull List<Guest> guests);

}
