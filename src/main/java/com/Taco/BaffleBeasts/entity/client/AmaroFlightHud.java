package com.Taco.BaffleBeasts.entity.client;

import com.Taco.BaffleBeasts.BaffleBeasts;
import com.Taco.BaffleBeasts.config.BaffleClientConfig;
import com.Taco.BaffleBeasts.entity.custom.RideableFlightEntity;
import com.Taco.BaffleBeasts.flight.AmaroFlightProvider;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class AmaroFlightHud {
    private static final ResourceLocation FILLED_FLIGHTBAR = new ResourceLocation(BaffleBeasts.MODID,
            "textures/flightgui/filled_flightbar.png");
    private static final ResourceLocation EMPTY_FLIGHTBAR = new ResourceLocation(BaffleBeasts.MODID,
            "textures/flightgui/empty_flightbar.png");
    private static final ResourceLocation ANIMATED_FLIGHTBAR = new ResourceLocation(BaffleBeasts.MODID,
            "textures/flightgui/twinkle_flightbar.png");
    public static int FLIGHT_ANIMATION_DRAWSTATE = 0;
    public static boolean STOP_DRAW = false;

    public static final IGuiOverlay HUD_AMARO_FLIGHTBAR = (((gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        int x = screenWidth;
        int y = screenHeight;
        // (x-16*6)/2 is the distance 6 feathers are drawn from the center

        if (Minecraft.getInstance().player.getVehicle() instanceof RideableFlightEntity flightEntity) {
            int feathers = flightEntity.flightPower;
            int xOffset = (x - 16 * feathers)/2;
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0f,1.0f,1.0f,1.0f);
            RenderSystem.setShaderTexture(0,EMPTY_FLIGHTBAR);
            // Draw Empty Feathers
            // 360 was last hard coded
            for (int i = 0; i < feathers; i++) {
                GuiComponent.blit(poseStack, xOffset + (i*14) + BaffleClientConfig.FLIGHT_HUD_X_OFFSET.get(), y - 60 - BaffleClientConfig.FLIGHT_HUD_Y_OFFSET.get(),
                        0, 0, 16, 16, 16, 16);
            }
            // Draw Filled Feathers
            RenderSystem.setShaderTexture(0,FILLED_FLIGHTBAR);

            flightEntity.getCapability(AmaroFlightProvider.AMARO_FLIGHT_POWER).ifPresent(amaroFlight -> {
                int filledFeathers = amaroFlight.getFlightPower();
                int gradualFill = flightEntity.flightRechargeBuffer / (flightEntity.maxFlightRechargeBuffer / 16); // amount of the filled feather to draw
                for (int i = 0; i < feathers; i++) {
                    // Feathers "filling up" slowly
                    if (filledFeathers == i) {
                        GuiComponent.blit(poseStack, xOffset + (i*14) + BaffleClientConfig.FLIGHT_HUD_X_OFFSET.get(), y - 60 + (gradualFill) - BaffleClientConfig.FLIGHT_HUD_Y_OFFSET.get(), 0,  gradualFill, 16,  16 - gradualFill, 16, 16);
                    }
                    // Full Feathers
                    if (filledFeathers > i) {
                        GuiComponent.blit(poseStack, xOffset + (i*14) + BaffleClientConfig.FLIGHT_HUD_X_OFFSET.get(), y - 60 - BaffleClientConfig.FLIGHT_HUD_Y_OFFSET.get(), 0, 0, 16, 16, 16, 16);
                    }
                }

                //Draw the animated texture on when flight power is available.
                RenderSystem.setShaderTexture(0, ANIMATED_FLIGHTBAR);
                if (!STOP_DRAW && amaroFlight.getFlightPower() != 0)  {
                    GuiComponent.blit(poseStack, xOffset + ((amaroFlight.getFlightPower() - 1)*14) + BaffleClientConfig.FLIGHT_HUD_X_OFFSET.get(), y - 60 - BaffleClientConfig.FLIGHT_HUD_Y_OFFSET.get(), 0,
                            FLIGHT_ANIMATION_DRAWSTATE * 16 , 16, 16, 16, 256);
                }


            });
        }


    }));
    // This method is fired in the tick of the Amaro to fill the pixels of the
    // feather for what value of FLIGHT_ANIMATION_DRAWSTATE is in.
    // once it is over 18, it will stop being called by setting STOP_DRAW to true
    public static void updateFlightPowerGUI() {
        STOP_DRAW = false;
        FLIGHT_ANIMATION_DRAWSTATE++;
        if (FLIGHT_ANIMATION_DRAWSTATE > 18) {
            FLIGHT_ANIMATION_DRAWSTATE = 0;
            STOP_DRAW = true;
        }
    }

    public static int getFlightAnimationDrawstate() {
        return FLIGHT_ANIMATION_DRAWSTATE;
    }
}
