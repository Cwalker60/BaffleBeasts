package com.Taco.BaffleBeasts.entity.client;

import com.Taco.BaffleBeasts.BaffleBeasts;
import com.Taco.BaffleBeasts.entity.custom.AmaroEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

public class AmaroModel extends AnimatedGeoModel<AmaroEntity> {
    @Override
    public void setCustomAnimations(AmaroEntity entity, int uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        head.setRotationX(extraData.headPitch * 0.017453292F);
        head.setRotationY(extraData.netHeadYaw * 0.017453292F);
    }

    @Override
    public ResourceLocation getModelResource(AmaroEntity object) {
        return new ResourceLocation(BaffleBeasts.MODID, "geo/amaro.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(AmaroEntity object) {
        return new ResourceLocation(BaffleBeasts.MODID, "textures/entity/amaro/amaro_mount1.png");
    }

    @Override
    public ResourceLocation getAnimationResource(AmaroEntity animatable) {
        return new ResourceLocation(BaffleBeasts.MODID, "animations/amaro.animation.json");
    }

}
