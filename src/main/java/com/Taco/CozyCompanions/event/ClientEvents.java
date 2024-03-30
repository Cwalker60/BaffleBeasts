package com.Taco.CozyCompanions.event;

import com.Taco.CozyCompanions.CozyCompanions;
import com.Taco.CozyCompanions.entity.client.AmaroFlightHud;
import com.Taco.CozyCompanions.entity.custom.AmaroEntity;
import com.Taco.CozyCompanions.flight.AmaroFlight;
import com.Taco.CozyCompanions.flight.AmaroFlightProvider;
import com.Taco.CozyCompanions.networking.ModPackets;
import com.Taco.CozyCompanions.networking.packet.AmaroDescendC2SPacket;
import com.Taco.CozyCompanions.util.KeyBindings;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import javax.swing.text.JTextComponent;

public class ClientEvents {
    private static final Logger LOGGER = LogUtils.getLogger();
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

                if (KeyBindings.GLIDE_KEY.consumeClick() && amaro.isFlying()) {
                    amaro.setElytraFlying(!amaro.isElytraFlying());
                }
            }
        }
        //AmaroFlightPower capability
        @SubscribeEvent
        public static void entityAttributeCreationEvent(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof AmaroEntity) {
                if (!event.getObject().getCapability(AmaroFlightProvider.AMARO_FLIGHT_POWER).isPresent()) {
                    event.addCapability(new ResourceLocation(CozyCompanions.MODID, "properties"), new AmaroFlightProvider());
                }
            }
        }

//        @SubscribeEvent
//        public static void onPlayerTick() {
//
//        }

        @SubscribeEvent
        public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
            event.register(AmaroFlight.class);
        }
    }

    @Mod.EventBusSubscriber(modid = CozyCompanions.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {
        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(KeyBindings.DESCENDING_KEY);
            event.register(KeyBindings.GLIDE_KEY);
        }

        @SubscribeEvent
        public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
            event.registerAboveAll("flightgui", AmaroFlightHud.HUD_AMARO_FLIGHTBAR);
        }
    }
}
