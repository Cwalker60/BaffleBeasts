package com.taco.bafflebeasts.entity.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.taco.bafflebeasts.BaffleBeasts;
import com.taco.bafflebeasts.entity.custom.DozeDrakeEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class BubblePowerHud {
    private static final ResourceLocation EMPTY_BUBBLE_ICON = new ResourceLocation(BaffleBeasts.MODID,
            "textures/mounthuds/dozedrake/bubble_outline.png");
    private static final ResourceLocation FULL_BUBBLE_ICON = new ResourceLocation(BaffleBeasts.MODID,
            "textures/mounthuds/dozedrake/bubble_full.png");
    private static final ResourceLocation ANIMATED_BUBBLE_ICON = new ResourceLocation(BaffleBeasts.MODID,
            "textures/mounthuds/dozedrake/bubble_animated.png");
    public static int BUBBLE_ANIMATION_DRAWSTATE = 0; // Used to draw the animated texture.
    public static boolean STOP_DRAW = false;

    public static final IGuiOverlay HUD_BUBBLE_ATTACK = ((((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        int x = screenWidth;
        int y = screenHeight;

        if (Minecraft.getInstance().player.getVehicle() instanceof DozeDrakeEntity dozeDrake) {
            boolean hasCharge = dozeDrake.isBubbleBlasting();
            int xOffset = (x - 24)/2;
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0f,1.0f,1.0f,1.0f);

            RenderSystem.setShaderTexture(0, EMPTY_BUBBLE_ICON);
            // Draw Empty Bubble at middle of the screen.
            // 85 is base height
            guiGraphics.blit(EMPTY_BUBBLE_ICON,xOffset,y - 85,
                    0,0,24,24,24,24);

            // Filled in Bubble.
            RenderSystem.setShaderTexture(0, FULL_BUBBLE_ICON);
            int gradualFill = dozeDrake.getBubbleBlastCooldown() / ( 100 / 24);
            if (!dozeDrake.isBubbleBlasting()) {
                guiGraphics.blit(FULL_BUBBLE_ICON,xOffset,y - 61 - gradualFill,
                        0, 24 - gradualFill,24, gradualFill ,24,24);
            } else {
                guiGraphics.blit(FULL_BUBBLE_ICON,xOffset,y - 85,
                        0,0,24,24 ,24,24);
            }

            // Animated Bubble
            RenderSystem.setShaderTexture(0, ANIMATED_BUBBLE_ICON);
            if (!STOP_DRAW && dozeDrake.isBubbleBlasting()) {
                guiGraphics.blit(ANIMATED_BUBBLE_ICON,xOffset,y - 85,
                        0,BUBBLE_ANIMATION_DRAWSTATE * 24,24,24,24,216);
            }
        }

    })));

    public static void updateBubbleGUI() {
        STOP_DRAW = false;
        BUBBLE_ANIMATION_DRAWSTATE++;
        if (BUBBLE_ANIMATION_DRAWSTATE > 11) {
            BUBBLE_ANIMATION_DRAWSTATE = 0;
            STOP_DRAW = true;
        }
    }

    public static int getBubbleAnimationDrawstate() {
        return BUBBLE_ANIMATION_DRAWSTATE;
    }
}
