package com.Taco.BaffleBeasts.recipes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.common.brewing.IBrewingRecipe;

public class ModPotionRecipes implements IBrewingRecipe {

    private final Potion input;
    private final Item ingredient;
    private final Item output;

    /**
     * A class to help make custom brewing recipes.
     * @param input is the base potion to use.
     * @param ingredient is the ingredient at the top of the brewing stand
     * @param output is the output of the brewing recipe
     */
    public ModPotionRecipes(Potion input, Item ingredient, Item output) {
        this.input = input;
        this.ingredient = ingredient;
        this.output = output;
    }

    @Override
    public boolean isInput(ItemStack input) {
        return (PotionUtils.getPotion(input) == this.input);
    }

    @Override
    public boolean isIngredient(ItemStack ingredient) {
        return (ingredient.getItem() == this.ingredient);
    }

    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
        if (!this.isInput(input) || !this.isIngredient(ingredient)) {
            return ItemStack.EMPTY;
        }

        ItemStack itemStack = new ItemStack(output);
        itemStack.setTag(new CompoundTag());

        return itemStack;
    }
}
