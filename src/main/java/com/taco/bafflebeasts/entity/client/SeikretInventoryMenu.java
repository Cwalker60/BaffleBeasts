package com.taco.bafflebeasts.entity.client;

import com.taco.bafflebeasts.entity.custom.SeikretEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;

/**
 * Class is based off of HorseInventoryMenu.java from package net.minecraft.world.inventory
 * This gives a menu that display the seikret in a window, one slot for weapons, and then 3x9 rows for items.
 */
public class SeikretInventoryMenu extends AbstractContainerMenu {
    private final Container seikretContainer;
    protected final SeikretEntity seikret;

    // Client Constructor
    public SeikretInventoryMenu(int containerId, Inventory playerInv, FriendlyByteBuf data) {
        this(containerId, playerInv,  (SeikretEntity)(playerInv.player.level()).getEntity(data.readInt()));
    }

    // Server Constructor
    public SeikretInventoryMenu(int pContainerId, Inventory pPlayerINventory, final SeikretEntity pSeikret) {
        super(BaffleMenuRegistry.SEIKRET_MENU.get(), pContainerId);
        this.seikret = pSeikret;
        this.seikretContainer = this.seikret.inventory;
        SimpleContainer pContainer = seikret.inventory;
        pContainer.startOpen(pPlayerINventory.player);

        // Add our inventory slot for the weapon
        this.addSlot(new Slot(pContainer,0,8,54) {
            public boolean mayPlace(ItemStack weapon) {
                return checkWeapon(weapon) && !this.hasItem() && seikret.isTame();
            }

            public boolean isActive() {
                return seikret.isTame();
            }
        });

        // Chest Slots
        for(int k = 0; k < 3; ++k) {
            for(int l = 0; l < seikret.getInventoryColumns(); ++l) {
                this.addSlot(new Slot(pContainer, 1 + l + k * seikret.getInventoryColumns(), 80 + l * 18, 18 + k * 18));
            }
        }

        // Player Slots (FIGURE OUT WHAT THESE MEAN)
        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 9; k++) {
                this.addSlot(new Slot(pPlayerINventory, k + i * 9 + 9, 8 + k * 18, 102 + i * 18 + -18));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(pPlayerINventory, i, 8 + i * 18, 142));
        }

    }


    // Code taken from HorseInventoryMenu
    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            int i = this.seikretContainer.getContainerSize();
            if (pIndex < i) {
                if (!this.moveItemStackTo(itemstack1, i, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.getSlot(1).mayPlace(itemstack1) && !this.getSlot(1).hasItem()) {
                if (!this.moveItemStackTo(itemstack1, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.getSlot(0).mayPlace(itemstack1)) {
                if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (i <= 2 || !this.moveItemStackTo(itemstack1, 2, i, false)) {
                int j = i + 27;
                int k = j + 9;
                if (pIndex >= j && pIndex < k) {
                    if (!this.moveItemStackTo(itemstack1, i, j, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (pIndex >= i && pIndex < j) {
                    if (!this.moveItemStackTo(itemstack1, j, k, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemstack1, j, j, false)) {
                    return ItemStack.EMPTY;
                }

                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return !this.seikret.hasInventoryChanged(this.seikretContainer) && this.seikretContainer.stillValid(pPlayer) && this.seikret.isAlive() && this.seikret.distanceTo(pPlayer) < 8.0f;
    }

    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        this.seikretContainer.stopOpen(pPlayer);
    }

    public SeikretEntity getSeikret() {
        return this.seikret;
    }

    private boolean checkWeapon(ItemStack weapon) {
        return weapon.is(Tags.Items.TOOLS) || weapon.is(Tags.Items.TOOLS_CROSSBOWS) || weapon.is(Tags.Items.TOOLS_BOWS);
    }

}
