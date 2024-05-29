package com.Taco.CozyCompanions;

import com.Taco.CozyCompanions.config.CozyClientConfig;
import com.Taco.CozyCompanions.config.CozyServerConfig;
import com.Taco.CozyCompanions.entity.ModEntityTypes;
import com.Taco.CozyCompanions.entity.client.AmaroRenderer;
import com.Taco.CozyCompanions.entity.client.JellyBatRenderer;
import com.Taco.CozyCompanions.item.ModItems;
import com.Taco.CozyCompanions.networking.ModPackets;
import com.Taco.CozyCompanions.sound.SoundRegistry;
import com.Taco.CozyCompanions.world.BiomeModifierRegistry;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import software.bernie.geckolib3.GeckoLib;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CozyCompanions.MODID)
public class CozyCompanions
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "cozycompanions";
    // Directly reference a slf4j logger
    public static final Logger MAIN_LOGGER = LogUtils.getLogger();

    public CozyCompanions()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the Deferred Register to the mod event bus so blocks get registered
        // Register the Deferred Register to the mod event bus so items get registered
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        ModItems.register(modEventBus);
        ModEntityTypes.register(modEventBus);
        SoundRegistry.register(modEventBus);
        BiomeModifierRegistry.register(modEventBus);
        GeckoLib.initialize();

        //Config
        CozyServerConfig.createConfig(CozyServerConfig.BUILDER);
        CozyClientConfig.createConfig(CozyClientConfig.BUILDER);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, CozyServerConfig.COZY_COMPANIONS_CONFIG,
                "cozy_companions_config.toml");

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CozyClientConfig.COZY_CLIENT_CONFIG,
                "cozy_companions_client_config.toml");

        MinecraftForge.EVENT_BUS.register(this);

    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModPackets.register();
        });
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        MAIN_LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            MAIN_LOGGER.info("HELLO FROM CLIENT SETUP");
            MAIN_LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());

            EntityRenderers.register(ModEntityTypes.Amaro.get(), AmaroRenderer::new);
            EntityRenderers.register(ModEntityTypes.JellyBat.get(), JellyBatRenderer::new);
        }
    }
}
