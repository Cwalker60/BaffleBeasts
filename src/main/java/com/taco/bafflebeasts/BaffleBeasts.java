package com.taco.bafflebeasts;

import com.mojang.logging.LogUtils;
import com.taco.bafflebeasts.config.BaffleClientConfig;
import com.taco.bafflebeasts.config.BaffleServerConfig;
import com.taco.bafflebeasts.entity.ModEntityTypes;
import com.taco.bafflebeasts.entity.client.AmaroRenderer;
import com.taco.bafflebeasts.entity.client.BubbleProjectileRenderer;
import com.taco.bafflebeasts.entity.client.DozeDrakeRenderer;
import com.taco.bafflebeasts.entity.client.JellyBatRenderer;
import com.taco.bafflebeasts.item.ModItems;
import com.taco.bafflebeasts.networking.ModPackets;
import com.taco.bafflebeasts.recipes.ModPotionRecipes;
import com.taco.bafflebeasts.sound.SoundRegistry;
import com.taco.bafflebeasts.world.BiomeModifierRegistry;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(BaffleBeasts.MODID)
public class BaffleBeasts
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "bafflebeasts";
    public static final Logger MAIN_LOGGER = LogUtils.getLogger();

    public BaffleBeasts()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        ModItems.register(modEventBus);
        SoundRegistry.register(modEventBus);
        ModEntityTypes.register(modEventBus);
        BiomeModifierRegistry.register(modEventBus);
        GeckoLib.initialize();

        BaffleServerConfig.createConfig(BaffleServerConfig.BUILDER);
        BaffleClientConfig.createConfig(BaffleClientConfig.BUILDER);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, BaffleServerConfig.BAFFLE_COMPANIONS_CONFIG,
                "baffle_beasts_config.toml");

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, BaffleClientConfig.BAFFLE_CLIENT_CONFIG,
                "baffle_client_config.toml");



        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);


    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        event.enqueueWork(() -> {
            ModPackets.register();
        });
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            MAIN_LOGGER.info("BaffleBeasts mobs on your client will now be visible even in 144p!");

            EntityRenderers.register(ModEntityTypes.Amaro.get(), AmaroRenderer::new);
            EntityRenderers.register(ModEntityTypes.JellyBat.get(), JellyBatRenderer::new);
            EntityRenderers.register(ModEntityTypes.DozeDrake.get(), DozeDrakeRenderer::new);

            EntityRenderers.register(ModEntityTypes.BubbleProjectile.get(), BubbleProjectileRenderer::new);
        }
    }
}
