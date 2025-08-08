package com.taco.bafflebeasts.entity.client;

import com.taco.bafflebeasts.BaffleBeasts;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BaffleMenuRegistry {

    public static DeferredRegister<MenuType<?>> MENU_REG = DeferredRegister.create(ForgeRegistries.MENU_TYPES, BaffleBeasts.MODID);

    public static final RegistryObject<MenuType<SeikretInventoryMenu>> SEIKRET_MENU = MENU_REG.register("seikret_menu", () ->
            IForgeMenuType.create(SeikretInventoryMenu::new));

    @SubscribeEvent
    public static void register(IEventBus bus) {
        MENU_REG.register(bus);
    }
}
