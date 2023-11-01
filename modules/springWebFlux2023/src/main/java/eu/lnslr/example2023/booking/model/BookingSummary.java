package eu.lnslr.example2023.booking.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import eu.lunisolar.lava.lang.utils.ToString;
import lombok.*;

import java.util.List;

import static eu.lunisolar.lava.lang.utils.Collections4U.imm;

@Data
@Builder(toBuilder = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BookingSummary {

    private static final ToString<BookingSummary> TO_STRING = ToString.toStringBuilder(BookingSummary.class)
            .field("summaryByType")
            .build();

    private final List<TierSummary> summaryByType;

    private BookingSummary(@NonNull List<TierSummary> summaryByType) {
        this.summaryByType = imm().copyOf(summaryByType);
    }

    public static @NonNull BookingSummary bookingSummary(@NonNull List<TierSummary> summaryByType) {
        return new BookingSummary(summaryByType);
    }

    @Override public String toString() {return TO_STRING.toString(this);}

    // TODO builder: hide main property, expose nice add (either the @Singular or custom)
    // TODO prevent building bad (e.g. duplicated room type) summaries??
}

