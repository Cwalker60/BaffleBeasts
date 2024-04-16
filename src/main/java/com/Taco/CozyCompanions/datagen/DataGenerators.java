package com.Taco.CozyCompanions.datagen;

import com.Taco.CozyCompanions.CozyCompanions;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = CozyCompanions.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        MobSpawnRulesGenerator.onGatherData(event);
        generator.addProvider(true, new SoundGenerator(generator, CozyCompanions.MODID, existingFileHelper));

    }

}
