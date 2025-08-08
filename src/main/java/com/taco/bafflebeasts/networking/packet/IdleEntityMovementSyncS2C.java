package com.taco.bafflebeasts.networking.packet;

import com.taco.bafflebeasts.entity.custom.IdleAnimatedEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import com.taco.bafflebeasts.entity.custom.RideableFlightEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class IdleEntityMovementSyncS2C {
    protected boolean movement;
    protected int mobId;
    protected boolean isElytraGliding;

    public IdleEntityMovementSyncS2C() {

    }

    public IdleEntityMovementSyncS2C(boolean move, int id, boolean gliding) {
        this.movement = move;
        this.mobId = id;
        this.isElytraGliding = gliding;
    }


    public IdleEntityMovementSyncS2C(FriendlyByteBuf buf) {
        this.movement = buf.readBoolean();
        this.mobId = buf.readInt();
        this.isElytraGliding = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(movement);
        buf.writeInt(mobId);
        buf.writeBoolean(isElytraGliding);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> IdleEntityMovementSyncS2C.syncFlightEntity(supplier, movement, mobId, isElytraGliding));
            }




        });
        return true;
    }

    public static boolean syncFlightEntity(Supplier<NetworkEvent.Context> supplier, boolean move, int mobId, boolean gliding) {

            if (supplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                ServerPlayer player = supplier.get().getSender();
                Entity mob = Minecraft.getInstance().level.getEntity(mobId);

                if (mob instanceof RideableFlightEntity) {
                    RideableFlightEntity flightEntity = (RideableFlightEntity) Minecraft.getInstance().level.getEntity(mobId);
                    flightEntity.hasMoved = move;
                    flightEntity.isMoving = move;
                    flightEntity.setElytraFlying(gliding);

                } else if (mob instanceof IdleAnimatedEntity){
                    IdleAnimatedEntity idleEntity = (IdleAnimatedEntity) Minecraft.getInstance().level.getEntity(mobId);

                    idleEntity.hasMoved = move;
                    idleEntity.isMoving = move;

                }
            }
        return true;
    }
}
