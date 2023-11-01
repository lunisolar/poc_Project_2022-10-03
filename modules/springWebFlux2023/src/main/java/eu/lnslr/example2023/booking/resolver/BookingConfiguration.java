package eu.lnslr.example2023.booking.resolver;

import eu.lnslr.example2023.booking.model.Guest;
import eu.lnslr.example2023.booking.model.RoomTier;
import eu.lunisolar.lava.lang.seq.Seq;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static eu.lnslr.example2023.booking.model.RoomTier.ECONOMY;
import static eu.lnslr.example2023.booking.model.RoomTier.PREMIUM;
import static eu.lunisolar.lava.lang.utils.Collections4U.immMap;
import static java.math.BigDecimal.valueOf;

@Configuration
public class BookingConfiguration {

    @Bean Map<RoomTier, BigDecimal> tierSetup() {
        return immMap().ofEntries(
                Map.entry(PREMIUM, valueOf(100)),
                Map.entry(ECONOMY, valueOf(0))
        );
    }

    @Bean UpgradePolicy upgradePolicy() {
        return UpgradePolicies.fairUpgradePolicy();
    }

    @Bean BookingResolver bookingResolver(@NonNull Map<RoomTier, BigDecimal> tierSetup, @NonNull UpgradePolicy upgradePolicy) {
        return StandardResolver.resolver(tierSetup, upgradePolicy);
    }


}
