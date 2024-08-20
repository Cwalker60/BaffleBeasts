package com.taco.bafflebeasts.event;

import com.taco.bafflebeasts.BaffleBeasts;
import com.taco.bafflebeasts.entity.ModEntityTypes;
import com.taco.bafflebeasts.entity.custom.AmaroEntity;
import com.taco.bafflebeasts.entity.custom.DozeDrakeEntity;
import com.taco.bafflebeasts.entity.custom.JellyBatEntity;
import com.taco.bafflebeasts.entity.custom.RideableFlightEntity;
import com.taco.bafflebeasts.item.ModItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BaffleBeasts.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {

    @SubscribeEvent
    public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.Amaro.get(), AmaroEntity.setAttributes());
        event.put(ModEntityTypes.JellyBat.get(), JellyBatEntity.setAttributes());
        event.put(ModEntityTypes.DozeDrake.get(), DozeDrakeEntity.setAttributes());
    }

    @SubscribeEvent
    public static void createItemtabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(ModItems.AMARO_SPAWN_EGG);
            event.accept(ModItems.JELLYBAT_SPAWN_EGG);
            event.accept(ModItems.DOZEDRAKE_SPAWN_EGG);
        }

        if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(ModItems.JELLYBAT_DONUT);
            event.accept(ModItems.SUPER_SHAKE);
        }

    }

    @Mod.EventBusSubscriber(modid = BaffleBeasts.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public class ForgeBus {
        @SubscribeEvent
        public static void entityMountEvent(EntityMountEvent event) {
            if (event.getEntityBeingMounted() != null && event.getEntityMounting() != null) {
                Entity rider = event.getEntityMounting();
                Entity mount = event.getEntityBeingMounted();
                if (event.isMounting()) {
                    if (rider instanceof Player p && mount instanceof RideableFlightEntity a) {
                        a.setNoGravity(true);
                    }
                } else {
                    if (rider instanceof Player p && mount instanceof RideableFlightEntity a) {
                        a.setNoGravity(false);
                        a.setElytraFlying(false);
                    }
                }

            }

        }

        @SubscribeEvent
        public static void entitySpawnRestriction(SpawnPlacementRegisterEvent event) {
            event.register(ModEntityTypes.Amaro.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    Animal::checkAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.OR);
            event.register(ModEntityTypes.JellyBat.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    JellyBatEntity::jellyBatSpawnRules, SpawnPlacementRegisterEvent.Operation.OR);
        }

    }


}