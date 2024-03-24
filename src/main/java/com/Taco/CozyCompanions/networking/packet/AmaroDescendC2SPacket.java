package com.Taco.CozyCompanions.networking.packet;

import com.Taco.CozyCompanions.entity.custom.AmaroEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.client.event.RenderHighlightEvent;
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
                if (player.getVehicle() instanceof AmaroEntity amaro) {
                    if (amaro.isFlying()) {
                        amaro.setDescend(true);
                    }
                }
            } else {
                return;
            }

        });
        return true;
    }
}
