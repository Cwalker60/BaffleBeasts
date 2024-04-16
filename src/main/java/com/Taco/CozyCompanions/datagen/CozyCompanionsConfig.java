package com.Taco.CozyCompanions.datagen;

import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.common.ForgeConfigSpec;

public class CozyCompanionsConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec COZY_COMPANIONS_CONFIG;

    public static ForgeConfigSpec.ConfigValue<Integer> AMARO_SPAWN_AMOUNT;
    public static ForgeConfigSpec.ConfigValue<Integer> AMARO_SPAWN_WEIGHT;

    public static void createConfig(ForgeConfigSpec.Builder builder) {
        builder.push("Amaro");

        AMARO_SPAWN_AMOUNT = BUILDER.comment("This value is the number of Amaros that spawn when they are chosen to spawn by the weight.")
                .define("Amaro_Spawn_Amount", 2);

        AMARO_SPAWN_WEIGHT = BUILDER.comment("This value is the weight of how often Amaros can spawn.")
                .define("Amaro_Spawn_Weight", 50);
        builder.pop();

        COZY_COMPANIONS_CONFIG = builder.build();
    }


}
