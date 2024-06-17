package com.Taco.BaffleBeasts.networking.packet;

import com.Taco.BaffleBeasts.entity.custom.AmaroEntity;
import com.Taco.BaffleBeasts.flight.AmaroFlightProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AmaroGUISyncS2CPacket {
    public int flightPower;
    public int id;

    public AmaroGUISyncS2CPacket() {

    }

    public AmaroGUISyncS2CPacket(int flight, int i) {
        this.flightPower = flight;
        this.id = i;
    }


    public AmaroGUISyncS2CPacket(FriendlyByteBuf buf) {
        this.flightPower = buf.readInt();
        this.id = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(flightPower);
        buf.writeInt(id);
    }

    public void encode() {

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            ServerLevel level = context.getSender().getLevel();
            AmaroEntity amaro = (AmaroEntity)level.getEntity(id);

            if (player != null) {
                if (player.getVehicle() instanceof AmaroEntity) {
                    amaro.getCapability(AmaroFlightProvider.AMARO_FLIGHT_POWER).ifPresent((amaroFlight -> {
                        if (flightPower < 0) {
                            amaroFlight.subFlightPower(-flightPower);
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
