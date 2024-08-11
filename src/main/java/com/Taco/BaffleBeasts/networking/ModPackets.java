package com.taco.bafflebeasts.networking;

import com.taco.bafflebeasts.BaffleBeasts;
import com.taco.bafflebeasts.networking.packet.FlightEntityDashC2SPacket;
import com.taco.bafflebeasts.networking.packet.FlightEntityDescendC2SPacket;
import com.taco.bafflebeasts.networking.packet.FlightEntityPowerC2SPacket;
import com.taco.bafflebeasts.networking.packet.MayFlyC2SPacket;
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

//        net.messageBuilder(AmaroGUISyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
//                .decoder(AmaroGUISyncS2CPacket::new)
//                .encoder(AmaroGUISyncS2CPacket::toBytes)
//                .consumerMainThread((AmaroGUISyncS2CPacket::handle))
//                .add();

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