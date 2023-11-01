package eu.lnslr.example2023.booking.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import eu.lunisolar.lava.lang.utils.ToString;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class TierSummary {

    private static final ToString<TierSummary> TO_STRING = ToString.toStringBuilder(TierSummary.class)
            .field("roomTier")
            .field("count")
            .field("price")
            .build();

    private final RoomTier   roomTier;
    private final int        count;
    private final BigDecimal price;

    public static @NonNull TierSummary typeSummary(@NonNull RoomTier roomTier, int count, @NonNull BigDecimal price) {
        return new TierSummary(roomTier, count, price);
    }

    @Override public String toString() {return TO_STRING.toString(this);}
}
