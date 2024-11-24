package com.taco.bafflebeasts.entity.goal;

import com.taco.bafflebeasts.entity.ModEntityTypes;
import com.taco.bafflebeasts.entity.custom.BubbleProjectile;
import com.taco.bafflebeasts.entity.custom.DozeDrakeEntity;
import com.taco.bafflebeasts.sound.CustomSoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;

public class DozeDrakeBubbleAttackGoal extends Goal {
    private final DozeDrakeEntity dozedrake;
    private int lastSeen;
    private int chargeTime;

    public DozeDrakeBubbleAttackGoal(DozeDrakeEntity drake) {
        this.dozedrake = drake;
    }

    @Override
    public boolean canUse() {
        LivingEntity livingentity = this.dozedrake.getTarget();
        return livingentity != null && livingentity.isAlive() && this.dozedrake.canAttack(livingentity)
                && this.dozedrake.distanceToSqr(livingentity) > 100d && this.dozedrake.isBubbleBlasting();
    }

    public void start() {
        this.dozedrake.triggerAnim("attack", "bubble_blast");

        chargeTime = 0;
    }

    public void stop() {
        this.lastSeen = 0;
        this.chargeTime = 0;
        this.dozedrake.setBubbleBlast(false);
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void tick() {
        LivingEntity targetEntity = this.dozedrake.getTarget();
        this.dozedrake.getLookControl().setLookAt(targetEntity);
        // Check if the entity has LOS, increase chargetime if so.
        // Once charge time hits 120, fire a bubble projectile and end the goal.
        // If there is no LineOfSight during goal, start ticking lastSeen.
        // If lastSeen reaches a threshhold, stop the goal.
        if (this.dozedrake.getSensing().hasLineOfSight(targetEntity) && this.dozedrake.distanceToSqr(targetEntity) > 100d) {
            chargeTime++;
            this.dozedrake.getNavigation().stop();
        } else {
            lastSeen++;
        }

        if (lastSeen >= 30) {
            this.stop();
        }

        // Fire projectile
        if (chargeTime >= 60) {
            double d0 = this.dozedrake.distanceToSqr(targetEntity);
            double xdist = targetEntity.getX() - this.dozedrake.getX();
            double d2 = targetEntity.getY(0.5D) - this.dozedrake.getY(0.5D);
            double d3 = targetEntity.getZ() - this.dozedrake.getZ();
            double d4 = Math.sqrt(Math.sqrt(d0)) * 0.5D;

            BubbleProjectile bubble = new BubbleProjectile(ModEntityTypes.BubbleProjectile.get(), dozedrake, targetEntity, this.dozedrake.getRandom().triangle(xdist, d4), d2, this.dozedrake.getRandom().triangle(d3, d4), this.dozedrake.level());
            bubble.setPos(bubble.getX(), this.dozedrake.getY(0.5D) + 0.5D, bubble.getZ());
            this.dozedrake.level().addFreshEntity(bubble);

            this.dozedrake.getServer().getLevel(this.dozedrake.getCommandSenderWorld().dimension())
                    .playSound(this.dozedrake,this.dozedrake.blockPosition(), CustomSoundEvents.DOZEDRAKE_BUBBLE_SHOOT
                            ,this.dozedrake.getSoundSource(), 1.0f, 1.0f);

            this.stop();
        }


            super.tick();
    }

    private double getFollowDistance() {
        return this.dozedrake.getAttributeValue(Attributes.FOLLOW_RANGE);
    }

}
