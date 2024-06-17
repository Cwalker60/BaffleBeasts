package com.Taco.BaffleBeasts.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {

    @Inject(method = "render", at = @At("HEAD"))
    protected void injectRenderMethod(AbstractClientPlayer pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight,
                                              CallbackInfo info) {
        pMatrixStack.mulPose(Vector3f.XP.rotationDegrees(pEntity.getViewXRot(pPartialTicks)));
        pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(pEntity.getViewYRot(pPartialTicks)));

    }
}
