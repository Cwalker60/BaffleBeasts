package com.Taco.CozyCompanions.networking.packet;

import com.Taco.CozyCompanions.entity.custom.AmaroEntity;
import com.Taco.CozyCompanions.entity.custom.RideableFlightEntity;
import com.Taco.CozyCompanions.flight.AmaroFlightProvider;
import com.Taco.CozyCompanions.networking.ModPackets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Abilities;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AmaroFlightPowerC2SPacket {
    public int flightPower;
    public int id;
    public AmaroFlightPowerC2SPacket() {

    }

    public AmaroFlightPowerC2SPacket(int power, int i) {
        this.flightPower = power;
        this.id = i;
    }


    public AmaroFlightPowerC2SPacket(FriendlyByteBuf buf) {
        this.flightPower = buf.readInt();
        this.id = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(flightPower);
        buf.writeInt(id);
    }


    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && player.connection != null) {
                if (player.getVehicle() != null) {
                    if (player.getVehicle() instanceof RideableFlightEntity) {

                    }
                }
            } else {
                return;
            }

        });
        return true;
    }
}
