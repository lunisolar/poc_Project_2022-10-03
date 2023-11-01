package eu.lnslr.example2023.booking.resolver;

import eu.lunisolar.lava.lang.tasks.Result;
import lombok.NonNull;

class UpgradePolicies {

    private static final UpgradePolicy UNFAIR     = new UnfairUpgradePolicy();
    private static final UpgradePolicy FAIR       = new FairUpgradePolicy();
    private static final UpgradePolicy NO_UPGRADE = assignments -> Result.resultOf(assignments, UpgradePolicy::doCopy);

    static @NonNull UpgradePolicy noUpgradePolicy()     {return NO_UPGRADE;}
    static @NonNull UpgradePolicy unfairUpgradePolicy() {return UNFAIR;}
    static @NonNull UpgradePolicy fairUpgradePolicy()   {return FAIR;}

}
