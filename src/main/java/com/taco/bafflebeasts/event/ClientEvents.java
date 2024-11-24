package com.taco.bafflebeasts.event;

import com.mojang.math.Axis;
import com.taco.bafflebeasts.BaffleBeasts;
import com.taco.bafflebeasts.entity.client.BubblePowerHud;
import com.taco.bafflebeasts.entity.client.FlightPowerHud;
import com.taco.bafflebeasts.entity.custom.DozeDrakeEntity;
import com.taco.bafflebeasts.entity.custom.RideableFlightEntity;
import com.taco.bafflebeasts.flight.FlightPower;
import com.taco.bafflebeasts.flight.FlightPowerProvider;
import com.taco.bafflebeasts.item.JellyDonutItem;
import com.taco.bafflebeasts.item.ModItems;
import com.taco.bafflebeasts.networking.ModPackets;
import com.taco.bafflebeasts.networking.packet.DozeDrakeMountAttackC2SPacket;
import com.taco.bafflebeasts.networking.packet.FlightEntityDescendC2SPacket;
import com.taco.bafflebeasts.networking.packet.FlightEntityMovementSyncC2S;
import com.taco.bafflebeasts.util.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ClientEvents {
    @Mod.EventBusSubscriber(modid = BaffleBeasts.MODID, value = Dist.CLIENT)
    public static class ClientForgeEvents {

        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            // Check if the entity has a controlling player
            RideableFlightEntity flightEntity = null;
            LocalPlayer player = Minecraft.getInstance().player;

            if (Minecraft.getInstance().player != null) {
                if (Minecraft.getInstance().player.getVehicle() instanceof RideableFlightEntity) {
                    flightEntity = (RideableFlightEntity) Minecraft.getInstance().player.getVehicle();
                    if (!flightEntity.hasControllingPassenger()) {
                        flightEntity = null;
                    }
                }
            }

            if (flightEntity != null) {
                // Descend Key bind
                if(KeyBindings.DESCENDING_KEY.isDown() && flightEntity.isFlying()) {
                    ModPackets.sendToServer(new FlightEntityDescendC2SPacket());
                    flightEntity.setDescend(true);
                } else {
                    flightEntity.setDescend(false);
                }

                // Mount Glide Key bind
                if (KeyBindings.GLIDE_KEY.consumeClick() && flightEntity.isFlying()) {
                        flightEntity.setElytraFlying(!flightEntity.isElytraFlying());
                        ModPackets.sendToServer(new FlightEntityMovementSyncC2S(flightEntity.isMoving,
                                flightEntity.getId(), flightEntity.isElytraFlying()));

                }

                // Mount Attack bind
                if (KeyBindings.MOUNT_ATTACK_KEY.consumeClick()) {
                    // DozeDrake attack
                    if (flightEntity instanceof DozeDrakeEntity) {
                        DozeDrakeEntity dozeDrake = (DozeDrakeEntity)flightEntity;
                        Vec3 playerLook = player.getLookAngle();
                        if (dozeDrake.isBubbleBlasting()) {
                            ModPackets.sendToServer(new DozeDrakeMountAttackC2SPacket(playerLook.x, playerLook.y, playerLook.z));;
                        }

                    }
                }
            }

        }

        //AmaroFlightPower capability
        @SubscribeEvent
        public static void entityAttributeCreationEvent(AttachCapabilitiesEvent<Entity> event) {

            if (event.getObject() instanceof RideableFlightEntity) {
                if (!event.getObject().getCapability(FlightPowerProvider.AMARO_FLIGHT_POWER).isPresent()) {
                    event.addCapability(new ResourceLocation(BaffleBeasts.MODID, "properties"), new FlightPowerProvider());
                }
            }
        }


        @SubscribeEvent
        public static void onPlayerRender(RenderPlayerEvent event) {
            //LOGGER.debug("viewYRot is " + event.getEntity().getViewYRot(event.getPartialTick()));
            if (event.getEntity().getVehicle() != null) {
                if (event.getEntity().getVehicle() instanceof RideableFlightEntity flightEntity) {
                    if (flightEntity.isElytraFlying()) {
                        float pXRot = flightEntity.getXRot() % 360;
                        float pYRot = flightEntity.getYRot();

                        if (pYRot < 0) {
                            pYRot += 360;
                        } else {
                            pYRot = pYRot % 360;
                        }

                        // Face the player wherever the flightEntity is tilting
                        event.getPoseStack().mulPose(Axis.XP.rotationDegrees((float) Math.cos(((pYRot * Math.PI) / 180)) * pXRot));
                        event.getPoseStack().mulPose(Axis.ZP.rotationDegrees((float) Math.sin(((pYRot * Math.PI) / 180)) * pXRot));


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
                            event.getPoseStack().mulPose(Axis.ZP.rotation((float)Math.cos( ((pYRot * Math.PI) / 180)) * d4));
                            event.getPoseStack().mulPose(Axis.XP.rotation((float)Math.sin( ((pYRot * Math.PI) / 180)) * -d4));
                        }
                    }

                }
            }

        }

        @SubscribeEvent
        public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
            event.register(FlightPower.class);
        }
    }

    @Mod.EventBusSubscriber(modid = BaffleBeasts.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {
        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(KeyBindings.DESCENDING_KEY);
            event.register(KeyBindings.GLIDE_KEY);
            event.register(KeyBindings.MOUNT_ATTACK_KEY);
        }

        @SubscribeEvent
        public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
            event.registerAboveAll("flightgui", FlightPowerHud.HUD_AMARO_FLIGHTBAR);
            event.registerAboveAll("dozedrake_mountattack", BubblePowerHud.HUD_BUBBLE_ATTACK);
        }
        @SubscribeEvent
        public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
            event.register(JellyDonutItem::getColor, ModItems.JELLYBAT_DONUT.get());
        }


    }
}
