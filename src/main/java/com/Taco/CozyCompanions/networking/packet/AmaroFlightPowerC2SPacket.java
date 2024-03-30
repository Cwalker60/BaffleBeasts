package com.Taco.CozyCompanions.networking.packet;

import com.Taco.CozyCompanions.entity.custom.AmaroEntity;
import com.Taco.CozyCompanions.flight.AmaroFlightProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AmaroFlightPowerC2SPacket {
    public int flightPower;

    public AmaroFlightPowerC2SPacket() {

    }

    public AmaroFlightPowerC2SPacket(int power) {
        this.flightPower = power;
    }


    public AmaroFlightPowerC2SPacket(FriendlyByteBuf buf) {
        this.flightPower = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(flightPower);
    }

    public void encode() {

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                if (player.getVehicle() instanceof AmaroEntity amaro) {

                    amaro.getCapability(AmaroFlightProvider.AMARO_FLIGHT_POWER).ifPresent((amaroFlight -> {
                        if (flightPower < 0) {
                            amaroFlight.subFlightPower(flightPower);
                        } else if (flightPower > 0) {
                            amaroFlight.addFlightPower(flightPower);
                        }
                        player.sendSystemMessage(Component.literal("Current Flight Power " + amaroFlight.getFlightPower()));
                    }));
                }
            } else {
                return;
            }

        });
        return true;
    }
}
