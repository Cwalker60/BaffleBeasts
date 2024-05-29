package com.Taco.CozyCompanions.entity.client;

import com.Taco.CozyCompanions.CozyCompanions;
import com.Taco.CozyCompanions.entity.custom.JellyBatEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

public class JellyBatModel extends AnimatedGeoModel<JellyBatEntity> {
    @Override
    public void setCustomAnimations(JellyBatEntity entity, int uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        head.setRotationX(extraData.headPitch * 0.017453292F);
        head.setRotationY(extraData.netHeadYaw * 0.017453292F);
    }

    @Override
    public ResourceLocation getModelResource(JellyBatEntity object) {
        return new ResourceLocation(CozyCompanions.MODID, "geo/jellybat.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(JellyBatEntity object) {
        return new ResourceLocation(CozyCompanions.MODID, "textures/entity/jellybat/jellybat_mob1.png");
    }

    @Override
    public ResourceLocation getAnimationResource(JellyBatEntity animatable) {
        return new ResourceLocation(CozyCompanions.MODID, "animations/jellybat.animation.json");
    }
}
