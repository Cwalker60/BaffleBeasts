package com.Taco.BaffleBeasts.datagen;

import com.Taco.BaffleBeasts.BaffleBeasts;
import com.Taco.BaffleBeasts.entity.ModEntityTypes;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.JsonCodecProvider;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

public class MobSpawnRulesGenerator  {



    public static void onGatherData(GatherDataEvent event) {

        MobSpawnSettings.SpawnerData amaroSpawnData = new MobSpawnSettings.SpawnerData(
                ModEntityTypes.Amaro.get(), 50, 1, 2);

        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        RegistryAccess registryAccess = RegistryAccess.builtinCopy();
        RegistryOps<JsonElement> registryOps = RegistryOps.create(JsonOps.INSTANCE, registryAccess);

        // Biome Holders
        HolderSet.Named<Biome> TAIGA_TAG = new HolderSet.Named<>(registryOps.registry(Registry.BIOME_REGISTRY).orElseThrow(), BiomeTags.IS_TAIGA);

        // Resource Locations
        ResourceLocation amaroSpawnLocation = new ResourceLocation("bafflebeasts", "amaro_spawns");

        //Biome Modifier Spawn Data
        ForgeBiomeModifiers.AddSpawnsBiomeModifier amaroSpawnsBiomeModifier = ForgeBiomeModifiers.
                AddSpawnsBiomeModifier.singleSpawn(TAIGA_TAG, amaroSpawnData);

        // Create a map for the JsonCodecProvider
        Map<ResourceLocation, BiomeModifier> modifierMap = Map.of(amaroSpawnLocation, amaroSpawnsBiomeModifier);

        // Create the provider
        JsonCodecProvider provider = JsonCodecProvider.forDatapackRegistry(
                generator, existingFileHelper, BaffleBeasts.MODID, registryOps,
                ForgeRegistries.Keys.BIOME_MODIFIERS, modifierMap
        );

        generator.addProvider(event.includeServer(), provider);
    }
}
