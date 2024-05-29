package com.Taco.CozyCompanions.world;

import com.Taco.CozyCompanions.config.CozyServerConfig;
import com.Taco.CozyCompanions.config.CozyConfigValues;
import com.Taco.CozyCompanions.entity.ModEntityTypes;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;


public record ModMobSpawnModifier(boolean config) implements BiomeModifier {
    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        // Amaro biome Modifier
        if (phase == Phase.ADD) {
            // Check through the biome resource keys to see if the resource location matches with
            // CozyCompanions Config
            biome.unwrapKey().ifPresent(biomeResourceKey -> {
                ResourceLocation biomeID = biomeResourceKey.location();
                ArrayList<String> biomeList = CozyConfigValues.AMARO_BIOME_SPAWN_LIST;
                biomeList = removeDuplicates(biome, biomeID, biomeList);

                // if the tag is in the config list, add the amaro to that biome with that tag.
                for (String s : biomeList) {
                    TagKey<Biome> tag = TagKey.create(ForgeRegistries.BIOMES.getRegistryKey(), new ResourceLocation(s));

                    if (biome.containsTag(tag)) {
                        builder.getMobSpawnSettings().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(ModEntityTypes.Amaro.get(),
                                50, CozyServerConfig.AMARO_SPAWN_AMOUNT_MIN.get(), CozyServerConfig.AMARO_SPAWN_AMOUNT_MAX.get()));

                    }
                }

                if (biomeList.contains(biomeID.toString())) {
                    builder.getMobSpawnSettings().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(ModEntityTypes.Amaro.get(),
                            50, CozyServerConfig.AMARO_SPAWN_AMOUNT_MIN.get(), CozyServerConfig.AMARO_SPAWN_AMOUNT_MAX.get()));
                }
            });

        }

        // TEMPLATE PHASE

//        if (phase == Phase.ADD) {
//            builder.getMobSpawnSettings().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(
//                    ModEntityTypes.Amaro.get(), 50, CozyServerConfig.AMARO_SPAWN_AMOUNT.get(), CozyServerConfig.AMARO_SPAWN_AMOUNT.get()));
//        }
    }
    @Override
    public Codec<? extends BiomeModifier> codec() {
        return BiomeModifierRegistry.MOD_MOB_SPAWN_CODEX.get();
    }

    public ArrayList<String> removeDuplicates (Holder<Biome> biome, ResourceLocation biomeID, ArrayList<String> listWithDuplicates) {
        ArrayList<String> duplicateBiomes = new ArrayList<String>();
        ArrayList<String> returnList = listWithDuplicates;

        // Iterate through the biome List searching for biomes in the config list that are already from a tag in the same list.
        for (String s : listWithDuplicates) {
            TagKey<Biome> tag = TagKey.create(ForgeRegistries.BIOMES.getRegistryKey(), new ResourceLocation(s));
            // if the current biome has a tag from the list, check if that biome is in the list and add it to duplicate biomes.
            if (biome.containsTag(tag)) {
                if (listWithDuplicates.contains(biomeID.toString())) {
                    duplicateBiomes.add(biomeID.toString());
                }
            }
        }
        // Remove duplicates
        for (String duplicate : duplicateBiomes) {
            if (listWithDuplicates.contains(duplicate)) {
                returnList.remove(duplicate);
            }
        }

        return returnList;
    }
}

