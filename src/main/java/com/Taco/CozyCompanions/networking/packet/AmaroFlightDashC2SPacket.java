package com.Taco.CozyCompanions.networking.packet;

import com.Taco.CozyCompanions.entity.custom.AmaroEntity;
import com.Taco.CozyCompanions.entity.custom.RideableFlightEntity;
import com.Taco.CozyCompanions.util.ElytraGlideCalculation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AmaroFlightDashC2SPacket {
    public int amaroId;

    public AmaroFlightDashC2SPacket() {

    }

    public AmaroFlightDashC2SPacket(int id) {
        this.amaroId = id;
    }


    public AmaroFlightDashC2SPacket(FriendlyByteBuf buf) {
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
            ServerLevel level = context.getSender().getLevel();

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
