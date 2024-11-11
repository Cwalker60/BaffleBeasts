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

    public FlightEntityMovementSyncC2S() {

    }

    public FlightEntityMovementSyncC2S(boolean move, int id) {
        this.movement = move;
        this.mobId = id;
    }

    public FlightEntityMovementSyncC2S(FriendlyByteBuf buf) {
        movement = buf.readBoolean();
        mobId = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(movement);
        buf.writeInt(mobId);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            ServerLevel serverLevel = context.getSender().serverLevel();
            RideableFlightEntity rideableFlightEntity = (RideableFlightEntity) serverLevel.getEntity(this.mobId);

            if (!serverLevel.isClientSide()) {
                rideableFlightEntity.hasMoved = this.movement;
                rideableFlightEntity.isMoving = this.movement;
                PacketDistributor.TargetPoint  areaForSync = new PacketDistributor.TargetPoint(context.getSender(), rideableFlightEntity.getX(),
                        rideableFlightEntity.getY(), rideableFlightEntity.getZ(), 50,serverLevel.dimension());
                //BaffleBeasts.MAIN_LOGGER.debug("Sending targetpoint of sync packed too " + areaForSync.toString());
                BaffleBeasts.MAIN_LOGGER.debug("dozedrake id passed from packet is  " + this.mobId);
                BaffleBeasts.MAIN_LOGGER.debug("dozedrake id found on server is " + rideableFlightEntity.getId());
                //ModPackets.sendToNearbyPlayersByEntity(new FlightEntityMovementSyncS2C(this.movement,this.mobId), areaForSync);
                ModPackets.sendToAllPlayers(new FlightEntityMovementSyncS2C(this.movement,this.mobId));
            }


        });
        return true;
    }
}
