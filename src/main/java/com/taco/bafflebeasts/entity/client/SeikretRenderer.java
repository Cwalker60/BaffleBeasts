package com.taco.bafflebeasts.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.taco.bafflebeasts.BaffleBeasts;
import com.taco.bafflebeasts.entity.custom.SeikretEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class SeikretRenderer extends GeoEntityRenderer<SeikretEntity> {

    public SeikretRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SeikretModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
        addRenderLayer(new SeikretHandItemRenderer(this));
        this.shadowRadius = 1.5f;
    }

    @Override
    public ResourceLocation getTextureLocation(SeikretEntity instance) {

        switch (instance.getVariant()) {
            case 1 : return new ResourceLocation(BaffleBeasts.MODID,"textures/entity/seikret/seikret_1.png");
            case 2 : return new ResourceLocation(BaffleBeasts.MODID,"textures/entity/seikret/seikret_2.png");
            case 3 : return new ResourceLocation(BaffleBeasts.MODID,"textures/entity/seikret/seikret_3.png");
            case 4 : return new ResourceLocation(BaffleBeasts.MODID,"textures/entity/seikret/seikret_4.png");
            case 5 : return new ResourceLocation(BaffleBeasts.MODID,"textures/entity/seikret/seikret_5.png");
            case 6 : return new ResourceLocation(BaffleBeasts.MODID,"textures/entity/seikret/seikret_6.png");
        }

        return new ResourceLocation(BaffleBeasts.MODID,"textures/entity/seikret_1.png");
    }


    @Override
    public void preRender(PoseStack poseStack, SeikretEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue,
                          float alpha) {

        this.entityRenderTranslations = new Matrix4f(poseStack.last().pose());
        if (animatable.isBaby()) {
            this.scaleHeight = 0.4f;
            this.scaleWidth = 0.4f;

        } else {
            this.scaleHeight = 0.9f;
            this.scaleWidth = 0.9f;
        }

        scaleModelForRender(this.scaleWidth, this.scaleHeight, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, SeikretEntity entity, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (!entity.isSaddled()) {
            getGeoModel().getBone("saddle").get().setHidden(true);
        } else {
            getGeoModel().getBone("saddle").get().setHidden(false);
        }

        super.actuallyRender(poseStack, entity,  model,  renderType,  bufferSource,  buffer,  isReRender,  partialTick,  packedLight,  packedOverlay,  red,  green,  blue,  alpha);
    }


}
