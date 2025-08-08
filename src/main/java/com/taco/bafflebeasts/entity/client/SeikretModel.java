package com.taco.bafflebeasts.entity.client;

import com.taco.bafflebeasts.BaffleBeasts;
import com.taco.bafflebeasts.entity.custom.SeikretEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import javax.annotation.Nullable;

public class SeikretModel extends GeoModel<SeikretEntity> {

    @Override
    public void setCustomAnimations(SeikretEntity entity, long uniqueID, @Nullable AnimationState<SeikretEntity> customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
        CoreGeoBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData as;
        if (customPredicate.getData(DataTickets.ENTITY_MODEL_DATA) != null) {
            as = customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
            // If there is no head tilt or turning, don't rotate to avoid messing up the sleep animation
            if (entity.isAsleep()) {

            } else {
                head.setRotX(as.headPitch() * 0.017453292F);
                head.setRotY(as.netHeadYaw() * 0.017453292F);
            }
        }

    }

    @Override
    public ResourceLocation getModelResource(SeikretEntity animatable) {
        return new ResourceLocation(BaffleBeasts.MODID,"geo/seikret.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SeikretEntity animatable) {
        return new ResourceLocation(BaffleBeasts.MODID,"textures/entity/seikret/seikret_1.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SeikretEntity animatable) {
        return new ResourceLocation(BaffleBeasts.MODID, "animations/seikret.animation.json");
    }
}
