package com.taco.bafflebeasts.networking.packet;

import com.taco.bafflebeasts.entity.custom.RideableFlightEntity;
import com.taco.bafflebeasts.util.ElytraGlideCalculation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FlightEntityDashC2SPacket {
    public int amaroId;

    public FlightEntityDashC2SPacket() {

    }

    public FlightEntityDashC2SPacket(int id) {
        this.amaroId = id;
    }


    public FlightEntityDashC2SPacket(FriendlyByteBuf buf) {
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
            ServerLevel level = context.getSender().serverLevel();

            if (player != null) {
                if (player.getVehicle() instanceof RideableFlightEntity flightEntity) {

                    ItemStack itemstack = ElytraGlideCalculation.createAmaroFirework();
                    if (!level.isClientSide()) {
                        FireworkRocketEntity amarofirework = new FireworkRocketEntity(level, itemstack, flightEntity);
                        amarofirework.setInvisible(true);
                        amarofirework.setSilent(true);
                        level.addFreshEntity(amarofirework);
                    }

                }
            } else {
                return;
            }

        });
        return true;
    }

}
