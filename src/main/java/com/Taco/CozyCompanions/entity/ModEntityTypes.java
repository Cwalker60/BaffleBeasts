package com.Taco.CozyCompanions.entity;

import com.Taco.CozyCompanions.CozyCompanions;
import com.Taco.CozyCompanions.entity.custom.AmaroEntity;
import com.Taco.CozyCompanions.entity.custom.JellyBatEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CozyCompanions.MODID);

    public static final RegistryObject<EntityType<AmaroEntity>> Amaro =
            ENTITY_TYPES.register("amaro",
                    () -> EntityType.Builder.of(AmaroEntity::new, MobCategory.CREATURE)
            .sized(1.5f, 3.2f)
            .build(new ResourceLocation(CozyCompanions.MODID, "amaro").toString()));

    public static final RegistryObject<EntityType<JellyBatEntity>> JellyBat =
            ENTITY_TYPES.register("jellybat",
                    () -> EntityType.Builder.of(JellyBatEntity::new, MobCategory.CREATURE)
            .sized(0.8f, 0.8f)
            .build(new ResourceLocation(CozyCompanions.MODID, "jellybat").toString()));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
