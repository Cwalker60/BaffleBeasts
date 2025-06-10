package com.taco.bafflebeasts.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.taco.bafflebeasts.BaffleBeasts;
import com.taco.bafflebeasts.entity.custom.WrymistEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.awt.Color;

public class WrymistTailLayer extends GeoRenderLayer<WrymistEntity> {

    private static final ResourceLocation TAIL_LAYER_TEXTURES = new ResourceLocation(BaffleBeasts.MODID, "textures/entity/wrymist/paint_brush_layer.png");

    public WrymistTailLayer(GeoEntityRenderer<WrymistEntity> entityRenderIn) {
        super(entityRenderIn);
    }

    @Override
    public void render(PoseStack ps, WrymistEntity entity, BakedGeoModel bakedModel, RenderType renderType,
                       MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick,
                       int packedLight, int packedOverlay) {

        RenderType rt = RenderType.armorCutoutNoCull(TAIL_LAYER_TEXTURES);
        ps.scale(1.0f, 1.0f, 1.0f);
        ps.translate(0.0d, 0.0d, 0.0d);

        // Take the Color from the current tail by the textureDiffuseColors
        float r, g, b;
        float[] color = DyeColor.byId(entity.getTailDye()).getTextureDiffuseColors();
        r = color[0];
        g = color[1];
        b = color[2];


        this.getRenderer().reRender(getDefaultBakedModel(entity),ps,bufferSource,entity,
                rt, bufferSource.getBuffer(rt), partialTick, packedLight, OverlayTexture.NO_OVERLAY,
                r,g,b,1.0f);


    }
}
