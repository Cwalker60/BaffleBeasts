package com.taco.bafflebeasts.entity.custom;

import com.taco.bafflebeasts.sound.CustomSoundEvents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;


public class BubbleProjectile extends AbstractHurtingProjectile {

    private final LivingEntity target;
    private final LivingEntity shooter;

    public BubbleProjectile(EntityType<? extends BubbleProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        target = null;
        shooter = null;
    }

    public BubbleProjectile(EntityType<? extends BubbleProjectile> pEntityType, double pX, double pY, double pZ, double pOffsetX, double pOffsetY, double pOffsetZ, Level pLevel) {
        super(pEntityType, pX, pY, pZ, pOffsetX, pOffsetY, pOffsetZ, pLevel);
        target = null;
        shooter = null;
    }

    /**
     * This projectile is a slow moving bubble that will slowly home towards players. Upon contact the player
     * will take damage and explode.
     * @param pEntityType
     * @param pShooter
     * @param pOffsetX
     * @param pOffsetY
     * @param pOffsetZ
     * @param pLevel
     */
    public BubbleProjectile(EntityType<? extends BubbleProjectile> pEntityType, LivingEntity pShooter, LivingEntity homingTarget, double pOffsetX, double pOffsetY, double pOffsetZ, Level pLevel) {
        super(pEntityType, pShooter,  pOffsetX, pOffsetY, pOffsetZ, pLevel);
        shooter = pShooter;
        target = homingTarget;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!this.level().isClientSide) {
            Entity target = result.getEntity();
            Entity shooter = this.getOwner();

            if (!target.hurt(this.damageSources().explosion(this, shooter), 10.0F)) {
            } else {
                Vec3 targetDelta = target.getDeltaMovement();
                targetDelta = targetDelta.add(0.0, 1.0, 0.0);
                target.setDeltaMovement(targetDelta);
                this.level().playSound(this,target.blockPosition(), CustomSoundEvents.DOZEDRAKE_BUBBLE_EXPLODE,
                        SoundSource.AMBIENT, 1.0f, 1.0f);

                this.getServer().getLevel(this.getCommandSenderWorld().dimension()).sendParticles(ParticleTypes.EXPLOSION, target.getX(),
                        target.getY() + 1,target.getZ(),1,0,1,0,0.5);

            }


        }

    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            this.discard();

        }
    }

    @Override
    public void tick() {
        super.tick();
        // Move bubble towards target if available
        // Slow the bubble down as it goes.
        if (this.target != null) {
            double xDistance = (target.getX() - this.getX());
            double yDistance = ((target.getY() + target.getBbHeight()/2) - this.getY());
            double zDistance = (target.getZ() - this.getZ());
            Vec3 travelVector = new Vec3(xDistance, yDistance, zDistance);
            travelVector = travelVector.normalize().multiply(0.25, 0.25, 0.25);
            Vec3 normalVector = travelVector.lerp(this.getDeltaMovement(), 0.40);



            this.setDeltaMovement(normalVector);
        }

    }

    @Override
    protected boolean shouldBurn() {return false;}

//    @Override
//    protected ParticleOptions getTrailParticle() {return ParticleTypes.EFFECT;}

}
