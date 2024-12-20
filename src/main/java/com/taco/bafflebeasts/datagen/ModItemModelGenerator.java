package com.taco.bafflebeasts.datagen;

import com.taco.bafflebeasts.BaffleBeasts;
import com.taco.bafflebeasts.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemModelGenerator extends ItemModelProvider {

    public ModItemModelGenerator(PackOutput po, ExistingFileHelper fileHelper)  {
        super(po, BaffleBeasts.MODID, fileHelper);
    }

    public  void registerModels() {
        basicLayerItem(ModItems.JELLYBAT_DONUT.getId());
        basicItem(ModItems.SUPER_SHAKE.get());
    }

    public ItemModelBuilder basicLayerItem(ResourceLocation item) {
        return getBuilder(item.toString())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", new ResourceLocation(item.getNamespace(), "item/" + item.getPath() + "_layer"));
    }


}
