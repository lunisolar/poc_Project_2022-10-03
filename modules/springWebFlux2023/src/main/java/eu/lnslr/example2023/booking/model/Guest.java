package eu.lnslr.example2023.booking.model;

import eu.lunisolar.lava.lang.utils.ToString;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class Guest {

    private static final ToString<Guest> TO_STRING = ToString.toStringBuilder(Guest.class)
            .field("preferredPrice")
            .build();

    private final BigDecimal preferredPrice;

    public static @NonNull Guest.B guest()                        {return Guest.builder();}
    public static @NonNull Guest guest(BigDecimal preferredPrice) {return new Guest(preferredPrice);}
    public static @NonNull Guest guest(double preferredPrice)     {return new Guest(BigDecimal.valueOf(preferredPrice));}

    @Override public String toString()                            {return TO_STRING.toString(this);}

    public static class B {

        public @NonNull B preferredPrice(BigDecimal preferredPrice) {
            this.preferredPrice = preferredPrice;
            return this;
        }

        public @NonNull B preferredPrice(double dbl) {return preferredPrice(BigDecimal.valueOf(dbl));}

    }
}
