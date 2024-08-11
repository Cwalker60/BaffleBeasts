package com.taco.bafflebeasts.world;

import com.mojang.serialization.Codec;
import com.taco.bafflebeasts.BaffleBeasts;
import com.taco.bafflebeasts.config.BaffleConfigValues;
import com.taco.bafflebeasts.config.BaffleServerConfig;
import com.taco.bafflebeasts.config.EntityConfigData;
import com.taco.bafflebeasts.entity.ModEntityTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
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
            // BaffleBeasts Config
            biome.unwrapKey().ifPresent(biomeResourceKey -> {

                // Amaro
                // if the tag is in the config list, add the amaro to that biome with that tag.
                addEntitySpawn(BaffleConfigValues.AMARO_CONFIG_DATA, biomeResourceKey, biome, builder, MobCategory.CREATURE);
                addEntitySpawn(BaffleConfigValues.JELLYBAT_CONFIG_DATA, biomeResourceKey, biome, builder, MobCategory.AMBIENT);
            });

        }

    }

    public void addEntitySpawn(EntityConfigData configData, ResourceKey<Biome> biomeResourceKey, Holder<Biome> biome,
                               ModifiableBiomeInfo.BiomeInfo.Builder builder, MobCategory category) {

        ResourceLocation biomeID = biomeResourceKey.location();
        ArrayList<String> biomeList = configData.getSpawnBiomes();
        biomeList = removeDuplicates(biome, biomeID, biomeList);

        // if the tag is in the config list, add the amaro to that biome with that tag.
        for (String s : biomeList) {
            TagKey<Biome> tag = TagKey.create(ForgeRegistries.BIOMES.getRegistryKey(), new ResourceLocation(s));

            if (biome.containsTag(tag)) {
                builder.getMobSpawnSettings().addSpawn(category, new MobSpawnSettings.SpawnerData(configData.getMobType(),
                        configData.getSpawnWeight(), configData.getMinSpawn(), configData.getMaxSpawn()));

            }
        }
        if (biomeList.contains(biomeID.toString())) {
            builder.getMobSpawnSettings().addSpawn(category, new MobSpawnSettings.SpawnerData(configData.getMobType(),
                    configData.getSpawnWeight(), configData.getMinSpawn(), configData.getMaxSpawn()));
        }
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

    public ArrayList<String> getSpawnList(String entityName) {
        ArrayList<String> biomeList = new ArrayList<>();
        switch (entityName) {
            case "AMARO" :
                return biomeList = BaffleConfigValues.AMARO_BIOME_SPAWN_LIST;
            case "JELLYBAT" :
                return biomeList = BaffleConfigValues.JELLYBAT_BIOME_SPAWN_LIST;
        }

        return biomeList;
    }

}
