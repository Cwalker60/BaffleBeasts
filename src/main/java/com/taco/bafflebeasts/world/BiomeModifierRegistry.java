package com.taco.bafflebeasts.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.taco.bafflebeasts.BaffleBeasts;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BiomeModifierRegistry {
    public static DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, BaffleBeasts.MODID);

    public static RegistryObject<Codec<ModMobSpawnModifier>> MOD_MOB_SPAWN_CODEX =
            BIOME_MODIFIER_SERIALIZERS.register("add_spawns", () ->
                    RecordCodecBuilder.create(builder -> builder.group(
                            Codec.BOOL.fieldOf("use_configs").forGetter(ModMobSpawnModifier::config))
                            .apply(builder, ModMobSpawnModifier::new)
                    ));

    public static void register(IEventBus eventBus) {
        BIOME_MODIFIER_SERIALIZERS.register(eventBus);
    }
}
