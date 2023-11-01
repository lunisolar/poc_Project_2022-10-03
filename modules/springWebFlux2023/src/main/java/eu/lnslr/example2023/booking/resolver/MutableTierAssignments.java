package eu.lnslr.example2023.booking.resolver;

import eu.lnslr.example2023.booking.model.Guest;
import eu.lnslr.example2023.booking.model.RoomTier;
import eu.lnslr.example2023.booking.model.TierSummary;
import eu.lunisolar.lava.lang.seq.Seq;
import eu.lunisolar.magma.basics.exceptions.X;
import eu.lunisolar.magma.func.supp.opt.Opt;
import lombok.*;

import java.math.BigDecimal;
import java.util.Deque;
import java.util.LinkedList;

import static eu.lunisolar.magma.func.supp.check.Checks.state;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
class MutableTierAssignments {

    final RoomTier     tier;
    final int          tierCapacity;
    final Deque<Guest> guests;
    final Deque<Guest> reserve;

    MutableTierAssignments(RoomTier tier, int tierCapacity) {
        this.tier         = tier;
        this.tierCapacity = tierCapacity;
        this.guests       = deque();
        this.reserve      = deque();
    }

    public void addCandidate(@NonNull Guest guest) {
        if (isFull()) {
            addReserve(guest);
        } else {
            addGuest(guest);
        }
    }

    public void addGuest(@NonNull Guest guest) {
        state(isFull()).mustBeFalse();
        guests.add(guest);
    }

    public void addReserve(@NonNull Guest guest) {
        state(isFull()).mustBeTrue();
        reserve.add(guest);
    }

    public boolean isFull()               {return guests.size() >= tierCapacity;}
    public boolean isCompletelyEmpty()    {return guests.isEmpty() && reserve.isEmpty();}
    public int currentSize()              {return guests.size();}
    public @NonNull Seq<Guest> seq()      {return Seq.seq(guests);}
    public @NonNull BigDecimal priceSum() {return seq().map(Guest::preferredPrice).reduce(BigDecimal.ZERO, BigDecimal::add);}

    public @NonNull Opt<Guest> pullForUpgrade() {
        if (reserve.isEmpty()) return Opt.empty();

        guests.add(reserve.pollFirst());
        return Opt.notNull(guests.pollFirst());
    }

    public @NonNull Guest swapPull(@NonNull Guest guest) {
        if ( isCompletelyEmpty() ) {
            // throughout 
            return guest;
        }
        reserve.add(guest);
        return pullForUpgrade().orElseThrow(X::state, "Given one was added, one always must be pulled (even if the same)");
    }

    public @NonNull TierSummary toSummary()       {return TierSummary.typeSummary(tier, currentSize(), priceSum());}
    public @NonNull MutableTierAssignments copy() {return new MutableTierAssignments(tier, tierCapacity, deque(guests), deque(reserve));}

    // 

    private static @NonNull Deque<Guest> deque()                            {return new LinkedList<>();}
    private static @NonNull Deque<Guest> deque(@NonNull Deque<Guest> deque) {return new LinkedList<>(deque);}

}
