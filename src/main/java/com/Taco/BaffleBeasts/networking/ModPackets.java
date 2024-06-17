package com.Taco.BaffleBeasts.networking;

import com.Taco.BaffleBeasts.BaffleBeasts;
import com.Taco.BaffleBeasts.networking.packet.*;
import net.minecraft.resources.ResourceLocation;
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
        net.messageBuilder(AmaroDescendC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(AmaroDescendC2SPacket::new)
                .encoder(AmaroDescendC2SPacket::toBytes)
                .consumerMainThread(AmaroDescendC2SPacket::handle)
                .add();

        net.messageBuilder(AmaroFlightPowerC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(AmaroFlightPowerC2SPacket::new)
                .encoder(AmaroFlightPowerC2SPacket::toBytes)
                .consumerMainThread((AmaroFlightPowerC2SPacket::handle))
                .add();

        net.messageBuilder(AmaroFlightDashC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(AmaroFlightDashC2SPacket::new)
                .encoder(AmaroFlightDashC2SPacket::toBytes)
                .consumerMainThread((AmaroFlightDashC2SPacket::handle))
                .add();

        net.messageBuilder(AmaroGUISyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(AmaroGUISyncS2CPacket::new)
                .encoder(AmaroGUISyncS2CPacket::toBytes)
                .consumerMainThread((AmaroGUISyncS2CPacket::handle))
                .add();

        // MayFlyC2SPacket
        net.messageBuilder(MayFlyC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(MayFlyC2SPacket::new)
                .encoder(MayFlyC2SPacket::toBytes)
                .consumerMainThread(MayFlyC2SPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}