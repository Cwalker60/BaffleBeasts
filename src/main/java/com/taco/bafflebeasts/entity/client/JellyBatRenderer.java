package com.taco.bafflebeasts.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.taco.bafflebeasts.BaffleBeasts;
import com.taco.bafflebeasts.entity.custom.JellyBatEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class JellyBatRenderer extends GeoEntityRenderer<JellyBatEntity> {

    public JellyBatRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new JellyBatModel());
        addRenderLayer(new JellyBatFurLayer(this));
        this.shadowRadius = 1.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(JellyBatEntity instance) {
        return new ResourceLocation(BaffleBeasts.MODID, "textures/entity/jellybat/jellybat_mob1.png");
    }



    @Override
    public void render(JellyBatEntity entity, float pEntityYaw, float partialTick, PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight) {
        if (entity.isElytraFlying()) {
            float pXRot = entity.getXRot() % 360;
            float pYRot = entity.getYRot();

            if (pYRot < 0) {
                pYRot += 360;
            } else {
                pYRot = pYRot % 360;
            }

            if (!entity.isAutoSpinAttack()) {
                poseStack.mulPose(Axis.XP.rotationDegrees((float)Math.cos( ((pYRot * Math.PI) / 180)) * pXRot));
                poseStack.mulPose(Axis.ZP.rotationDegrees((float)Math.sin( ((pYRot * Math.PI) / 180)) * pXRot));
            }

            Vec3 vec3 = entity.getViewVector(partialTick);
            Vec3 vec31 = entity.getDeltaMovement();
            double d0 = vec31.horizontalDistanceSqr();
            double d1 = vec3.horizontalDistanceSqr();
            if (d0 > 0.0D && d1 > 0.0D) {
                double d2 = (vec31.x * vec3.x + vec31.z * vec3.z) / Math.sqrt(d0 * d1);
                double d3 = vec31.x * vec3.z - vec31.z * vec3.x;
                float d4 = (float)(Math.signum(d3) * Math.acos(d2)); // angle to change tilt in positive/negative
                poseStack.mulPose(Axis.ZP.rotation((float)Math.cos( ((pYRot * Math.PI) / 180)) * d4));
                poseStack.mulPose(Axis.XP.rotation((float)Math.sin( ((pYRot * Math.PI) / 180)) * -d4));
            }
        }

        super.render(entity,pEntityYaw,partialTick,poseStack,pBuffer,pPackedLight);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, JellyBatEntity entity, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource,
                               VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {

        // If the bat has been sheared, set the chest fur to false;
        if (!entity.hasFur()) {
            model.getBone("chest_fur").get().setHidden(true);
        } else {
            model.getBone("chest_fur").get().setHidden(false);
        }

        if (!entity.isSaddled()) {
            model.getBone("saddle").get().setHidden(true);
        } else {
            model.getBone("saddle").get().setHidden(false);
        }

        // If supersized, make the bat BIG
        if (entity.getSuperSize() == true) {
            poseStack.scale(2.0f,2.0f,2.0f);
        } else {
            poseStack.scale(1.0f,1.0f,1.0f);
        }

        if (entity.isBaby()) {
            poseStack.scale(0.4f, 0.4f, 0.4f);
            model.getBone("head").get().updateScale(1.6f, 1.6f, 1.6f);
        }


        super.actuallyRender(poseStack,  entity,  model,  renderType,  bufferSource, buffer,  isReRender,  partialTick,  packedLight,  packedOverlay, red,  green,  blue,  alpha);
    }

}
