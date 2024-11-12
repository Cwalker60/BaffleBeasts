package com.taco.bafflebeasts.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final String KEY_CATEGORY_COZYCOMPANIONS = "key.category.bafflebeasts.keybinds";
    public static final String KEY_DESCEND = "key.bafflebeasts.descend";
    public static final String KEY_GLIDE = "key.bafflebeasts.glide";
    public static final String KEY_MOUNT_ATTACK = "key.bafflebeasts.mount_attack";

    public static final KeyMapping DESCENDING_KEY = new KeyMapping(KEY_DESCEND, KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, KEY_CATEGORY_COZYCOMPANIONS);

    public static final KeyMapping GLIDE_KEY = new KeyMapping(KEY_GLIDE, KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_X, KEY_CATEGORY_COZYCOMPANIONS);

    public static final KeyMapping MOUNT_ATTACK_KEY = new KeyMapping(KEY_MOUNT_ATTACK, KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, KEY_CATEGORY_COZYCOMPANIONS);
}
