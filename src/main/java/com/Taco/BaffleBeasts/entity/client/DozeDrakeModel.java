package com.taco.bafflebeasts.entity.client;

import com.taco.bafflebeasts.BaffleBeasts;
import com.taco.bafflebeasts.entity.custom.DozeDrakeEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import javax.annotation.Nullable;

public class DozeDrakeModel extends GeoModel<DozeDrakeEntity> {

    @Override
    public void setCustomAnimations(DozeDrakeEntity entity, long uniqueID, @Nullable AnimationState<DozeDrakeEntity> customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
        CoreGeoBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData as;
        if (customPredicate.getData(DataTickets.ENTITY_MODEL_DATA) != null) {
            if (!entity.isAsleep()) {
                as = customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
                head.setRotX(as.headPitch() * 0.017453292F);
                head.setRotY(as.netHeadYaw() * 0.017453292F);
            }
        }

    }

    @Override
    public ResourceLocation getModelResource(DozeDrakeEntity object) {
        return new ResourceLocation(BaffleBeasts.MODID, "geo/dozedrake.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(DozeDrakeEntity object) {
        return new ResourceLocation(BaffleBeasts.MODID, "textures/entity/dozedrake/dozedrake_1.png");
    }

    @Override
    public ResourceLocation getAnimationResource(DozeDrakeEntity animatable) {
        return new ResourceLocation(BaffleBeasts.MODID, "animations/doze_drake.animation.json");
    }
}
