package eu.lnslr.example2023.booking.model;

import eu.lunisolar.magma.func.supp.Have;
import eu.lunisolar.magma.func.supp.Predicates.P;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static eu.lnslr.example2023.booking.model.Accommodations.accommodations;
import static eu.lnslr.example2023.booking.model.RoomTier.*;
import static eu.lunisolar.magma.func.supp.check.Checks.attest;

class AccommodationsTest {

    @Test void addingRooms() {
        attest(
                accommodations()
                        .add(ECONOMY, 3)
                        .add(PREMIUM, 3)
                        .add(ECONOMY, 5)
                        .build()
        )
                .mustBeNotNull()
                .check(Accommodations::availability, str -> str
                        .mustEx(Have::sizeEx, 2)
                        .checkInt(Map::size, size -> size.mustBeLessThan(RoomTier.list().size(), "Accommodations does not need to contain all tiers."))
                )
                .check(Accommodations::toString, str -> str
                        .mustEx(P::startWithEx, "Accommodations[availability={")
                        .mustEx(P::containEx, "ECONOMY=8")
                        .mustEx(P::notContainEx, BUSINESS.toString())
                        .mustEx(P::containEx, "PREMIUM=3")
                        .mustEx(P::endWithEx, "}]")
                );
    }

}