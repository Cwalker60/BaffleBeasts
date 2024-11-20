package com.taco.bafflebeasts.networking.packet;

import com.taco.bafflebeasts.BaffleBeasts;
import com.taco.bafflebeasts.entity.custom.DozeDrakeEntity;
import com.taco.bafflebeasts.entity.custom.RideableFlightEntity;
import com.taco.bafflebeasts.networking.ModPackets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class FlightEntityMovementSyncC2S {
    protected boolean movement;
    protected int mobId;
    protected boolean isElytraGliding;

    public FlightEntityMovementSyncC2S() {

    }

    public FlightEntityMovementSyncC2S(boolean move, int id, boolean gliding) {
        this.movement = move;
        this.mobId = id;
        this.isElytraGliding = gliding;
    }

    public FlightEntityMovementSyncC2S(FriendlyByteBuf buf) {
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
            RideableFlightEntity rideableFlightEntity = (RideableFlightEntity) serverLevel.getEntity(this.mobId);

            if (!serverLevel.isClientSide()) {
                // Set the serverside entity to match what the client sent.
                rideableFlightEntity.hasMoved = this.movement;
                rideableFlightEntity.isMoving = this.movement;
                rideableFlightEntity.setElytraFlying(false);

                // Send the packet to nearby clients
                PacketDistributor.TargetPoint  areaForSync = new PacketDistributor.TargetPoint(context.getSender(), rideableFlightEntity.getX(),
                        rideableFlightEntity.getY(), rideableFlightEntity.getZ(), 50,serverLevel.dimension());
                ModPackets.sendToNearbyPlayersByEntity(new FlightEntityMovementSyncS2C(this.movement,this.mobId, this.isElytraGliding), areaForSync);
            }


        });
        return true;
    }
}
