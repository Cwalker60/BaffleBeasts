package com.taco.bafflebeasts.particle;


import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Consumer;

public class WrymistSlashParticle extends ColorParticleBase<ColorParticleOptionsBase> {

    // Code taken from ShriekParticle

    private static final Vector3f ROTATION_VECTOR = (new Vector3f(0.5F, 0.5F, 0.5F)).normalize();
    private static final Vector3f TRANSFORM_VECTOR = new Vector3f(-1.0F, -1.0F, 0.0F);

    private final float yRot;


    public WrymistSlashParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, ColorParticleOptionsBase pOptions, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, pOptions, pSprites);
        this.lifetime = 22;
        this.quadSize = 0.4F;
        this.xd = 0.0D;
        this.yd = 0.0D;
        this.zd = 0.0D;
        this.yRot = pOptions.getYRot();
        this.setParticleColors(pOptions.getColor());
        this.scale(pOptions.getScale());
        this.hasPhysics = false;
    }

    public void setParticleColors(int color) {
        float[] colors = DyeColor.byId(color).getTextureDiffuseColors();
        this.setColor(colors[0], colors[1], colors[2]);
    }

    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
        this.renderRotatedParticle(pBuffer,pRenderInfo,pPartialTicks,
                (rotateP -> {
                    rotateP.mul((new Quaternionf()).rotationYXZ(-this.yRot, 1.5708f, 0.0F)); // 90 degrees in radians
                }));
    }

    private void renderRotatedParticle(VertexConsumer pConsumer, Camera pRenderInfo, float pPartialTicks, Consumer<Quaternionf> pQuaternion) {
        Vec3 camVec = pRenderInfo.getPosition();
        float xDist = (float)(Mth.lerp((double)pPartialTicks, this.xo, this.x) - camVec.x());
        float yDist = (float)(Mth.lerp((double)pPartialTicks, this.yo, this.y) - camVec.y());
        float zDist = (float)(Mth.lerp((double)pPartialTicks, this.zo, this.z) - camVec.z());
        Quaternionf quaternionf = new Quaternionf().setAngleAxis(0.0f,ROTATION_VECTOR.x(), ROTATION_VECTOR.y(),
                ROTATION_VECTOR.z());
        pQuaternion.accept(quaternionf);
        quaternionf.transform(TRANSFORM_VECTOR);
        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float quadSize = this.getQuadSize(pPartialTicks);

        for(int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.rotate(quaternionf);
            vector3f.mul(quadSize);
            vector3f.add(xDist, yDist, zDist);
        }

        int j = this.getLightColor(pPartialTicks);
        this.makeCornerVertex(pConsumer, avector3f[0], this.getU1(), this.getV1(), j);
        this.makeCornerVertex(pConsumer, avector3f[1], this.getU1(), this.getV0(), j);
        this.makeCornerVertex(pConsumer, avector3f[2], this.getU0(), this.getV0(), j);
        this.makeCornerVertex(pConsumer, avector3f[3], this.getU0(), this.getV1(), j);

    }

    private void makeCornerVertex(VertexConsumer pConsumer, Vector3f pVertex, float pU, float pV, int pPackedLight) {
        pConsumer.vertex((double)pVertex.x(), (double)pVertex.y(), (double)pVertex.z()).uv(pU, pV).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(pPackedLight).endVertex();
    }



    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class Provider implements ParticleProvider<ColorParticleOptionsBase> {
        private final SpriteSet sprite;

        public Provider(SpriteSet pSprites) {this.sprite = pSprites;}

        @Nullable
        public Particle createParticle(ColorParticleOptionsBase pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            WrymistSlashParticle slashParticle = new WrymistSlashParticle(pLevel,pX,pY, pZ,
                    pXSpeed,pYSpeed,pZSpeed, pType, this.sprite);
            slashParticle.pickSprite(this.sprite);
            slashParticle.setSpriteFromAge(this.sprite);
//            slashParticle.hasPhysics = false;
//            slashParticle.setParticleSpeed(0,0,0);
//            slashParticle.setSize(1.0F, 1.0F);
//            slashParticle.xd = 0.0D;
//            slashParticle.yd = 0.0D;
//            slashParticle.zd = 0.0D;
            return slashParticle;
        }

    }

}
