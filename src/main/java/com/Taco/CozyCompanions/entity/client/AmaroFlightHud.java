package com.Taco.CozyCompanions.entity.client;

import com.Taco.CozyCompanions.CozyCompanions;
import com.Taco.CozyCompanions.entity.custom.AmaroEntity;
import com.Taco.CozyCompanions.flight.AmaroFlightProvider;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class AmaroFlightHud {
    private static final ResourceLocation FILLED_FLIGHTBAR = new ResourceLocation(CozyCompanions.MODID,
            "textures/flightgui/filled_flightbar.png");
    private static final ResourceLocation EMPTY_FLIGHTBAR = new ResourceLocation(CozyCompanions.MODID,
            "textures/flightgui/empty_flightbar.png");

    public static final IGuiOverlay HUD_AMARO_FLIGHTBAR = (((gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        int x = screenWidth;
        int y = screenHeight;

        if (Minecraft.getInstance().player.getVehicle() instanceof AmaroEntity amaro) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0f,1.0f,1.0f,1.0f);
            RenderSystem.setShaderTexture(0,EMPTY_FLIGHTBAR);
            // Draw Empty Feathers
            for (int i = 0; i < 6; i++) {
                GuiComponent.blit(poseStack, x - 360 + (i*14), y - 54, 0, 0, 16, 16, 16, 16);
            }
            // Draw Filled Feathers
            RenderSystem.setShaderTexture(0,FILLED_FLIGHTBAR);

            amaro.getCapability(AmaroFlightProvider.AMARO_FLIGHT_POWER).ifPresent(amaroFlight -> {
                int filledFeathers = amaroFlight.getFlightPower();
                for (int i = 0; i < 6; i++) {
                    if (filledFeathers > i) {
                        GuiComponent.blit(poseStack, x - 360 + (i*14), y - 54, 0, 0, 16, 16, 16, 16);
                    }
                }
            });
        }

    }));
}
