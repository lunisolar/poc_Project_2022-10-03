package eu.lnslr.example2023.booking.model;

import java.util.List;

import static eu.lunisolar.lava.lang.utils.Collections4U.immutableList;

public enum RoomTier {

    PREMIUM,
    BUSINESS,
    ECONOMY,

    //
    ;

    //<editor-fold desc="list">

    private static final List<RoomTier> LIST = immutableList().of(RoomTier.values());
    public static List<RoomTier> list() {return LIST;}

    //</editor-fold>

}
