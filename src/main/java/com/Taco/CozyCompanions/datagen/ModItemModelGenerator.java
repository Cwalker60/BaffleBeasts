package com.Taco.CozyCompanions.datagen;

import com.Taco.CozyCompanions.CozyCompanions;
import com.Taco.CozyCompanions.item.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemModelGenerator extends ItemModelProvider {

    public ModItemModelGenerator(DataGenerator generator, ExistingFileHelper fileHelper)  {
        super(generator, CozyCompanions.MODID, fileHelper);
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
