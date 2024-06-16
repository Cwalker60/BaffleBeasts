package com.Taco.CozyCompanions.sound;

import com.Taco.CozyCompanions.CozyCompanions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundRegistry {

    public static final DeferredRegister<SoundEvent> SOUND_REG =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CozyCompanions.MODID);

    // Amaro sound effects
    public static final RegistryObject<SoundEvent> AMARO_IDLE = createSoundEvent("amaro/amaro_idle");
    public static final RegistryObject<SoundEvent> AMARO_HURT = createSoundEvent("amaro/amaro_hurt");
    // Jellybat Sound effects
    public static final RegistryObject<SoundEvent> JELLYBAT_IDLE = createSoundEvent("jellybat/jellybat_idle1");
    public static final RegistryObject<SoundEvent> JELLYBAT_HURT = createSoundEvent("jellybat/jellybat_hurt1");
    public static final RegistryObject<SoundEvent> JELLYBAT_DEATH = createSoundEvent("jellybat/jellybat_death");
    public static final RegistryObject<SoundEvent> JELLYBAT_SUPERSIZE = createSoundEvent("jellybat/jellybat_growth_effect");


    private static RegistryObject<SoundEvent> createSoundEvent(final String soundName) {
        return SOUND_REG.register(soundName, () -> new SoundEvent(new ResourceLocation(CozyCompanions.MODID, soundName)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_REG.register(eventBus);
    }

}
