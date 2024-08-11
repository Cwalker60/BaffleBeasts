package com.taco.bafflebeasts.config;

import com.taco.bafflebeasts.BaffleBeasts;
import com.taco.bafflebeasts.entity.ModEntityTypes;
import com.taco.bafflebeasts.util.RegistryUtility;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import org.w3c.dom.Entity;

import java.util.ArrayList;

public class BaffleConfigValues {
    // Amaro Values
    public static final ArrayList<String> AMARO_BIOME_SPAWN_LIST =
            validateBiomes(new ArrayList<String>(BaffleServerConfig.AMARO_SPAWN_BIOMES.get()), "AMARO");
    public static final ArrayList<String> JELLYBAT_BIOME_SPAWN_LIST =
            validateBiomes(new ArrayList<String>(BaffleServerConfig.JELLYBAT_SPAWN_BIOMES.get()), "JELLYBAT");

    public static final EntityConfigData AMARO_CONFIG_DATA = new EntityConfigData(ModEntityTypes.Amaro.get(),
            BaffleServerConfig.AMARO_SPAWN_AMOUNT_MAX.get(),
            BaffleServerConfig.AMARO_SPAWN_AMOUNT_MIN.get(),BaffleServerConfig.AMARO_SPAWN_WEIGHT.get(),
            AMARO_BIOME_SPAWN_LIST);

    public static final EntityConfigData JELLYBAT_CONFIG_DATA = new EntityConfigData(ModEntityTypes.JellyBat.get(),
            BaffleServerConfig.JELLYBAT_SPAWN_AMOUNT_MAX.get(), BaffleServerConfig.JELLYBAT_SPAWN_AMOUNT_MIN.get(),
            BaffleServerConfig.JELLYBAT_SPAWN_WEIGHT.get(),JELLYBAT_BIOME_SPAWN_LIST);


    /**
     *  validateList will take in a list of biomes from the CozyCompaniosnConfig.java class and return the list
     *  with invalid biomes removed.
     * @param biomeList List of biomes to check over.
     * @param mobName Name of the mob that is validating.
     * @return List of biomes with invalid biomes removed.
     */
    public static ArrayList<String> validateBiomes(ArrayList<String> biomeList, String mobName) {
        ArrayList<String> validatedBiomes = new ArrayList<String>();

        // for each string, check if the biome exists
        for (String s: biomeList) {
            // Biome registry check.
            // Check if the string is a tag by checking for # at the start.
            if (s.startsWith("#")) {
                String tagString = s.substring(1, s.length());
                validatedBiomes.add(tagString);
                // Check if the biome exists in the registries, and add the biomes to the validated list
            }

            else if (RegistryUtility.getRegistryAccess().registryOrThrow(Registries.BIOME).containsKey(new ResourceLocation(s))) {
                validatedBiomes.add(s);
            } else {
                BaffleBeasts.MAIN_LOGGER.warn("BaffleServerConfig error in validating biomes for " + mobName + ". "
                        + "Invalid biome  " + s + " and will not be loaded in game.");
            }

        }

        return validatedBiomes;
    }
}
