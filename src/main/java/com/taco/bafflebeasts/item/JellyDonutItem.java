package com.taco.bafflebeasts.item;

import com.taco.bafflebeasts.BaffleBeasts;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
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

    // Called when using the item. Creates potions based off of the NBT_EFFECTS string and SECONDARY_NBT_EFFECTS and applies
    // them to the player when called
    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        // Add the potion effects storeed from the NBT data of the item.
        if (pStack.getTag() != null) {
            Potion p = PotionUtils.getPotion(pStack.getTag());
            CompoundTag potionTag = new CompoundTag();
            if (pStack.getOrCreateTag().get(SECONDARY_NBT_EFFECTS) != null) {
                potionTag.put("Potion", pStack.getOrCreateTag().get(SECONDARY_NBT_EFFECTS));
            }

            Potion p2 = PotionUtils.getPotion(potionTag);

            // For each effects of the potion, apply the the entity using the item.
            // Check if the effect is instantaneous, and then apply it.
            if (!pLevel.isClientSide()) {
                // Primary Effect
                if (!p.getEffects().isEmpty()) {

                }
                applyEffects(pLivingEntity, p);
                applyEffects(pLivingEntity, p2);
            }

        }
        return this.isEdible() ? pLivingEntity.eat(pLevel, pStack) : pStack;
    }

    public static int getDonutColor(ItemStack stack) {
        return stack.getOrCreateTag().getInt(DONUT_COLOR);
    }

    public static void setDonutColor(ItemStack stack, int c) {
        stack.getOrCreateTag().putInt(DONUT_COLOR, c);
    }

    /**
     * Used to set the potion effect the item will give on creation.
     * @param stack ItemStack of the item being created.
     * @param potion Potion Effect to store in JellyDonutItem's NBT_EFFECTS field.
     */
    public static void addEffects(ItemStack stack, CompoundTag potion) {
        String potionNameSpace = "";

        if (!potion.isEmpty()) {
            stack.getOrCreateTag().put(NBT_EFFECTS, potion.get("Potion"));
        }

    }

    /**
     * Used to set the secondary potion effect the item will give on creation.
     * @param stack ItemStack of the item being created.
     * @param potion Potion Effect to store in JellyDonutItem's SECONDARY_NBT_EFFECTS field.
     */
    public static void addSecondaryEffects(ItemStack stack, CompoundTag potion) {
        String potionNameSpace = "";



        if (!potion.isEmpty()) {
            stack.getOrCreateTag().put(SECONDARY_NBT_EFFECTS, potion.get("Potion"));
        }
    }

    private void applyEffects(LivingEntity pLivingEntity, Potion potion) {
        ArrayList<MobEffectInstance> effects = new ArrayList<MobEffectInstance>(potion.getEffects());
        for (MobEffectInstance e : effects) {
            pLivingEntity.addEffect(new MobEffectInstance(e));
        }
    }

    public static int getColor(ItemStack pStack, int pTintIndex) {
        if (pTintIndex == 0) {
            return getDonutColor(pStack);
        }
        return 0xFFFFFF;
    }

}
