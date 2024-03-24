package com.Taco.CozyCompanions.event;

import com.Taco.CozyCompanions.CozyCompanions;
import com.Taco.CozyCompanions.entity.custom.AmaroEntity;
import com.Taco.CozyCompanions.networking.ModPackets;
import com.Taco.CozyCompanions.networking.packet.AmaroDescendC2SPacket;
import com.Taco.CozyCompanions.util.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.swing.text.JTextComponent;

public class ClientEvents {
    @Mod.EventBusSubscriber(modid = CozyCompanions.MODID, value = Dist.CLIENT)
    public static class ClientForgeEvents {

        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            // Amaro Descend Keybind
            AmaroEntity amaro = null;
            if (Minecraft.getInstance().player != null) {
                if (Minecraft.getInstance().player.getVehicle() instanceof AmaroEntity) {
                    amaro = (AmaroEntity)Minecraft.getInstance().player.getVehicle();
                }
            }

            if (amaro != null) {
                if(KeyBindings.DESCENDING_KEY.isDown() && amaro.isFlying()) {
                    ModPackets.sendToServer(new AmaroDescendC2SPacket());
                    amaro.setDescend(true);
                } else {
                    amaro.setDescend(false);
                }
            }
        }
    }

    @Mod.EventBusSubscriber(modid = CozyCompanions.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {
        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(KeyBindings.DESCENDING_KEY);
        }
    }
}
