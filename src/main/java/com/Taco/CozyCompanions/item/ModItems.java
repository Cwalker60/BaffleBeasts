package com.Taco.CozyCompanions.item;

import com.Taco.CozyCompanions.CozyCompanions;
import com.Taco.CozyCompanions.entity.ModEntityTypes;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, CozyCompanions.MODID);

    public static final RegistryObject<Item> AMARO_SPAWN_EGG = ITEMS.register("amaro_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.Amaro,0x948e8d, 0x3b3635,
                    new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

    public static final RegistryObject<Item> JELLYBAT_SPAWN_EGG = ITEMS.register("jellybat_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.JellyBat, 0xe07a56, 0xe07a56,
                    new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
