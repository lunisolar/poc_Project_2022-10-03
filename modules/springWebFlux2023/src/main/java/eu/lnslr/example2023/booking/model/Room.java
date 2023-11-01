package eu.lnslr.example2023.booking.model;


import lombok.*;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class Room {  // TODO do I need this? / remove if not used

    private final @NonNull RoomTier type;

    public static @NonNull Room.B room()                     {return Room.builder();}
    public static @NonNull Room room(@NonNull RoomTier type) {return new Room(type);}
}
