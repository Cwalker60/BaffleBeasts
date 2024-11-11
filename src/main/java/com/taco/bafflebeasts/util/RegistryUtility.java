package com.taco.bafflebeasts.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;


public class RegistryUtility {


    @Nullable
    public static RegistryAccess getRegistryAccess() {
        RegistryAccess access = null;
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        if (server != null) {
            Level level = server.getLevel(Level.OVERWORLD);
            if (level != null) {
                access = level.registryAccess();
            } else {
                access = server.registryAccess();
            }
        }

        if (access == null && FMLEnvironment.dist == Dist.CLIENT) {
            if (Minecraft.getInstance().level != null) {
                access = Minecraft.getInstance().level.registryAccess();
            } else {
                ClientPacketListener connection = Minecraft.getInstance().getConnection();
                if (connection != null) {
                    access = connection.registryAccess();
                }
            }
        }

        return access;
    }
}
