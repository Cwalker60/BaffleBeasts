package com.Taco.CozyCompanions.entity.client;

import com.Taco.CozyCompanions.CozyCompanions;
import com.Taco.CozyCompanions.entity.custom.AmaroEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class AmaroRenderer extends GeoEntityRenderer<AmaroEntity> {
    public AmaroRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new AmaroModel());
        this.shadowRadius = 1.0F;
    }
    @Override
    public ResourceLocation getTextureLocation(AmaroEntity instance) {
        return new ResourceLocation(CozyCompanions.MODID, "textures/entity/amaro/amaro_mount1.png");
    }
    @Override
    public RenderType getRenderType(AmaroEntity animatable, float partialTicks, PoseStack stack,
                                    MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
                                    ResourceLocation textureLocation) {
        stack.scale(1.0F, 1.0F, 1.0F);

        return super.getRenderType(animatable, partialTicks, stack, renderTypeBuffer, vertexBuilder, packedLightIn, textureLocation);
    }
}