package com.taco.bafflebeasts.datagen;

import com.taco.bafflebeasts.BaffleBeasts;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BaffleBeasts.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput po = event.getGenerator().getPackOutput();

        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        //MobSpawnRulesGenerator.onGatherData(event);
        generator.addProvider(event.includeClient(), new ModItemModelGenerator(po, existingFileHelper));
        generator.addProvider(true, new SoundGenerator(po, BaffleBeasts.MODID, existingFileHelper));

    }

}