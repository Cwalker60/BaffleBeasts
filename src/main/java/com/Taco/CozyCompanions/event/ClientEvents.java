package com.Taco.CozyCompanions.event;

import com.Taco.CozyCompanions.CozyCompanions;
import com.Taco.CozyCompanions.entity.client.AmaroFlightHud;
import com.Taco.CozyCompanions.entity.custom.AmaroEntity;
import com.Taco.CozyCompanions.entity.custom.RideableFlightEntity;
import com.Taco.CozyCompanions.flight.AmaroFlight;
import com.Taco.CozyCompanions.flight.AmaroFlightProvider;
import com.Taco.CozyCompanions.networking.ModPackets;
import com.Taco.CozyCompanions.networking.packet.AmaroDescendC2SPacket;
import com.Taco.CozyCompanions.util.KeyBindings;
import com.mojang.logging.LogUtils;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;


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

            if (event.getObject() instanceof RideableFlightEntity) {
                if (!event.getObject().getCapability(AmaroFlightProvider.AMARO_FLIGHT_POWER).isPresent()) {
                    event.addCapability(new ResourceLocation(CozyCompanions.MODID, "properties"), new AmaroFlightProvider());
                }
            }
        }


        @SubscribeEvent
        public static void onPlayerRender(RenderPlayerEvent.Pre event) {
            //LOGGER.debug("viewYRot is " + event.getEntity().getViewYRot(event.getPartialTick()));
            if (event.getEntity().getVehicle() != null) {
                if (event.getEntity().getVehicle() instanceof RideableFlightEntity flightEntity) {
                    if (flightEntity.isFallFlying()) {
                        float pXRot = event.getEntity().getXRot() % 360;
                        float pYRot = event.getEntity().getYRot();
                        if (pYRot < 0) {
                            pYRot += 360;
                        } else {
                            pYRot = pYRot % 360;
                        }
                        // Face the player wherever the flightEntity is turning
                        event.getPoseStack().mulPose(Vector3f.XP.rotationDegrees((float)Math.cos( ((pYRot * Math.PI) / 180)) * pXRot));
                        event.getPoseStack().mulPose(Vector3f.ZP.rotationDegrees((float)Math.sin( ((pYRot * Math.PI) / 180)) * pXRot));

                        Vec3 vec3 = flightEntity.getViewVector(event.getPartialTick());
                        Vec3 vec31 = flightEntity.getDeltaMovement();
                        double d0 = vec31.horizontalDistanceSqr();
                        double d1 = vec3.horizontalDistanceSqr();
                        if (d0 > 0.0D && d1 > 0.0D) {
                            double d2 = (vec31.x * vec3.x + vec31.z * vec3.z) / Math.sqrt(d0 * d1); // angle between view/delta
                            double d3 = vec31.x * vec3.z - vec31.z * vec3.x; // positive negative change
                            float d4 = (float)(Math.signum(d3) * Math.acos(d2)); // angle to change tilt in positive/negative
                            // Apply Movement tilt on the X/Z axis where the flightEntity is facing
                            // Invert cos/sin to change the Z tilt when facing N, instead of X tilt when facing N
                            // d4 needs to be inverted to tilt when facing S
                            event.getPoseStack().mulPose(Vector3f.ZP.rotation((float)Math.cos( ((pYRot * Math.PI) / 180)) * d4));
                            event.getPoseStack().mulPose(Vector3f.XP.rotation((float)Math.sin( ((pYRot * Math.PI) / 180)) * -d4));
                        }
                    }

                }
            }

        }

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
