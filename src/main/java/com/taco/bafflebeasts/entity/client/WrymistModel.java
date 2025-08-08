package com.taco.bafflebeasts.entity.client;

import com.taco.bafflebeasts.BaffleBeasts;
import com.taco.bafflebeasts.entity.custom.WrymistEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import javax.annotation.Nullable;

public class WrymistModel extends GeoModel<WrymistEntity> {

    @Override
    public void setCustomAnimations(WrymistEntity entity, long uniqueID, @Nullable AnimationState<WrymistEntity> customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
        CoreGeoBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData as;
        if (customPredicate.getData(DataTickets.ENTITY_MODEL_DATA) != null) {
            as = customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(as.headPitch() * 0.017453292F);
            head.setRotY(as.netHeadYaw() * 0.017453292F);
        }

    }

    @Override
    public ResourceLocation getModelResource(WrymistEntity animatable) {
        return new ResourceLocation(BaffleBeasts.MODID, "geo/wrymist.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WrymistEntity animatable) {
        return new ResourceLocation(BaffleBeasts.MODID, "textures/entity/wrymist/wrymist.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WrymistEntity animatable) {
        return new ResourceLocation(BaffleBeasts.MODID, "animations/wrymist.animation.json");
    }
}
