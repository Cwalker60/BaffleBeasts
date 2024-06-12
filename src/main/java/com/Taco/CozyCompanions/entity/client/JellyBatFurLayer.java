package com.Taco.CozyCompanions.entity.client;

import com.Taco.CozyCompanions.CozyCompanions;
import com.Taco.CozyCompanions.entity.custom.JellyBatEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

import java.awt.Color;

public class JellyBatFurLayer extends GeoLayerRenderer<JellyBatEntity> {
    public JellyBatFurLayer(IGeoRenderer<JellyBatEntity> entityRenderIn) {
        super(entityRenderIn);
    }

    private static final ResourceLocation FUR_LAYER_TEXTURE = new ResourceLocation(CozyCompanions.MODID, "textures/entity/jellybat/jellybat_fur_layer.png");
    private static final ResourceLocation FUR_MODEL = new ResourceLocation(CozyCompanions.MODID, "geo/jellybat.geo.json");


    @Override
    public void render(PoseStack ps, MultiBufferSource bufferIn, int packedLightIn, JellyBatEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        RenderType renderType = RenderType.armorCutoutNoCull(FUR_LAYER_TEXTURE);//RenderType.armorCutoutNoCull(getTextureLocation());
        GeoModel model = this.getEntityModel().getModel(FUR_MODEL);
        ps.pushPose();
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


        this.getRenderer().render(model,entity, partialTicks,
        renderType,ps, bufferIn, bufferIn.getBuffer(renderType), packedLightIn,
                OverlayTexture.NO_OVERLAY,r,g,b,1f);

        ps.popPose();
    }

}
