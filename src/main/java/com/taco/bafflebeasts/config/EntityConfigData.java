package com.taco.bafflebeasts.config;

import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;

public class EntityConfigData {

    private final EntityType<?> mobType;

    private final int maxSpawn;
    private final int minSpawn;
    private final int spawnWeight;
    private final ArrayList<String> spawnBiomes;

    public EntityConfigData(EntityType<?> entity, int maxSpawnAmount, int minSpawnAmount, int spawnWeightAmount, ArrayList<String> biomes) {
        mobType = entity;
        maxSpawn = maxSpawnAmount;
        minSpawn = minSpawnAmount;
        spawnWeight = spawnWeightAmount;
        spawnBiomes = biomes;
    }

    public EntityType<?> getMobType() {
        return mobType;
    }

    public int getMaxSpawn() {
        return maxSpawn;
    }

    public int getMinSpawn() {
        return minSpawn;
    }

    public int getSpawnWeight() {
        return spawnWeight;
    }

    public ArrayList<String> getSpawnBiomes() {
        return spawnBiomes;
    }

}
