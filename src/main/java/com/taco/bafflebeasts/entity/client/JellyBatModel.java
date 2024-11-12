package com.taco.bafflebeasts.entity.client;

import com.taco.bafflebeasts.BaffleBeasts;
import com.taco.bafflebeasts.entity.custom.JellyBatEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class JellyBatModel extends GeoModel<JellyBatEntity> {

    public ResourceLocation getModelResource(JellyBatEntity object) {
        return new ResourceLocation(BaffleBeasts.MODID, "geo/jellybat.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(JellyBatEntity object) {
        return new ResourceLocation(BaffleBeasts.MODID, "textures/entity/jellybat/jellybat_mob1.png");
    }

    @Override
    public ResourceLocation getAnimationResource(JellyBatEntity animatable) {
        return new ResourceLocation(BaffleBeasts.MODID, "animations/jellybat.animation.json");
    }
}

