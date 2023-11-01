package eu.lnslr.example2023.booking.resolver;

import eu.lnslr.example2023.booking.model.*;
import eu.lunisolar.lava.lang.seq.Seq;
import eu.lunisolar.lava.lang.tasks.Result;
import eu.lunisolar.magma.basics.exceptions.Handling;
import eu.lunisolar.magma.func.supp.Be;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;
import java.util.*;

import static eu.lunisolar.lava.lang.seq.Seq.seq;
import static eu.lunisolar.lava.lang.utils.Collections4U.list;
import static eu.lunisolar.lava.lang.utils.Types4U.BIG_DECIMAL;
import static eu.lunisolar.magma.func.supp.check.Checks.*;
import static java.math.BigDecimal.valueOf;

/**
 * TODO name, obviously
 */
@Log4j2
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class StandardResolver implements BookingResolver {

    private final @NonNull List<RoomTier>   activeTiers;
    private final @NonNull List<BigDecimal> thresholds;
    private final @NonNull UpgradePolicy    upgradePolicy;

    public static @NonNull StandardResolver resolver(@NonNull Map<RoomTier, BigDecimal> thresholdsMap, @NonNull UpgradePolicy processor) {
        var activeTiers = list().<RoomTier>empty();
        var thresholds  = list().<BigDecimal>empty();

        var lastThresholdValue = max(thresholdsMap.values()).add(BigDecimal.ONE);
        for (RoomTier tier : RoomTier.list()) {

            if (thresholdsMap.containsKey(tier)) {
                activeTiers.add(tier);
                var value = arg(thresholdsMap.get(tier))
                        .mustBeNotNull("Argument map must contain non null value for a threshold.")
                        .must(BIG_DECIMAL.lowerOrEqual(), lastThresholdValue, "Threshold values must descend with each tier.")
                        .get();
                thresholds.add(value);
                lastThresholdValue = value;
            }
        }

        return new StandardResolver(activeTiers, thresholds, processor);
    }


    @Override public @NonNull Result<BookingSummary> execute(@NonNull Accommodations accommodations, @NonNull List<Guest> guests) {

        // TODO lava: API is(?) missing mapVoid() - to e.g. map it to Result.failure(...)
        // TODO lava: replaceResult(Result.onVoid()) is doing the half the job - forcing to use the full { throw..} block.
        // TODO lava: onFailure() is(?) dangerously close to mapFailure (one will throw-and-propagate exception, the second keeps it wrapped in Result<> )

        return Result
                .resultOf(guests, this::protectiveSortedCopy)
                .mapResultWith(accommodations, this::doInitialAssignments)
                .flatMapResult(this::doUpgrades)
                .mapResult(this::toSummary)
                .replaceResult(Result.onVoid(), () -> {
                    throw Handling.create(IllegalStateException::new, "Failed to find solution.");
                })
                .wrapFailureIfNot(BookingException.class, BookingException::new, "Calculating reservations has failed.");
    }


    private static final Comparator<Guest> GUEST_COMPARATOR = Comparator.comparing(Guest::preferredPrice).reversed();

    private @NonNull List<Guest> protectiveSortedCopy(@NonNull List<Guest> guests) {
        return seq(guests).sorted(GUEST_COMPARATOR).toList();
    }

    private @NonNull List<MutableTierAssignments> doInitialAssignments(@NonNull Accommodations accommodations, @NonNull List<Guest> guests) {
        final var priceRoof = calculatePriceRoof(guests);

        var guestIterator = guests.listIterator();
        var tierIterator  = activeTiers.listIterator();

        Guest guest = null;

        var solution = list().<MutableTierAssignments>empty();

        while (tierIterator.hasNext()) {

            var tier             = tierIterator.next();
            var tierIndex        = currentIndex(tierIterator);
            var thresholdCeiling = tierIndex > 0 ? thresholds.get(tierIndex - 1) : priceRoof;
            var thresholdFloor   = thresholds.get(tierIndex);
            var tierCapacity     = accommodations.availability().get(tier);

            arg(tierCapacity, "accommodations").must1(Be::notNull, "No entry for tier used in current setup: %s", tier);

            var tierQueue = new MutableTierAssignments(tier, tierCapacity);

            while (guestIterator.hasNext()) {
                guest = guestIterator.next();

                if (!BIG_DECIMAL.greaterOrEqual().test(guest.preferredPrice(), thresholdFloor)) {
                    guestIterator.previous(); // we need to back 1 step;
                    break; // tier is not satisfactory
                }

                if (BIG_DECIMAL.lower().test(guest.preferredPrice(), thresholdCeiling)) {
                    tierQueue.addCandidate(guest);
                }
            }

            solution.add(tierQueue);

        }
        return solution;
    }

    private @NonNull Result<List<MutableTierAssignments>> doUpgrades(@NonNull List<MutableTierAssignments> assignments) {
        return upgradePolicy.reassign(assignments);
    }

    private @NonNull BookingSummary toSummary(@NonNull List<MutableTierAssignments> assignments) {
        return BookingSummary.bookingSummary(seq(assignments).map(MutableTierAssignments::toSummary).toImmutableList());
    }

    private static @NonNull BigDecimal calculatePriceRoof(@NonNull List<Guest> guests) {
        return guests.isEmpty() ? BigDecimal.ZERO : guests.get(0).preferredPrice().add(BigDecimal.ONE);
    }

    private static @NonNull BigDecimal max(Collection<BigDecimal> values) {
        return Seq.seq(values).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static int currentIndex(ListIterator<?> iterator) {
        return iterator.nextIndex() - 1;
    }


}
