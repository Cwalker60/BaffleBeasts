package com.Taco.BaffleBeasts.networking.packet;

import com.Taco.BaffleBeasts.entity.custom.RideableFlightEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AmaroDescendC2SPacket {
    public int amaroId;

    public AmaroDescendC2SPacket() {

    }

    public AmaroDescendC2SPacket(int id) {
        this.amaroId = id;
    }


    public AmaroDescendC2SPacket(FriendlyByteBuf buf) {
        this.amaroId = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(amaroId);
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
