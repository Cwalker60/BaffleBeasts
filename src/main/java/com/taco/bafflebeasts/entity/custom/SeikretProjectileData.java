package com.taco.bafflebeasts.entity.custom;

import net.minecraft.world.entity.projectile.Projectile;

import java.util.ArrayList;

public class SeikretProjectileData {
    private int inventoryIndex;
    private ArrayList<Projectile> projectiles;


    public SeikretProjectileData(int pInventoryIndex, ArrayList<Projectile> pProjectile) {
        this.inventoryIndex = pInventoryIndex;
        this.projectiles = pProjectile;
    }

    public int getInventoryIndex() {
        return inventoryIndex;
    }

    public ArrayList<Projectile> getProjectiles() {
        return this.projectiles;
    }


}
