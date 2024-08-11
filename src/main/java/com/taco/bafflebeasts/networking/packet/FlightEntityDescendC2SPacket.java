package com.taco.bafflebeasts.networking.packet;

import com.taco.bafflebeasts.entity.custom.RideableFlightEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FlightEntityDescendC2SPacket {
    public int entityId;

    public FlightEntityDescendC2SPacket() {

    }

    public FlightEntityDescendC2SPacket(int id) {
        this.entityId = id;
    }


    public FlightEntityDescendC2SPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
    }

    public void encode() {

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                if (player.getVehicle() instanceof RideableFlightEntity flightEntity) {
                    if (flightEntity.isFlying()) {
                        flightEntity.setDescend(true);
                    }
                }
            } else {
                return;
            }

        });
        return true;
    }
}
