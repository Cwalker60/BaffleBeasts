package com.taco.bafflebeasts.screens;

import com.taco.bafflebeasts.BaffleBeasts;
import com.taco.bafflebeasts.entity.client.SeikretInventoryMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SeikretGUIScreen extends AbstractContainerScreen<SeikretInventoryMenu> {

    private static final ResourceLocation SEIKRET_INVENTORY_LOCATION = new ResourceLocation(BaffleBeasts.MODID,
            "textures/gui/seikret_inventory.png");

    private float xMouse;
    private float yMouse;

    public SeikretGUIScreen(SeikretInventoryMenu pMenu, Inventory pPlayerInventory, Component title) {
        super (pMenu, pPlayerInventory,title);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        pGuiGraphics.blit(SEIKRET_INVENTORY_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);

        InventoryScreen.renderEntityInInventoryFollowsMouse(pGuiGraphics, i + 51, j + 60, 17, (float)(i + 51) - this.xMouse, (float)(j + 75 - 50) - this.yMouse, this.getMenu().getSeikret());
    }

    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);
        this.xMouse = (float)pMouseX;
        this.yMouse = (float)pMouseY;
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }
}
