package com.taco.bafflebeasts.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Locale;

/**
 * ColorParticleOptionsBase is the data class particles that want a color and rotation.
 */
public class ColorParticleOptionsBase implements ParticleOptions {
    public final int color;
    public final float scale;
    public final float yRot;

    /**
     * @param pColor    An int from 0-16 to get the DyeColor from DyeColor.java
     * @param pScale    A float to represent the size of the particle.
     * @param pYRot     A rotation value to rotate the particle from the yAxis (yaw)
     */
    public ColorParticleOptionsBase(int pColor, float pScale, float pYRot) {
        this.color = pColor;
        this.scale = pScale;
        this.yRot = pYRot;
    }


    @Override
    public ParticleType<ColorParticleOptionsBase> getType() {
        return ModParticles.WRYMIST_SLASH.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf pBuffer) {
        // Write the color to the buffer
        pBuffer.writeInt(color);
        pBuffer.writeFloat(scale);
        pBuffer.writeFloat(yRot);
    }

    // Taken from DustParticleOptionsBase
    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", this.getType(), this.color, this.scale, this.yRot);
    }

    public int getColor() {
        return this.color;
    }

    public float getScale() {
        return this.scale;
    }

    public float getYRot() {
        return this.yRot;
    }

    public static final Codec<ColorParticleOptionsBase> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("color").forGetter(d -> d.getColor()),
            Codec.FLOAT.fieldOf("scale").forGetter(d -> d.getScale()),
            Codec.FLOAT.fieldOf("yRot").forGetter(d -> d.getYRot())

    ).apply(instance, ColorParticleOptionsBase::new));

    public static final ParticleOptions.Deserializer<ColorParticleOptionsBase> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Override
        public ColorParticleOptionsBase fromCommand(ParticleType<ColorParticleOptionsBase> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            return new ColorParticleOptionsBase(reader.readInt(), reader.readFloat(), reader.readFloat());
        }
        @Override
        public ColorParticleOptionsBase fromNetwork(ParticleType<ColorParticleOptionsBase> type, FriendlyByteBuf pBuffer) {
            return new ColorParticleOptionsBase(pBuffer.readInt(), pBuffer.readFloat(), pBuffer.readFloat());
        }
    };

}
