package com.taco.bafflebeasts.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class BaffleServerConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec BAFFLE_COMPANIONS_CONFIG;

    public static ForgeConfigSpec.ConfigValue<Integer> AMARO_SPAWN_AMOUNT_MIN;
    public static ForgeConfigSpec.ConfigValue<Integer> AMARO_SPAWN_AMOUNT_MAX;
    public static ForgeConfigSpec.ConfigValue<Integer> AMARO_SPAWN_WEIGHT;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> AMARO_SPAWN_BIOMES;

    public static ForgeConfigSpec.ConfigValue<Integer> JELLYBAT_SPAWN_AMOUNT_MIN;
    public static ForgeConfigSpec.ConfigValue<Integer> JELLYBAT_SPAWN_AMOUNT_MAX;
    public static ForgeConfigSpec.ConfigValue<Integer> JELLYBAT_SPAWN_WEIGHT;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> JELLYBAT_SPAWN_BIOMES;

    public static ForgeConfigSpec.ConfigValue<Integer> DOZEDRAKE_AMOUNT_MIN;
    public static ForgeConfigSpec.ConfigValue<Integer> DOZEDRAKE_AMOUNT_MAX;
    public static ForgeConfigSpec.ConfigValue<Integer> DOZEDRAKE_SPAWN_WEIGHT;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> DOZEDRAKE_SPAWN_BIOMES;



    public static void createConfig(ForgeConfigSpec.Builder builder) {

        builder.push("Amaro");

        AMARO_SPAWN_AMOUNT_MIN = BUILDER.comment("This value is the MIN number of Amaros that spawn when they are chosen to spawn by the weight.")
                .define("Amaro_Min_Spawn_Amount", 1);

        AMARO_SPAWN_AMOUNT_MAX = BUILDER.comment("This value is the MAX number of Amaros that spawn when they are chosen to spawn by the weight.")
                .define("Amaro_Max_Spawn_Amount", 2);

        AMARO_SPAWN_WEIGHT = BUILDER.comment("This value is the weight of how often Amaros can spawn.")
                .define("Amaro_Spawn_Weight", 15);

        builder.push("Spawn Biomes");
        AMARO_SPAWN_BIOMES = BUILDER.comment("These values are the biomes that the Amaro will spawn in. They are seperated by ,'s." +
                "Biomes that do not exist will log an error message, an example being: BaffleServerConfig error in validating biomes for...")
                .comment("Tags will work and be checked as well")
                .comment("An example biome config would be: Amaro_Spawn_Biomes = minecraft:taiga, minecraft:plains, byg:cika_woods, #is_taiga")
                .defineListAllowEmpty("Amaro_Spawn_Biomes",  createDefaultSpawnBiomes("AMARO"), entry -> true);
        builder.pop();
        builder.pop();

        builder.push("Jelly Bat");

        JELLYBAT_SPAWN_AMOUNT_MIN = BUILDER.comment("This value is the MIN number of Jelly Bats that spawn when they are chosen to spawn by the weight.")
                .define("JellyBat_Min_Spawn_Amount", 2);

        JELLYBAT_SPAWN_AMOUNT_MAX = BUILDER.comment("This value is the MAX number of Jelly Bats that spawn when they are chosen to spawn by the weight.")
                .define("JellyBat_Max_Spawn_Amount", 4);

        JELLYBAT_SPAWN_WEIGHT = BUILDER.comment("This value is the weight of how often Jelly Bats can spawn.")
                .define("JellyBat_Spawn_Weight", 20);

        builder.push("Spawn Biomes");
        JELLYBAT_SPAWN_BIOMES = BUILDER.defineListAllowEmpty("JellyBat_Spawn_Biomes",  createDefaultSpawnBiomes("JELLYBAT"), entry -> true);
        builder.pop();
        builder.pop();

        builder.push("Doze Drake");

        DOZEDRAKE_AMOUNT_MIN = BUILDER.comment("This value is the MIN number of Doze Drakes that spawn when they are chosen to spawn by the weight.")
                .define("Dozedrake_Min_Spawn_Amount", 1);

        DOZEDRAKE_AMOUNT_MAX = BUILDER.comment("This value is the MAX number of Doze Drakes that spawn when they are chosen to spawn by the weight.")
                .define("Dozedrake_Max_Spawn_Amount", 1);

        DOZEDRAKE_SPAWN_WEIGHT = BUILDER.comment("This value is the weight of how often Doze Drakes can spawn.")
                .define("Dozedrake_Spawn_Weight", 2);

        builder.push("Spawn Biomes");
        DOZEDRAKE_SPAWN_BIOMES = BUILDER.defineListAllowEmpty("Dozedrake_Spawn_Biomes", createDefaultSpawnBiomes("DOZEDRAKE"), entry -> true);
        builder.pop();
        builder.pop();


        BAFFLE_COMPANIONS_CONFIG = builder.build();
    }
    // Helper method to create a default list of biomes in the config when createConfig() is called. Uses a mob ID for specific mob.
    public static ArrayList<String> createDefaultSpawnBiomes(String mob) {
        ArrayList<String> defaultList = new ArrayList<>();
        switch (mob) {
            case "AMARO" :
                defaultList.add("minecraft:taiga"); defaultList.add("minecraft:windswept_hills"); defaultList.add("#is_taiga");
                break;
            case "JELLYBAT" :
                defaultList.add("minecraft:lush_caves"); defaultList.add("minecraft:sparse_jungle"); defaultList.add("minecraft:jungle");
                break;
            case "DOZEDRAKE" :
                defaultList.add("minecraft:birch_forest"); defaultList.add("minecraft:flower_forest"); defaultList.add("minecraft:old_growth_birch_forest");
        }

        return defaultList;
    }
}
