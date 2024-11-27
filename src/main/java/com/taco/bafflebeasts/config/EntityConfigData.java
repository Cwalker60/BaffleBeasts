package com.taco.bafflebeasts.config;

import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;

public class EntityConfigData {

    private final EntityType<?> mobType;

    private final int maxSpawn;
    private final int minSpawn;
    private final int spawnWeight;
    private final ArrayList<String> spawnBiomes;

    /**
     * EntityConfigData is used in BaffleConfigValues.java as data to pass into ModMobSpawnModifier.class to add mob spawns in the minecraft
     * world.
     * @param entity The Entity type that the data will be based on.
     * @param maxSpawnAmount The max amount of Entities that will spawn.
     * @param minSpawnAmount The min amount of Entities that will spawn.
     * @param spawnWeightAmount The weight of how often the entity will spawn.
     * @param biomes A list of strings of resource locations for the biomes that the entity can spawn in.
     */
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
