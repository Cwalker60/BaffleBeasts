package com.Taco.BaffleBeasts.entity.client;

import com.Taco.BaffleBeasts.BaffleBeasts;
import com.Taco.BaffleBeasts.entity.custom.JellyBatEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
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
        return new ResourceLocation(BaffleBeasts.MODID, "textures/entity/jellybat/jellybat_mob1.png");
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
            model.getBone("head").get().setScale(1.6f, 1.6f, 1.6f);
        }

        if (entity.isElytraFlying()) {
            float f1 = (float)entity.getFallFlyingTicks() + partialTick;
            float f2 = Mth.clamp(f1 * f1 / 100.0F, 0.0F, 1.0F);
            if (!entity.isAutoSpinAttack()) {
                poseStack.mulPose(Vector3f.XP.rotationDegrees(f2 * (0 - entity.getXRot())));
            }

            Vec3 vec3 = entity.getViewVector(partialTick);
            Vec3 vec31 = entity.getDeltaMovement();
            double d0 = vec31.horizontalDistanceSqr();
            double d1 = vec3.horizontalDistanceSqr();
            if (d0 > 0.0D && d1 > 0.0D) {
                double d2 = (vec31.x * vec3.x + vec31.z * vec3.z) / Math.sqrt(d0 * d1);
                double d3 = vec31.x * vec3.z - vec31.z * vec3.x;
                poseStack.mulPose(Vector3f.ZP.rotation((float)(-Math.signum(d3) * Math.acos(d2))));
            }
        }

        super.render(model, entity, partialTick, type, poseStack, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

}
