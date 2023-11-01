package eu.lnslr.example2023.booking.resolver;

import eu.lnslr.example2023.booking.model.Guest;
import eu.lunisolar.lava.lang.tasks.Result;
import eu.lunisolar.magma.func.supp.opt.Opt;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.List;

import static eu.lunisolar.lava.lang.tasks.Result.resultOf;

/**
 * Simple policy where number of changes is reduced by allowing guests to jump directly more than one tier (it becomes unfair when there are more than 2 tiers).
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
class UnfairUpgradePolicy implements UpgradePolicy {

    @Override public @NonNull Result<List<MutableTierAssignments>> reassign(@NonNull List<MutableTierAssignments> assignments) {
        return resultOf(() -> UpgradePolicy.doCopy(assignments))
                .mapResult(this::doUpgradesAndReturn);
    }

    private @NonNull List<MutableTierAssignments> doUpgradesAndReturn(@NonNull List<MutableTierAssignments> assignments) {

        for (int i = assignments.size() - 1; i >= 0; i--) {
            var upperTier = assignments.get(i);

            for (int k = i + 1; k < assignments.size() && !upperTier.isFull(); k++) {
                var lowerTier = assignments.get(k);

                Opt<Guest> pulled = null;
                while (!upperTier.isFull() && lowerTier.isFull() && (pulled = lowerTier.pullForUpgrade()).isPresent()) {
                    upperTier.addCandidate(pulled.get());
                }
            }
        }

        return assignments;
    }
}
