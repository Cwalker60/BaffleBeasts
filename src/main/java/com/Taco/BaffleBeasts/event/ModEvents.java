package com.Taco.BaffleBeasts.event;

import com.Taco.BaffleBeasts.BaffleBeasts;
import com.Taco.BaffleBeasts.entity.ModEntityTypes;
import com.Taco.BaffleBeasts.entity.custom.AmaroEntity;
import com.Taco.BaffleBeasts.entity.custom.JellyBatEntity;
import com.Taco.BaffleBeasts.entity.custom.RideableFlightEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap;
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
    }

    @SubscribeEvent
    public static void entitySpawnRestriction(SpawnPlacementRegisterEvent event) {
        event.register(ModEntityTypes.Amaro.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.OR);
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
    }


}

