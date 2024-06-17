package com.Taco.BaffleBeasts.datagen;

import com.Taco.BaffleBeasts.BaffleBeasts;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BaffleBeasts.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        MobSpawnRulesGenerator.onGatherData(event);

        generator.addProvider(event.includeClient(), new ModItemModelGenerator(generator, existingFileHelper));
        generator.addProvider(true, new SoundGenerator(generator, BaffleBeasts.MODID, existingFileHelper));

    }

}
