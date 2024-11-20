package com.taco.bafflebeasts.item;

import com.taco.bafflebeasts.BaffleBeasts;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class JellyDonutItem extends Item {
    private static final String NBT_EFFECTS = "Potion";
    private static final String DONUT_COLOR = "DonutColor";
    private static final String SECONDARY_NBT_EFFECTS = "SecondaryPotion";

    private int color;

    public JellyDonutItem(Properties pProperties) {
        super(pProperties);
    }
    public JellyDonutItem(Properties pProperties, int c) {
        super(pProperties);
        color = c;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        // Add the potion effects storeed from the NBT data of the item.
        if (pStack.getTag() != null) {
            Potion p = ForgeRegistries.POTIONS.getValue(new ResourceLocation(pStack.getTag().getString(NBT_EFFECTS)));
            Potion p2 = ForgeRegistries.POTIONS.getValue(new ResourceLocation(pStack.getTag().getString(SECONDARY_NBT_EFFECTS)));
            // For each effects of the potion, apply the the entity using the item.
            // Check if the effect is instantaneous, and then apply it.

            p.getEffects().iterator().forEachRemaining(effects -> {
                pLivingEntity.addEffect(effects);
            });
            // Secondary
            p2.getEffects().iterator().forEachRemaining(effects -> {
                pLivingEntity.addEffect(effects);
            });
        }
        return this.isEdible() ? pLivingEntity.eat(pLevel, pStack) : pStack;
    }

    public static int getDonutColor(ItemStack stack) {
        return stack.getOrCreateTag().getInt(DONUT_COLOR);
    }

    public static void setDonutColor(ItemStack stack, int c) {
        stack.getOrCreateTag().putInt(DONUT_COLOR, c);
    }

    public static void addEffects(ItemStack stack, Potion potion) {
        String potionNameSpace = "";

        if (ForgeRegistries.POTIONS.containsValue(potion)) {
            potionNameSpace = ForgeRegistries.POTIONS.getKey(potion).getPath();
        }

        stack.getOrCreateTag().putString(NBT_EFFECTS, potionNameSpace);
    }

    public static void addSecondaryEffects(ItemStack stack, Potion potion) {
        String potionNameSpace = "";

        if (ForgeRegistries.POTIONS.containsValue(potion)) {
            potionNameSpace = ForgeRegistries.POTIONS.getKey(potion).getPath();
        }

        stack.getOrCreateTag().putString(SECONDARY_NBT_EFFECTS, potionNameSpace);
    }

    public static void addEffects(ItemStack stack, List<Potion> potionsIn) {
        potionsIn.iterator().forEachRemaining(potions -> {
            String potionNameSpace = "";
            if (ForgeRegistries.POTIONS.containsValue(potions)) {
                potionNameSpace = ForgeRegistries.POTIONS.getKey(potions).getPath();

                BaffleBeasts.MAIN_LOGGER.debug("Checking Input Potion of : " + potions.getName("") + " to " +
                        ForgeRegistries.POTIONS.getKey(potions));
            }

            CompoundTag t = new CompoundTag();
            t.putString(NBT_EFFECTS, potionNameSpace);
            stack.setTag(t);
        });

    }



    public static int getColor(ItemStack pStack, int pTintIndex) {
        if (pTintIndex == 0) {
            return getDonutColor(pStack);
        }
        return 0xFFFFFF;
    }

}
