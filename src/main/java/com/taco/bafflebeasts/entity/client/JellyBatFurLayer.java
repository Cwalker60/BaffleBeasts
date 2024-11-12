package com.taco.bafflebeasts.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.taco.bafflebeasts.BaffleBeasts;
import com.taco.bafflebeasts.entity.custom.AmaroEntity;
import com.taco.bafflebeasts.entity.custom.JellyBatEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.awt.*;

public class JellyBatFurLayer extends GeoRenderLayer<JellyBatEntity> {
    public JellyBatFurLayer(GeoEntityRenderer<JellyBatEntity> entityRenderIn) {
        super(entityRenderIn);
    }

    private static final ResourceLocation FUR_LAYER_TEXTURE = new ResourceLocation(BaffleBeasts.MODID, "textures/entity/jellybat/jellybat_fur_layer.png");

    @Override
    public void render(PoseStack ps, JellyBatEntity entity, BakedGeoModel bakedModel, RenderType renderType,
                       MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick,
                       int packedLight, int packedOverlay) {
        RenderType rt = RenderType.armorCutoutNoCull(FUR_LAYER_TEXTURE);//RenderType.armorCutoutNoCull(getTextureLocation());

        ps.scale(1.0f, 1.0f, 1.0f);
        ps.translate(0.0d, 0.0d, 0.0d);

        // Get the color of the current donut that the Jellybat has, and apply it to the render() call at the bottom.
        float r = 0; float g = 0; float b = 0;

        if (entity.getDonutColor() != 0) {
            Color color = new Color(entity.getDonutColor());
            r = color.getRed()/255f; g = color.getGreen()/255f; b = color.getBlue()/255f;
        } else {
            r = 1f; g = 1f; b = 1f;
        }

        if (entity.getSuperSize() == true) {
            ps.scale(0.5f,0.5f,0.5f);
        } else {
            ps.scale(1.0f,1.0f,1.0f);
        }

        if (entity.isBaby()) {
            ps.scale(2.6f, 2.6f, 2.6f);
        }

        this.getRenderer().reRender(getDefaultBakedModel(entity),ps,bufferSource,entity,
                rt, bufferSource.getBuffer(rt), partialTick, packedLight, OverlayTexture.NO_OVERLAY,
                r,g,b,1.0f);


    }


}
