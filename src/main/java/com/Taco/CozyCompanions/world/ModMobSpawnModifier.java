package com.Taco.CozyCompanions.world;

import com.Taco.CozyCompanions.CozyCompanions;
import com.Taco.CozyCompanions.datagen.CozyCompanionsConfig;
import com.Taco.CozyCompanions.entity.ModEntityTypes;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.MobSpawnSettingsBuilder;
import net.minecraftforge.common.world.ModifiableBiomeInfo;


public record ModMobSpawnModifier(boolean config) implements BiomeModifier {
    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        CozyCompanions.MAIN_LOGGER.debug("Amaro Spawn Amount is " + CozyCompanionsConfig.AMARO_SPAWN_AMOUNT.get());
        if (phase == Phase.ADD) {
            builder.getMobSpawnSettings().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(
                    ModEntityTypes.Amaro.get(), 50, CozyCompanionsConfig.AMARO_SPAWN_AMOUNT.get(), CozyCompanionsConfig.AMARO_SPAWN_AMOUNT.get()));
        }
    }
    @Override
    public Codec<? extends BiomeModifier> codec() {
        return BiomeModifierRegistry.MOD_MOB_SPAWN_CODEX.get();
    }
}

