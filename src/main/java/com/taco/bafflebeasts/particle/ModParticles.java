package com.taco.bafflebeasts.particle;

import com.taco.bafflebeasts.BaffleBeasts;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModParticles {
    public static DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, BaffleBeasts.MODID);

    public static final RegistryObject<ParticleType<ColorParticleOptionsBase>> WRYMIST_SLASH  =
            PARTICLES.register("wrymist_slash", ColorParticleType::new);



    @SubscribeEvent
    public static void register(IEventBus eventBus) {
        PARTICLES.register(eventBus);
    }

}
