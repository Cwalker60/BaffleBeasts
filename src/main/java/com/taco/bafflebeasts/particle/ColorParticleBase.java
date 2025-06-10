package com.taco.bafflebeasts.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;

public class ColorParticleBase<T extends ColorParticleOptionsBase> extends TextureSheetParticle {
    protected final SpriteSet sprites;

    // Base class for Particles that can be dyed from the data passed from colorParticleOptionsBase.
    // Based off of DustParticles
    protected ColorParticleBase(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, T pOptions, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        this.sprites = pSprites;
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }





}
