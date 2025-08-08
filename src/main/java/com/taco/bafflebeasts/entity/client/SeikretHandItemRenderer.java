package com.taco.bafflebeasts.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.taco.bafflebeasts.entity.custom.SeikretEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3d;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class SeikretHandItemRenderer extends GeoRenderLayer<SeikretEntity> {



    public SeikretHandItemRenderer(GeoEntityRenderer<SeikretEntity> pEntityRender) {
        super(pEntityRender);
    }


    @Override
    public void render(PoseStack ps, SeikretEntity seikret, BakedGeoModel bakedModel, RenderType renderType,
                       MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick,
                       int packedLight, int packedOverlay) {
        ItemStack weapon = seikret.getMainHandItem();

        ps.pushPose();
        Vector3d handTranslation = bakedModel.getBone("left_hand").get().getLocalPosition();
        Vector3d chestTranslation = bakedModel.getBone("chest").get().getLocalPosition();

        // Check if gliding or not to adjust where the item is rendered.
        // Move the item to hand, flip to hold by the handle, and rotate to face where the mob is facing.
        float lerpBodyRot = seikret == null ? 0 : Mth.rotLerp(partialTick, seikret.yBodyRotO, seikret.yBodyRot);

        if (seikret.checkWeapon(seikret.getMainHandItem()).equals("BOW")) {
            if (seikret.gliding) {
                ps.translate(handTranslation.x() * 1.1, handTranslation.y() * 1.05, handTranslation.z() * 1.1);
                ps.mulPose(Axis.XP.rotationDegrees(90));
                ps.mulPose(Axis.ZP.rotationDegrees(lerpBodyRot));
            } else {
                ps.translate(handTranslation.x() * 1.2, handTranslation.y(), handTranslation.z() * 1.2);
                ps.mulPose(Axis.XP.rotationDegrees(90));
                ps.mulPose(Axis.ZP.rotationDegrees(lerpBodyRot));
            }

        } else if (seikret.checkWeapon(seikret.getMainHandItem()).equals("CROSSBOW")) {


            ps.translate(handTranslation.x() * 1.1, handTranslation.y(), handTranslation.z() * 1.1);
            ps.mulPose(Axis.XP.rotationDegrees(180));
            ps.mulPose(Axis.YP.rotationDegrees(lerpBodyRot - 70));

        } else {
            if (seikret.gliding) {
                ps.translate(handTranslation.x() * 1.1, handTranslation.y() * 1.05, handTranslation.z() * 1.1);
                ps.mulPose(Axis.XP.rotationDegrees(180));
                ps.mulPose(Axis.YP.rotationDegrees(lerpBodyRot));

            } else {
                ps.translate(handTranslation.x() * 1.2, handTranslation.y(), handTranslation.z() * 1.2);
                ps.mulPose(Axis.XP.rotationDegrees(180));
                ps.mulPose(Axis.YP.rotationDegrees(lerpBodyRot));
            }
        }



        // Render the item.
        ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
        renderer.renderItem(seikret,weapon, ItemDisplayContext.THIRD_PERSON_LEFT_HAND,false,
                ps,bufferSource,packedLight);

        ps.popPose();

    }

}
