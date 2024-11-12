package com.taco.bafflebeasts.datagen;

import com.taco.bafflebeasts.BaffleBeasts;
import com.taco.bafflebeasts.sound.CustomSoundEvents;
import com.taco.bafflebeasts.sound.SoundRegistry;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SoundDefinition;
import net.minecraftforge.common.data.SoundDefinitionsProvider;

public class SoundGenerator extends SoundDefinitionsProvider {

    public SoundGenerator(final PackOutput po, final String modId, final ExistingFileHelper helper) {
        super(po, modId, helper);
    }

    @Override
    public void registerSounds() {
        // Amaro Sound Generation
        this.add(CustomSoundEvents.AMARO_IDLE, definition()
                .subtitle("sound.bafflebeasts.amaro_idle")
                .with(sound(SoundRegistry.AMARO_IDLE.getId(), SoundDefinition.SoundType.SOUND))
                .with(sound(BaffleBeasts.MODID + ":amaro/amaro_idle2", SoundDefinition.SoundType.SOUND))
                .with(sound(BaffleBeasts.MODID + ":amaro/amaro_idle3", SoundDefinition.SoundType.SOUND))
                .with(sound(BaffleBeasts.MODID + ":amaro/amaro_idle4", SoundDefinition.SoundType.SOUND)));
        this.add(CustomSoundEvents.AMARO_HURT, definition()
                .subtitle("sound.bafflebeasts.amaro_hurt")
                .with(sound(SoundRegistry.AMARO_HURT.getId(), SoundDefinition.SoundType.SOUND).volume(1.0f)));
        this.add(CustomSoundEvents.AMARO_DEATH, definition()
                .subtitle("sound.bafflebeasts.amaro_death")
                .with(sound(SoundRegistry.AMARO_DEATH.getId(), SoundDefinition.SoundType.SOUND).volume(1.0f)));

        // JellyBat Sound Generation
        this.add(CustomSoundEvents.JELLYBAT_IDLE, definition()
                .subtitle("sound.bafflebeasts.jellybat_idle")
                .with(sound(SoundRegistry.JELLYBAT_IDLE.getId(), SoundDefinition.SoundType.SOUND).volume(0.8f),
                        sound(BaffleBeasts.MODID + ":jellybat/jellybat_idle2", SoundDefinition.SoundType.SOUND).volume(0.8f),
                        sound(BaffleBeasts.MODID + ":jellybat/jellybat_idle3", SoundDefinition.SoundType.SOUND).volume(0.8f)));
        this.add(CustomSoundEvents.JELLYBAT_HURT, definition()
                .subtitle("sound.bafflebeasts.jellybat_hurt1")
                .with(sound(SoundRegistry.JELLYBAT_HURT.getId(), SoundDefinition.SoundType.SOUND)));
        this.add(CustomSoundEvents.JELLYBAT_DEATH, definition()
                .subtitle("sound.bafflebeasts.jellybat_death1")
                .with(sound(SoundRegistry.JELLYBAT_DEATH.getId(), SoundDefinition.SoundType.SOUND)));
        this.add(CustomSoundEvents.JELLYBAT_SUPERSIZE, definition()
                .subtitle("sound.bafflebeasts.jellybat_growth_effect")
                .with(sound(SoundRegistry.JELLYBAT_SUPERSIZE.getId(), SoundDefinition.SoundType.SOUND)));

        //DozeDrake Sound Generation
        this.add(CustomSoundEvents.DOZEDRAKE_IDLE, definition()
                .subtitle("sound.bafflebeasts.dozedrake_idle")
                .with(sound(SoundRegistry.DOZEDRAKE_IDLE.getId(), SoundDefinition.SoundType.SOUND))
                .with(sound(BaffleBeasts.MODID + ":dozedrake/dozedrake_idle2", SoundDefinition.SoundType.SOUND))
                .with(sound(BaffleBeasts.MODID + ":dozedrake/dozedrake_idle3", SoundDefinition.SoundType.SOUND)));
        this.add(CustomSoundEvents.DOZEDRAKE_HURT, definition()
                .subtitle("sound.bafflebeasts.dozedrake_hurt1")
                .with(sound(SoundRegistry.DOZEDRAKE_HURT.getId(), SoundDefinition.SoundType.SOUND)));
        this.add(CustomSoundEvents.DOZEDRAKE_DEATH, definition()
                .subtitle("sound.bafflebeasts.dozedrake_death")
                .with(sound(SoundRegistry.DOZEDRAKE_DEATH.getId(), SoundDefinition.SoundType.SOUND)));
        this.add(CustomSoundEvents.DOZEDRAKE_SNOOZE, definition()
                .subtitle("sound.bafflebeasts.dozedrake_snooze")
                .with(sound(SoundRegistry.DOZEDRAKE_SNOOZE.getId(), SoundDefinition.SoundType.SOUND)));
        this.add(CustomSoundEvents.DOZEDRAKE_HONK_MIMIMI, definition()
                .subtitle("sound.bafflebeasts.dozedrake_honk_mimimi")
                .with(sound(SoundRegistry.DOZEDRAKE_HONK_MIMI.getId(), SoundDefinition.SoundType.SOUND)));
        this.add(CustomSoundEvents.DOZEDRAKE_BUBBLE_SHOOT, definition()
                .subtitle("sound.bafflebeasts.dozedrake_bubble_shoot")
                .with(sound(SoundRegistry.DOZEDRAKE_BUBBLE_SHOOT.getId(), SoundDefinition.SoundType.SOUND)));
        this.add(CustomSoundEvents.DOZEDRAKE_BUBBLE_EXPLODE, definition()
                .subtitle("sound.bafflebeasts.dozedrake_bubble_explode")
                .with(sound(SoundRegistry.DOZEDRAKE_BUBBLE_EXPLODE.getId(), SoundDefinition.SoundType.SOUND)));




    }
}
