package com.taco.bafflebeasts.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.taco.bafflebeasts.BaffleBeasts;
import com.taco.bafflebeasts.entity.custom.WrymistEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WrymistRenderer extends GeoEntityRenderer<WrymistEntity> {

    public WrymistRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WrymistModel());
        addRenderLayer(new WrymistTailLayer(this));
        this.shadowRadius = 1.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(WrymistEntity instance) {
        return new ResourceLocation(BaffleBeasts.MODID, "textures/entity/wrymist/wrymist.png");
    }

    @Override
    public void preRender(PoseStack poseStack, WrymistEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue,
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
    public void render(WrymistEntity entity, float pEntityYaw, float partialTick, PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight) {
        if (entity.isElytraFlying()) {
            float pXRot = entity.getXRot() % 360;
            float pYRot = entity.getYRot();

            if (pYRot < 0) {
                pYRot += 360;
            } else {
                pYRot = pYRot % 360;
            }

            if (!entity.isAutoSpinAttack()) {
                poseStack.mulPose(Axis.XP.rotationDegrees((float) Math.cos(((pYRot * Math.PI) / 180)) * pXRot));
                poseStack.mulPose(Axis.ZP.rotationDegrees((float) Math.sin(((pYRot * Math.PI) / 180)) * pXRot));
            }

            Vec3 vec3 = entity.getViewVector(partialTick);
            Vec3 vec31 = entity.getDeltaMovement();
            double d0 = vec31.horizontalDistanceSqr();
            double d1 = vec3.horizontalDistanceSqr();
            if (d0 > 0.0D && d1 > 0.0D) {
                double d2 = (vec31.x * vec3.x + vec31.z * vec3.z) / Math.sqrt(d0 * d1);
                double d3 = vec31.x * vec3.z - vec31.z * vec3.x;
                float d4 = (float) (Math.signum(d3) * Math.acos(d2)); // angle to change tilt in positive/negative
                poseStack.mulPose(Axis.ZP.rotation((float) Math.cos(((pYRot * Math.PI) / 180)) * d4));
                poseStack.mulPose(Axis.XP.rotation((float) Math.sin(((pYRot * Math.PI) / 180)) * -d4));
            }
        }
        super.render(entity, pEntityYaw, partialTick, poseStack, pBuffer, pPackedLight);
    }


    @Override
    public void actuallyRender(PoseStack poseStack, WrymistEntity entity, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
                               boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        if (!entity.isSaddled()) {
            getGeoModel().getBone("saddle").get().setHidden(true);
        } else {
            getGeoModel().getBone("saddle").get().setHidden(false);
        }

        super.actuallyRender(poseStack, entity,  model,  renderType,  bufferSource,  buffer,  isReRender,  partialTick,  packedLight,  packedOverlay,  red,  green,  blue,  alpha);
    }
}
