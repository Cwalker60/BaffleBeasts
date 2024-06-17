package com.Taco.BaffleBeasts.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class BaffleClientConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec BAFFLE_CLIENT_CONFIG;

    public static ForgeConfigSpec.ConfigValue<Integer> FLIGHT_HUD_X_OFFSET;
    public static ForgeConfigSpec.ConfigValue<Integer> FLIGHT_HUD_Y_OFFSET;

    public static void createConfig(ForgeConfigSpec.Builder builder) {
        builder.push("HUD Elements");
        FLIGHT_HUD_X_OFFSET = builder.comment("The following X and Y offset are to move the feathers in the flight gauge when " +
                "you mount a mob capable of flight.")
                .comment("Positive X will shift it to the right, and positive Y will shift it up")
                .define("Flight_Hud_X_Offset", 0);

        FLIGHT_HUD_Y_OFFSET = builder.define("Flight_Hud_Y_Offset", 0);

        builder.pop();

        BAFFLE_CLIENT_CONFIG = builder.build();
    }
}
