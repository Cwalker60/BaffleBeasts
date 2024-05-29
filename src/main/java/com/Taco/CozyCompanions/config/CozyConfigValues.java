package com.Taco.CozyCompanions.config;

import com.Taco.CozyCompanions.CozyCompanions;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;

public class CozyConfigValues {
    // Amaro Values
    public static final ArrayList<String> AMARO_BIOME_SPAWN_LIST =
            validateBiomes(new ArrayList<String>(CozyServerConfig.AMARO_SPAWN_BIOMES.get()), "AMARO");

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
            // Check if the biome exists in the registries, and add the biomes to eh validated list
            } else if (ForgeRegistries.BIOMES.containsKey(new ResourceLocation(s))) {
                validatedBiomes.add(s);
            } else {
                CozyCompanions.MAIN_LOGGER.warn("CozyServerConfig error in validating biomes for " + mobName + "."
                        + "Invalid biome being checked is " + s + " and will not be loaded in game.");
            }
        }

        return validatedBiomes;
    }
}
