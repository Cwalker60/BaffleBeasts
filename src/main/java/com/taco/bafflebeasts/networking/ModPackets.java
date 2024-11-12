package com.taco.bafflebeasts.networking;

import com.taco.bafflebeasts.BaffleBeasts;
import com.taco.bafflebeasts.entity.custom.RideableFlightEntity;
import com.taco.bafflebeasts.networking.packet.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModPackets {
    private static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(BaffleBeasts.MODID, "packets"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;
        net.messageBuilder(FlightEntityDescendC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(FlightEntityDescendC2SPacket::new)
                .encoder(FlightEntityDescendC2SPacket::toBytes)
                .consumerMainThread(FlightEntityDescendC2SPacket::handle)
                .add();

        net.messageBuilder(FlightEntityPowerC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(FlightEntityPowerC2SPacket::new)
                .encoder(FlightEntityPowerC2SPacket::toBytes)
                .consumerMainThread((FlightEntityPowerC2SPacket::handle))
                .add();

        net.messageBuilder(FlightEntityDashC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(FlightEntityDashC2SPacket::new)
                .encoder(FlightEntityDashC2SPacket::toBytes)
                .consumerMainThread((FlightEntityDashC2SPacket::handle))
                .add();

        net.messageBuilder(FlightEntityMovementSyncS2C.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(FlightEntityMovementSyncS2C::new)
                .encoder(FlightEntityMovementSyncS2C::toBytes)
                .consumerMainThread(FlightEntityMovementSyncS2C::handle)
                .add();

        net.messageBuilder(FlightEntityMovementSyncC2S.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(FlightEntityMovementSyncC2S::new)
                .encoder(FlightEntityMovementSyncC2S::toBytes)
                .consumerMainThread(FlightEntityMovementSyncC2S::handle)
                .add();


        // MayFlyC2SPacket
        net.messageBuilder(MayFlyC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(MayFlyC2SPacket::new)
                .encoder(MayFlyC2SPacket::toBytes)
                .consumerMainThread(MayFlyC2SPacket::handle)
                .add();

        //DozeDrakeMountAttackC2SPacket
        net.messageBuilder(DozeDrakeMountAttackC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(DozeDrakeMountAttackC2SPacket::new)
                .encoder(DozeDrakeMountAttackC2SPacket::toBytes)
                .consumerMainThread(DozeDrakeMountAttackC2SPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToNearbyPlayersByEntity(MSG message, PacketDistributor.TargetPoint targetPoint) {
        INSTANCE.send(PacketDistributor.NEAR.with(() -> targetPoint), message);
    }

    public static <MSG> void sendToAllPlayers(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }

}