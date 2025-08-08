package com.taco.bafflebeasts.networking.packet;

import com.taco.bafflebeasts.entity.custom.IdleAnimatedEntity;
import com.taco.bafflebeasts.entity.custom.RideableFlightEntity;
import com.taco.bafflebeasts.networking.ModPackets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class IdleEntityMovementSyncC2S {
    protected boolean movement;
    protected int mobId;
    protected boolean isElytraGliding;

    public IdleEntityMovementSyncC2S() {

    }

    public IdleEntityMovementSyncC2S(boolean move, int id, boolean gliding) {
        this.movement = move;
        this.mobId = id;
        this.isElytraGliding = gliding;
    }

    public IdleEntityMovementSyncC2S(FriendlyByteBuf buf) {
        movement = buf.readBoolean();
        mobId = buf.readInt();
        isElytraGliding = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(movement);
        buf.writeInt(mobId);
        buf.writeBoolean(isElytraGliding);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            ServerLevel serverLevel = context.getSender().serverLevel();
            Entity mob = serverLevel.getEntity(this.mobId);

            if (!serverLevel.isClientSide()) {
                // Set the serverside entity to match what the client sent.
                if (mob instanceof RideableFlightEntity) {
                    RideableFlightEntity flightEntity = (RideableFlightEntity) serverLevel.getEntity(this.mobId);
                    flightEntity.hasMoved = this.movement;
                    flightEntity.isMoving = this.movement;
                    flightEntity.setElytraFlying(false);

                    // Send the packet to nearby clients
                    PacketDistributor.TargetPoint  areaForSync = new PacketDistributor.TargetPoint(context.getSender(), flightEntity.getX(),
                            flightEntity.getY(), flightEntity.getZ(), 50,serverLevel.dimension());
                    ModPackets.sendToNearbyPlayersByEntity(new IdleEntityMovementSyncS2C(this.movement,this.mobId, this.isElytraGliding), areaForSync);

                } else if (mob instanceof IdleAnimatedEntity){
                    IdleAnimatedEntity idleEntity = (IdleAnimatedEntity) serverLevel.getEntity(this.mobId);

                    idleEntity.hasMoved = this.movement;
                    idleEntity.isMoving = this.movement;

                    // Send the packet to nearby clients
                    PacketDistributor.TargetPoint  areaForSync = new PacketDistributor.TargetPoint(context.getSender(), idleEntity.getX(),
                            idleEntity.getY(), idleEntity.getZ(), 50,serverLevel.dimension());
                    ModPackets.sendToNearbyPlayersByEntity(new IdleEntityMovementSyncS2C(this.movement,this.mobId, this.isElytraGliding), areaForSync);
                }
            }


        });
        return true;
    }
}
