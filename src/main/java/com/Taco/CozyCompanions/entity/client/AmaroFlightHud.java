package com.Taco.CozyCompanions.entity.client;

import com.Taco.CozyCompanions.CozyCompanions;
import com.Taco.CozyCompanions.entity.custom.AmaroEntity;
import com.Taco.CozyCompanions.flight.AmaroFlightProvider;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.slf4j.Logger;

public class AmaroFlightHud {
    private static final ResourceLocation FILLED_FLIGHTBAR = new ResourceLocation(CozyCompanions.MODID,
            "textures/flightgui/filled_flightbar.png");
    private static final ResourceLocation EMPTY_FLIGHTBAR = new ResourceLocation(CozyCompanions.MODID,
            "textures/flightgui/empty_flightbar.png");
    private static final ResourceLocation ANIMATED_FLIGHTBAR = new ResourceLocation(CozyCompanions.MODID,
            "textures/flightgui/twinkle_flightbar.png");
    public static int FLIGHT_ANIMATION_DRAWSTATE = 0;
    public static boolean STOP_DRAW = false;

    public static final IGuiOverlay HUD_AMARO_FLIGHTBAR = (((gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        int x = screenWidth;
        int y = screenHeight;
        // (x-16*6)/2 is the distance 6 feathers are drawn from the center
        int xOffset = (x - 16*6)/2;

        if (Minecraft.getInstance().player.getVehicle() instanceof AmaroEntity amaro) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0f,1.0f,1.0f,1.0f);
            RenderSystem.setShaderTexture(0,EMPTY_FLIGHTBAR);
            // Draw Empty Feathers
            // 360 was last hard coded
            for (int i = 0; i < 6; i++) {
                GuiComponent.blit(poseStack, 0 + xOffset + (i*14), y - 60, 0, 0, 16, 16, 16, 16);
            }
            // Draw Filled Feathers
            RenderSystem.setShaderTexture(0,FILLED_FLIGHTBAR);

            amaro.getCapability(AmaroFlightProvider.AMARO_FLIGHT_POWER).ifPresent(amaroFlight -> {
                int filledFeathers = amaroFlight.getFlightPower();
                int gradualFill = amaro.flightRechargeBuffer / 6; // amount of the filled feather to draw
                for (int i = 0; i < 6; i++) {
                    // Feathers "filling up" slowly
                    if (filledFeathers == i) {
                        GuiComponent.blit(poseStack, 0 + xOffset + (i*14), y - 60 + (gradualFill), 0,  gradualFill, 16,  16 - gradualFill, 16, 16);
                    }
                    // Full Feathers
                    if (filledFeathers > i) {
                        GuiComponent.blit(poseStack, 0 + xOffset + (i*14), y - 60, 0, 0, 16, 16, 16, 16);
                    }
                }

                //Draw the animated texture on when flight power is available.
                RenderSystem.setShaderTexture(0, ANIMATED_FLIGHTBAR);
                if (!STOP_DRAW && amaroFlight.getFlightPower() != 0)  {
                    GuiComponent.blit(poseStack, 0 + xOffset + ((amaroFlight.getFlightPower() - 1)*14), y - 60, 0,
                            FLIGHT_ANIMATION_DRAWSTATE * 16 , 16, 16, 16, 256);
                }


            });
        }


    }));

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
