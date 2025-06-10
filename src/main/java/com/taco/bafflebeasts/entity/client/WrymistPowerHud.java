package com.taco.bafflebeasts.entity.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.taco.bafflebeasts.BaffleBeasts;
import com.taco.bafflebeasts.config.BaffleClientConfig;
import com.taco.bafflebeasts.entity.custom.WrymistEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class WrymistPowerHud {
    private static final ResourceLocation EMPTY_BRUSH_ICON = new ResourceLocation(BaffleBeasts.MODID,
            "textures/mounthuds/wrymist/wrymist_outline.png");
    private static final ResourceLocation FULL_BRUSH_ICON = new ResourceLocation(BaffleBeasts.MODID,
            "textures/mounthuds/wrymist/wrymist_full.png");
    private static final ResourceLocation ANIMATED_BRUSH_ICON = new ResourceLocation(BaffleBeasts.MODID,
            "textures/mounthuds/wrymist/wrymist_animated.png");
    public static int BRUSH_ANIMATION_DRAWSTATE = 0; // Used to draw the animated texture.
    public static boolean STOP_DRAW = false;

    public static final IGuiOverlay HUD_BRUSH_ATTACK = ((((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        int x = screenWidth;
        int y = screenHeight;

        if (Minecraft.getInstance().player.getVehicle() instanceof WrymistEntity wrmyist) {
            boolean hasCharge = wrmyist.canTailAttack();
            int xOffset = (x - 24)/2;
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0f,1.0f,1.0f,1.0f);

            RenderSystem.setShaderTexture(0, EMPTY_BRUSH_ICON);
            // Draw Empty Bubble at middle of the screen.
            // 85 is base height
            guiGraphics.blit(EMPTY_BRUSH_ICON,xOffset + BaffleClientConfig.MOUNT_ATTACK_HUD_X_OFFSET.get(),y - 85 + BaffleClientConfig.MOUNT_ATTACK_HUD_Y_OFFSET.get(),
                    0,0,24,24,24,24);

            // Filled in Bubble.
            RenderSystem.setShaderTexture(0, FULL_BRUSH_ICON);
            int gradualFill = (wrmyist.getTailAttackCooldown() / ( 100 / 24)) - 1;
            if (!wrmyist.canTailAttack()) {
                guiGraphics.blit(FULL_BRUSH_ICON,xOffset + BaffleClientConfig.MOUNT_ATTACK_HUD_X_OFFSET.get(),y - 61 - gradualFill + BaffleClientConfig.MOUNT_ATTACK_HUD_Y_OFFSET.get(),
                        0, 24 - gradualFill,24, gradualFill ,24,24);
            } else {
                guiGraphics.blit(FULL_BRUSH_ICON,xOffset + BaffleClientConfig.MOUNT_ATTACK_HUD_X_OFFSET.get(),y - 85 + BaffleClientConfig.MOUNT_ATTACK_HUD_Y_OFFSET.get(),
                        0,0,24,24 ,24,24);
            }

            // Animated Bubble
            RenderSystem.setShaderTexture(0, ANIMATED_BRUSH_ICON);
            if (!STOP_DRAW && wrmyist.canTailAttack()) {
                guiGraphics.blit(ANIMATED_BRUSH_ICON,xOffset + BaffleClientConfig.MOUNT_ATTACK_HUD_X_OFFSET.get(),y - 85 + BaffleClientConfig.MOUNT_ATTACK_HUD_Y_OFFSET.get(),
                        0,BRUSH_ANIMATION_DRAWSTATE * 24,24,24,24,216);
            }
        }

    })));

    public static void updateWrymistGUI() {
        STOP_DRAW = false;
        BRUSH_ANIMATION_DRAWSTATE++;
        if (BRUSH_ANIMATION_DRAWSTATE > 11) {
            BRUSH_ANIMATION_DRAWSTATE = 0;
            STOP_DRAW = true;
        }
    }

    public static int getWrymistAnimationDrawstate() {
        return BRUSH_ANIMATION_DRAWSTATE;
    }
}
