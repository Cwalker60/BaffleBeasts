package com.Taco.CozyCompanions.entity.client;

import com.Taco.CozyCompanions.CozyCompanions;
import com.Taco.CozyCompanions.entity.custom.JellyBatEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;



public class JellyBatRenderer extends GeoEntityRenderer<JellyBatEntity> {
    public JellyBatRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new JellyBatModel());
        addLayer(new JellyBatFurLayer(this));
        this.shadowRadius = 1.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(JellyBatEntity instance) {
        return new ResourceLocation(CozyCompanions.MODID, "textures/entity/jellybat/jellybat_mob1.png");
    }

    @Override
    public RenderType getRenderType(JellyBatEntity animatable, float partialTicks, PoseStack stack,
                                    MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
                                    ResourceLocation textureLocation) {
        stack.scale(1.0F, 1.0F, 1.0F);
        return super.getRenderType(animatable, partialTicks, stack, renderTypeBuffer, vertexBuilder, packedLightIn, textureLocation);
    }

    @Override
    public void render(GeoModel model, JellyBatEntity entity, float partialTick, RenderType type,
                       PoseStack poseStack, MultiBufferSource bufferSource, VertexConsumer buffer,
                       int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        // If the bat has been sheared, set the chest fur to false;
        if (!entity.hasFur()) {
            model.getBone("chest_fur").get().setHidden(true);
        } else {
            model.getBone("chest_fur").get().setHidden(false);
        }
        // If supersized, make the bat BIG
        if (entity.getSuperSize() == true) {
            poseStack.scale(2.0f,2.0f,2.0f);
        } else {
            poseStack.scale(1.0f,1.0f,1.0f);
        }
        super.render(model, entity, partialTick, type, poseStack, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

}
