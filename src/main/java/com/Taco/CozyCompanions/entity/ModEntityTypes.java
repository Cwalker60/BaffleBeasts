package com.Taco.CozyCompanions.entity;

import com.Taco.CozyCompanions.CozyCompanions;
import com.Taco.CozyCompanions.entity.custom.AmaroEntity;
import com.Taco.CozyCompanions.entity.custom.FlightFireworkEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.Projectile;
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
// TODO - FIX AMARO FIREWORK ENTITY
//    public static final RegistryObject<EntityType<FlightFireworkEntity>> FlightFirework =
//            ENTITY_TYPES.register("flightfirework",
//                    () -> EntityType.Builder.of(FlightFireworkEntity::new, MobCategory.MISC)
//                    .sized(0.25f, 0.25f)
//                    .build(new ResourceLocation(ResourceLocation.DEFAULT_NAMESPACE, "firework_rocket").toString()));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
