package eu.lnslr.example2023.booking.model;

import eu.lunisolar.lava.lang.utils.ToString;
import eu.lunisolar.magma.basics.exceptions.X;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.Map;

import static eu.lunisolar.lava.lang.utils.Collections4U.immutableMap;
import static eu.lunisolar.lava.lang.utils.Collections4U.map;
import static eu.lunisolar.magma.func.supp.opt.Opt.of;

@Data
@Builder(toBuilder = true)
public class Accommodations {

    private static final ToString<Accommodations> TO_STRING = ToString.toStringBuilder(Accommodations.class)
            .field("availability")
            .build();

    private final @NonNull Map<RoomTier, Integer> availability;

    private Accommodations(@NonNull Map<RoomTier, Integer> configuration) {
        this.availability = immutableMap().unmodifiable().copyOf(configuration);
    }

    public static Accommodations.B accommodations() {return Accommodations.builder();}
    public int availability(@NonNull RoomTier type) {return availability.get(type);}
    @Override public String toString()              {return TO_STRING.toString(this);}

    public static class B {

        private B availabilityPerTier(@NonNull Map<RoomTier, Integer> availabilityPerTier) {
            throw X.unsupported("Hiding some of Lombok generated code, while keeping the rest.");
        }

        private @NonNull Map<RoomTier, Integer> availableMap() {
            if (availability == null) availability = map().empty();
            return availability;
        }

        public B add(@NonNull RoomTier type, int count) {
            var map = availableMap();
            map.compute(type, (t, current) -> of(current).mapWith(count, Integer::sum).orElse(count));
            return this;
        }


    }

}
