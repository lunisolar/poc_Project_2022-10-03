package eu.lnslr.example2023.booking.resolver;

import eu.lunisolar.lava.lang.seq.Seq;
import eu.lunisolar.lava.lang.tasks.Result;
import lombok.NonNull;

import java.util.List;

interface UpgradePolicy {

    @NonNull Result<List<MutableTierAssignments>> reassign(@NonNull List<MutableTierAssignments> assignments);

    static @NonNull List<MutableTierAssignments> doCopy(@NonNull List<MutableTierAssignments> assignments) {
        return Seq.seq(assignments).map(MutableTierAssignments::copy).toList();
    }

}

