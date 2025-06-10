package com.taco.bafflebeasts.particle;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;

public class ColorParticleType extends ParticleType<ColorParticleOptionsBase> {
    public ColorParticleType() {
        super(false, ColorParticleOptionsBase.DESERIALIZER);
    }

    @Override
    public Codec<ColorParticleOptionsBase> codec() {
        return ColorParticleOptionsBase.CODEC;
    }
}
