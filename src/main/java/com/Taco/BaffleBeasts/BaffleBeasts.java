package com.taco.bafflebeasts;

import com.mojang.logging.LogUtils;
import com.taco.bafflebeasts.config.BaffleClientConfig;
import com.taco.bafflebeasts.config.BaffleConfigValues;
import com.taco.bafflebeasts.config.BaffleServerConfig;
import com.taco.bafflebeasts.entity.ModEntityTypes;
import com.taco.bafflebeasts.entity.client.AmaroRenderer;
import com.taco.bafflebeasts.entity.client.DozeDrakeRenderer;
import com.taco.bafflebeasts.entity.client.JellyBatRenderer;
import com.taco.bafflebeasts.item.ModItems;
import com.taco.bafflebeasts.networking.ModPackets;
import com.taco.bafflebeasts.recipes.ModPotionRecipes;
import com.taco.bafflebeasts.sound.SoundRegistry;
import com.taco.bafflebeasts.world.BiomeModifierRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Blocks;
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
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;

import javax.swing.text.html.parser.Entity;

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
        MAIN_LOGGER.info("HELLO FROM COMMON SETUP");
        event.enqueueWork(() -> {
            ModPackets.register();
            BrewingRecipeRegistry.addRecipe(new ModPotionRecipes(Potions.STRONG_HEALING, Items.GOLDEN_CARROT,
                    ModItems.SUPER_SHAKE.get()));
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
            EntityRenderers.register(ModEntityTypes.DozeDrake.get(), DozeDrakeRenderer::new);
        }
    }
}
