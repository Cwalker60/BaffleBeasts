package com.taco.bafflebeasts.entity;

import com.taco.bafflebeasts.BaffleBeasts;
import com.taco.bafflebeasts.entity.custom.AmaroEntity;
import com.taco.bafflebeasts.entity.custom.BubbleProjectile;
import com.taco.bafflebeasts.entity.custom.DozeDrakeEntity;
import com.taco.bafflebeasts.entity.custom.JellyBatEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, BaffleBeasts.MODID);

    // Mobs
    public static final RegistryObject<EntityType<AmaroEntity>> Amaro =
            ENTITY_TYPES.register("amaro",
                    () -> EntityType.Builder.of(AmaroEntity::new, MobCategory.CREATURE)
                            .sized(1.5f, 3.2f)
                            .build(new ResourceLocation(BaffleBeasts.MODID, "amaro").toString()));

    public static final RegistryObject<EntityType<JellyBatEntity>> JellyBat =
            ENTITY_TYPES.register("jellybat",
                    () -> EntityType.Builder.of(JellyBatEntity::new, MobCategory.AMBIENT)
                            .sized(0.8f, 0.8f)
                            .build(new ResourceLocation(BaffleBeasts.MODID, "jellybat").toString()));

    public static final RegistryObject<EntityType<DozeDrakeEntity>> DozeDrake =
            ENTITY_TYPES.register("dozedrake",
                    () -> EntityType.Builder.of(DozeDrakeEntity::new, MobCategory.CREATURE)
                            .sized(2.0f, 4.0f)
                            .build(new ResourceLocation(BaffleBeasts.MODID, "dozedrake").toString()));

    public static final RegistryObject<EntityType<BubbleProjectile>> BubbleProjectile =
            ENTITY_TYPES.register("bubble_projectile",
                    () -> EntityType.Builder.<BubbleProjectile>of(BubbleProjectile::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .build(new ResourceLocation(BaffleBeasts.MODID, "bubble_projectile").toString()));



    public static void register(IEventBus eventBus) {ENTITY_TYPES.register(eventBus);}
}
