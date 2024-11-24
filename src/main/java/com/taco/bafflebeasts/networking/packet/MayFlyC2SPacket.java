package com.taco.bafflebeasts.networking.packet;

import com.taco.bafflebeasts.entity.custom.RideableFlightEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MayFlyC2SPacket {
    private boolean canFly;

    public MayFlyC2SPacket() {

    }

    public MayFlyC2SPacket(boolean b) {
        this.canFly = b;
    }


    public MayFlyC2SPacket(FriendlyByteBuf buf) {
        this.canFly = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(canFly);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            ServerLevel level = context.getSender().serverLevel();

            if (player != null) {
                if (player.getVehicle() != null) {
                    if (player.getVehicle() instanceof RideableFlightEntity a) {
                        a.setNoGravity(canFly);
                    }
                }
            } else {
                return;
            }

        });
        return true;
    }

}
