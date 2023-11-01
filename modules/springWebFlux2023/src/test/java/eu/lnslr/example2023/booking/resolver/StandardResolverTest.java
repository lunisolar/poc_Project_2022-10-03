package eu.lnslr.example2023.booking.resolver;

import eu.lnslr.example2023.booking.model.*;
import eu.lnslr.example2023.booking.resolver.BookingException;
import eu.lnslr.example2023.booking.resolver.StandardResolver;
import eu.lnslr.example2023.booking.resolver.UpgradePolicies;
import eu.lnslr.example2023.booking.resolver.UpgradePolicy;
import eu.lunisolar.lava.lang.seq.Seq;
import eu.lunisolar.lava.lang.tasks.Result;
import eu.lunisolar.magma.asserts.TestFlow.Stage;
import eu.lunisolar.magma.func.IA;
import eu.lunisolar.magma.func.supp.Be;
import eu.lunisolar.magma.func.supp.Have;
import eu.lunisolar.magma.func.supp.traits.CheckBoolTrait;
import lombok.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static eu.lnslr.example2023.booking.model.Accommodations.accommodations;
import static eu.lnslr.example2023.booking.model.RoomTier.*;
import static eu.lnslr.example2023.booking.model.TierSummary.typeSummary;
import static eu.lunisolar.lava.lang.utils.Collections4U.immMap;
import static eu.lunisolar.lava.lang.utils.Collections4U.list;
import static eu.lunisolar.magma.asserts.TestFlow.test;
import static eu.lunisolar.magma.func.consumer.LBiConsumer.forEach;
import static eu.lunisolar.magma.func.supp.check.Checks.attest;
import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class StandardResolverTest {

    //<editor-fold desc="setup">

    private static final @NonNull UpgradePolicy NO_UPGRADE      = UpgradePolicies.noUpgradePolicy();
    private static final @NonNull UpgradePolicy UNFAIR          = UpgradePolicies.unfairUpgradePolicy();
    private static final @NonNull UpgradePolicy FAIR            = UpgradePolicies.fairUpgradePolicy();
    public static final @NonNull  UpgradePolicy THROWING_POLICY = assignments -> {
        throw new NullPointerException("THROWING_POLICY");
    };

    private static final @NonNull List<Guest> GUESS_LIST1 = Seq.ofDbl(23, 45, 155, 374, 22, 99.99, 100, 101, 115, 209)
            .mapToObj(i -> Guest.guest().preferredPrice(i).build())
            .toList();

    private static final @NonNull List<Guest> NO_GUESTS = list().empty();


    private static Accommodations acc(int premium, int economy) {
        return accommodations()
                .add(PREMIUM, premium)
                .add(ECONOMY, economy)
                .build();
    }

    private static Accommodations acc(int premium, int business, int economy) {
        return accommodations()
                .add(PREMIUM, premium)
                .add(BUSINESS, business)
                .add(ECONOMY, economy)
                .build();
    }

    private static @NonNull Map<RoomTier, BigDecimal> TWO_TIER_SETUP_100 = immMap().ofEntries(
            Map.entry(PREMIUM, valueOf(100)),
            Map.entry(ECONOMY, valueOf(0))
    );

    private static @NonNull Map<RoomTier, BigDecimal> FREE_TIER_SETUP = immMap().ofEntries(
            Map.entry(PREMIUM, valueOf(200)),
            Map.entry(BUSINESS, valueOf(100)),
            Map.entry(ECONOMY, valueOf(0))
    );

    //</editor-fold>

    @Test void exceptionHandling() {

        test().given(new Stage() {
            Accommodations emptyAccommodations = acc(1, 1);
            List<Guest> guessList = list().empty();
            Result<?> result = null;
        }).when(stage -> {
            stage.result = StandardResolver.resolver(TWO_TIER_SETUP_100, THROWING_POLICY).execute(stage.emptyAccommodations, stage.guessList);
        }).then(stage -> {
            attest(stage.result)
                    .checkBool(Result::isFailure, CheckBoolTrait::mustBeTrue)
                    .check(Result::failure, e -> e
                            .mustBeExactlyInstanceOf(BookingException.class)
                            .mustEx(Have::msgEqualEx, "Calculating reservations has failed.")
                            .mustEx(Have::causeEx)
                            .check(Throwable::getCause, cause -> cause
                                    .mustBeExactlyInstanceOf(NullPointerException.class)
                                    .mustEx(Have::msgEqualEx, "THROWING_POLICY")
                            )
                    );
        });
    }

    public static Stream<Arguments> testData() {
        return Seq.of(
                arguments(TWO_TIER_SETUP_100, UNFAIR, acc(3, 3), GUESS_LIST1, list().of(typeSummary(PREMIUM, 3, valueOf(738)), typeSummary(ECONOMY, 3, valueOf(167.99)))),
                arguments(TWO_TIER_SETUP_100, UNFAIR, acc(7, 5), GUESS_LIST1, list().of(typeSummary(PREMIUM, 6, valueOf(1054)), typeSummary(ECONOMY, 4, valueOf(189.99)))),
                arguments(TWO_TIER_SETUP_100, UNFAIR, acc(2, 7), GUESS_LIST1, list().of(typeSummary(PREMIUM, 2, valueOf(583)), typeSummary(ECONOMY, 4, valueOf(189.99)))),
                arguments(TWO_TIER_SETUP_100, UNFAIR, acc(7, 1), GUESS_LIST1, list().of(typeSummary(PREMIUM, 7, valueOf(1153.99)), typeSummary(ECONOMY, 1, valueOf(45)))),

                // 374, 209,     155, 115, 101, 100,    99.99, 45, 23, 22
                // 374, 209, 155, 115,        101, 100,    99.99, 45,        23, 22
                arguments(FREE_TIER_SETUP, UNFAIR, acc(4, 2, 2), GUESS_LIST1, list().of(typeSummary(PREMIUM, 4, valueOf(853)), typeSummary(BUSINESS, 2, valueOf(201)), typeSummary(ECONOMY, 2, valueOf(144.99)))),

                // available: 6 2 2
                // guests: 374, 209,     155, 115, 101, 100,    99.99, 45, 23, 22
                // Unfair: 374, 209, 155, 115,  99.99, 45.0,       101, 100,        23, 22
                // Fair:   374, 209, 155, 115,  101, 100,       99.99, 45,        23, 22
                arguments(FREE_TIER_SETUP, UNFAIR, acc(6, 2, 2), GUESS_LIST1, list().of(typeSummary(PREMIUM, 6, valueOf(997.99)), typeSummary(BUSINESS, 2, valueOf(201)), typeSummary(ECONOMY, 2, valueOf(45)))),
                arguments(FREE_TIER_SETUP, FAIR, acc(6, 2, 2), GUESS_LIST1, list().of(typeSummary(PREMIUM, 6, valueOf(1054)), typeSummary(BUSINESS, 2, valueOf(144.99)), typeSummary(ECONOMY, 2, valueOf(45)))),

                // available: 10 1 1
                // guests: 374, 209,     155, 115, 101, 100,    99.99, 45, 23, 22
                // Fair:   374, 209, 155, 115,  101, 100,  99.99, 45,        23,             22
                arguments(FREE_TIER_SETUP, FAIR, acc(10, 1, 1), GUESS_LIST1, list().of(typeSummary(PREMIUM, 8, valueOf(1198.99)), typeSummary(BUSINESS, 1, valueOf(23)), typeSummary(ECONOMY, 1, valueOf(22)))),

                arguments(FREE_TIER_SETUP, FAIR, acc(10, 0, 1), GUESS_LIST1, list().of(typeSummary(PREMIUM, 9, valueOf(1221.99)), typeSummary(BUSINESS, 0, valueOf(0)), typeSummary(ECONOMY, 1, valueOf(22)))),
                arguments(FREE_TIER_SETUP, FAIR, acc(10, 0, 1), GUESS_LIST1, list().of(typeSummary(PREMIUM, 9, valueOf(1221.99)), typeSummary(BUSINESS, 0, valueOf(0)), typeSummary(ECONOMY, 1, valueOf(22)))),

                arguments(FREE_TIER_SETUP, FAIR, acc(0, 0, 0), GUESS_LIST1, list().of(typeSummary(PREMIUM, 0, valueOf(0)), typeSummary(BUSINESS, 0, valueOf(0)), typeSummary(ECONOMY, 0, valueOf(0)))),
                arguments(FREE_TIER_SETUP, FAIR, acc(2, 2, 2), NO_GUESTS, list().of(typeSummary(PREMIUM, 0, valueOf(0)), typeSummary(BUSINESS, 0, valueOf(0)), typeSummary(ECONOMY, 0, valueOf(0))))
        );
    }

    @ParameterizedTest
    @MethodSource("testData")
    void executeForTestData(Map<RoomTier, BigDecimal> setup, UpgradePolicy policy, Accommodations accommodations, List<Guest> guests, List<TierSummary> expected) {

        // TODO magma: by default it will to OUT - design decision from past - but is it right?
        test().given(new Stage() {
            Result<?> result = null;
        }).when(stage -> {
            stage.result = StandardResolver.resolver(setup, policy).execute(accommodations, guests);
        }).then(stage -> {
            stage.result.propagateIfFailure();
            attest(stage.result)
                    .checkBool(Result::isSuccessValue, CheckBoolTrait::mustBeTrue)
                    .check(Result::get, r -> r
                            .mustBeExactlyInstanceOf(BookingSummary.class)
                            .check(BookingSummary::summaryByType, byType -> {

                                        byType.mustEx(Have::sizeEx, setup.size());
                                        byType.mustEx(Have::sizeEx, expected.size());

                                        // TODO magma: unchecked boundaries - design decision from past - but is it right?
                                        forEach(IA.list(), byType.get(), IA.list(), expected, (actualItem, expectedItem) -> {
                                            attest(actualItem.roomTier()).mustEx2(Be::EqualEx, expectedItem.roomTier(), "Actual=%s, Expected=%s", actualItem, expectedItem);
                                            attest(actualItem.count()).mustEx2(Be::EqualEx, expectedItem.count(), "Actual=%s, Expected=%s", actualItem, expectedItem);
                                            attest(actualItem.price().compareTo(expectedItem.price())).mustEx2(Be::EqualEx, 0, "Actual=%s, Expected=%s", actualItem, expectedItem);
                                        });

                                    }
                            )
                    );
        });
    }

    @Test
    void missMatchWithSetupIsDetected() {

        test().given(new Stage() {
            final StandardResolver resolver = StandardResolver.resolver(FREE_TIER_SETUP, UNFAIR);
            Result<?> result = null;
        }).when(stage -> {
            stage.result = stage.resolver.execute(acc(7, 1), GUESS_LIST1);
        }).then(stage -> {
            attest(stage.result)
                    .checkBool(Result::isFailure, CheckBoolTrait::mustBeTrue)
                    .check(Result::failure, e -> e
                            .mustBeExactlyInstanceOf(BookingException.class)
                            .mustEx(Have::msgEqualEx, "Calculating reservations has failed.")
                            .mustEx(Have::causeEx)
                            .check(Throwable::getCause, cause -> cause
                                    .mustBeExactlyInstanceOf(IllegalArgumentException.class)
                                    .mustEx(Have::msgEqualEx, "Argument [accommodations]: No entry for tier used in current setup: BUSINESS")
                            )
                    );
        });
    }


}