package com.taco.bafflebeasts.networking.packet;

import com.taco.bafflebeasts.entity.custom.RideableFlightEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FlightEntityPowerC2SPacket {
    public int flightPower;
    public int id;
    public FlightEntityPowerC2SPacket() {

    }

    public FlightEntityPowerC2SPacket(int power, int i) {
        this.flightPower = power;
        this.id = i;
    }


    public FlightEntityPowerC2SPacket(FriendlyByteBuf buf) {
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
