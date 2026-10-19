package com.taco.bafflebeasts.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;

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

    /**
     * resolvePotion
     * @param tag Itemtag related to a potion.
     * @param pLivingEntity LivingEntity that will have effects applied too.
     * @return
     */
    protected Potion resolvePotion(CompoundTag tag, LivingEntity pLivingEntity) {
        Potion p = null;
        // If the tag is empty, return a null potion. Null potions will be skipped when applying effects.
        if (tag == null) {
            return p;
        // Check if there's a "CustomPotionEffectTag", or a regular "Potion" tag.
        } else {
            if (tag.contains("CustomPotionEffects")) {
                List<MobEffectInstance> effects = PotionUtils.getCustomEffects(tag);
                applyCustomPotionEffects(pLivingEntity, effects);
            } else {
                p = PotionUtils.getPotion(tag);
            }
        }

        return p;
    }

    // Called when using the item. Creates potions based off of the NBT_EFFECTS string and SECONDARY_NBT_EFFECTS and applies
    // them to the player when called
    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        // Add the potion effects storeed from the NBT data of the item.

        if (pStack.getTag() != null) {
            Potion p = null;
            Potion p2 = null;
            // Check if the tag has a custom potion effect data, else get the regular potion.
            CompoundTag primaryPotionTag = pStack.getTagElement("Potion");
            CompoundTag secondaryPotionTag = pStack.getTagElement(SECONDARY_NBT_EFFECTS);

            p = resolvePotion(primaryPotionTag, pLivingEntity);
            p2 = resolvePotion(secondaryPotionTag, pLivingEntity);

            // For each effects of the potion, apply the the entity using the item.
            // Check if the effect is instantaneous, and then apply it.
            if (!pLevel.isClientSide()) {
                if (p != null) {
                    applyEffects(pLivingEntity, p);
                }
                if (p2 != null) {
                    applyEffects(pLivingEntity, p2);
                }
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
            if (potion.contains("CustomPotionEffects")) {
                stack.getOrCreateTag().put(NBT_EFFECTS, potion.get("CustomPotionEffects"));
            } else if (potion.contains("Potion")) {
                stack.getOrCreateTag().put(NBT_EFFECTS, potion.get("Potion"));
            }
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
            if (potion.contains("CustomPotionEffects")) {
                stack.getOrCreateTag().put(SECONDARY_NBT_EFFECTS, potion.get("CustomPotionEffects"));
            } else if (potion.contains("Potion")) {
                stack.getOrCreateTag().put(SECONDARY_NBT_EFFECTS, potion.get("Potion"));
            }
        }
    }

    /**
     * applyEffects will take in a livingentity, and a potion, and apply the effects of the potion to the entity passed in.
     * @param pLivingEntity Entity receiving the effects.
     * @param potion The potion passed in to give the effects.
     */
    private void applyEffects(LivingEntity pLivingEntity, Potion potion) {
        ArrayList<MobEffectInstance> effects = new ArrayList<MobEffectInstance>(potion.getEffects());
        for (MobEffectInstance e : effects) {
            pLivingEntity.addEffect(new MobEffectInstance(e));
        }
    }

    /**
     * applyCustomPotionEffects will apply a custom potion effect if the potion has custom potion effects. (multiple effects)
     * @param pLivingEntity Entity receiving the effects.
     * @param pMobEffects List of effects being applied.
     */
    private void applyCustomPotionEffects(LivingEntity pLivingEntity, List<MobEffectInstance> pMobEffects) {
        for (MobEffectInstance e : pMobEffects) {
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
