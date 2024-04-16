package com.Taco.CozyCompanions.networking.packet;

import com.Taco.CozyCompanions.entity.custom.AmaroEntity;
import com.Taco.CozyCompanions.flight.AmaroFlightProvider;
import com.mojang.logging.LogUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;

import java.util.function.Supplier;

public class MayFlyC2SPacket {
    private boolean canFly;
    private static final Logger LOGGER = LogUtils.getLogger();

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
            ServerLevel level = context.getSender().getLevel();

            if (player != null) {
                if (player.getVehicle() != null) {
                    if (player.getVehicle() instanceof AmaroEntity a) {
                        a.setNoGravity(canFly);
                        LOGGER.debug("amaro gravity is : " + a.isNoGravity());
                    }
                }
            } else {
                return;
            }

        });
        return true;
    }
}

