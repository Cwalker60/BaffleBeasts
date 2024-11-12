package com.taco.bafflebeasts.item;

import com.taco.bafflebeasts.BaffleBeasts;
import com.taco.bafflebeasts.entity.ModEntityTypes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryManager;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, BaffleBeasts.MODID);

    public static final RegistryObject<Item> AMARO_SPAWN_EGG = ITEMS.register("amaro_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.Amaro,0x948e8d, 0x3b3635,
                    new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> JELLYBAT_SPAWN_EGG = ITEMS.register("jellybat_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.JellyBat, 0xe07a56, 0xe07a56,
                    new Item.Properties()));
    public static final RegistryObject<Item> DOZEDRAKE_SPAWN_EGG = ITEMS.register("dozedrake_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.DozeDrake, 0xfcba03, 0xda5f19,
                    new Item.Properties()));

    public static final RegistryObject<JellyDonutItem> JELLYBAT_DONUT = ITEMS.register("jellybat_donut",
            () -> new JellyDonutItem(new Item.Properties()
                    .food(new FoodProperties.Builder().nutrition(6).saturationMod(1.0f).build())));

    public static final RegistryObject<Item> SUPER_SHAKE = ITEMS.register("super_shake",
            () -> new Item(new Item.Properties()));

    @SubscribeEvent
    public static void register(IEventBus eventBus) {
        //registerDonutItems();
        ITEMS.register(eventBus);
    }

    /**
     * For each mob effect registered, create a donut item with that effect.
     */
//    public static void registerDonutItems() {
//        ArrayList<RegistryObject<JellyDonutItem>> donutItems = new ArrayList<>();
//
//        RegistryManager.ACTIVE.getRegistry(ForgeRegistries.Keys.POTIONS).getEntries().forEach(potions -> {
//            if (!potions.getValue().getEffects().isEmpty())  {
//                RegistryObject<JellyDonutItem> item = ITEMS.register("jellybat_donut_" + potions.getKey().location().getPath(),
//                        () -> new JellyDonutItem(new Item.Properties().tab(CreativeModeTab.TAB_FOOD)
//                                .food(new FoodProperties.Builder().nutrition(6).saturationMod(1.0f)
//                                        .effect(() -> potions.getValue().getEffects().get(0), 1.0f).build()),
//                                potions.getValue().getEffects().get(0).getEffect().getColor()));
//
//            }
//        });
//
//    }


}
