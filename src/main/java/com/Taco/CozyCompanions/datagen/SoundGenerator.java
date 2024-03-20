package com.Taco.CozyCompanions.datagen;

import com.Taco.CozyCompanions.sound.CustomSoundEvents;
import com.Taco.CozyCompanions.sound.SoundRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SoundDefinition;
import net.minecraftforge.common.data.SoundDefinitionsProvider;

public class SoundGenerator extends SoundDefinitionsProvider {

    public SoundGenerator(final DataGenerator generator, final String modId, final ExistingFileHelper helper) {
        super(generator, modId, helper);
    }

    @Override
    public void registerSounds() {
        this.add(CustomSoundEvents.AMARO_IDLE, definition()
                .subtitle("sound.cozycompanions.amaro_idle")
                .with(sound(SoundRegistry.AMARO_IDLE.getId(), SoundDefinition.SoundType.SOUND)));
        this.add(CustomSoundEvents.AMARO_HURT, definition()
                .subtitle("sound.cozycompanions.amaro_hurt")
                .with(sound(SoundRegistry.AMARO_HURT.getId(), SoundDefinition.SoundType.SOUND).volume(2.0f)));

    }
}
