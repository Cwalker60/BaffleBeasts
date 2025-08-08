package com.taco.bafflebeasts.entity.goal;

import com.taco.bafflebeasts.entity.custom.SeikretEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;

public class SeikretRangedAttackGoal extends Goal {
    private final SeikretEntity seikret;
    private final double speedModifier;
    private final float attackRadiusSqr;
    private int attackInterval;
    private int attackTime = -1;
    private int seeTime;
    private SeikretRangedAttackGoal.CrossbowState crossbowState = CrossbowState.UNCHARGED;

    public SeikretRangedAttackGoal(SeikretEntity entity, double pSpeedModifier, int pAttackIntervalMin, float pAttackRadius) {
        this.seikret = entity;
        this.speedModifier = pSpeedModifier;
        this.attackRadiusSqr = pAttackRadius;
        this.attackInterval = pAttackIntervalMin;;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return this.seikret.isAlive() && this.seikret.canShootWeapon() && this.isHoldingRangedWeapon();
    }

    public boolean canContinueToUse() {
        return (this.canUse());
    }

    public void setMinAttackInterval(int pAttackCooldown) {
        this.attackInterval = pAttackCooldown;
    }

    protected boolean isHoldingRangedWeapon() {
        if (this.seikret.checkWeapon(this.seikret.getMainHandItem()).equals("BOW") ||
                this.seikret.checkWeapon(this.seikret.getMainHandItem()).equals("CROSSBOW")) {
            return true;
        }

        return false;
    }

    public void start() {
        super.start();
        this.seikret.setAggressive(true);
    }

    public void stop() {
        super.stop();
        this.seikret.setAggressive(false);
        this.attackTime = -1;
        this.seikret.stopUsingItem();
        this.seeTime = 0;
        this.crossbowState = CrossbowState.UNCHARGED;
        this.seikret.getMoveControl().strafe(0f,0f);
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void tick() {
        LivingEntity target = this.seikret.getTarget();
        String weapon = this.seikret.checkWeapon(this.seikret.getMainHandItem());
        if (target != null) {
            // Get distance and check if it has line of sight.
            double distanceToTarget = this.seikret.distanceToSqr(target.getX(), target.getY(), target.getZ());
            boolean hasSight = this.seikret.getSensing().hasLineOfSight(target);


            // If there is sight, increase seeTime, else decrease it.
            if (hasSight) {
                seeTime++;
                this.seikret.lookAt(target, 30, 30);
            } else {
                seeTime--;
            }

            // If the target is too close, strafe away.
            if (distanceToTarget < this.attackRadiusSqr * 0.5D && seeTime > -20) {
                this.seikret.getMoveControl().strafe(-0.75f,0.9f);
                this.seikret.lookAt(target,15,15);
            // If out of range and has seen the mob for a while, move towards.
            } else if (distanceToTarget > this.attackRadiusSqr && seeTime > -20) {
                this.seikret.getNavigation().moveTo(target, speedModifier);
                this.seikret.lookAt(target,15,15);
            // Else stop the navigation.
            } else {
                this.seikret.getMoveControl().strafe(0.0f,0.0f);
                this.seikret.getNavigation().stop();
            }

            // Fire the projectile if the mob has seen the mob for a certain amount of time.
            if (weapon.equals("BOW")) {
                useBow(hasSight, target);
            } else if (weapon.equals("CROSSBOW")) {
                useCrossbow(hasSight, target);
            }

        }
    }

    public void useBow(boolean hasSight, LivingEntity target) {
        if (this.seikret.isUsingItem()) {
            if (!hasSight && this.seeTime < -20) {
                this.seikret.stopUsingItem();
            } else if (hasSight) {
                int itemTicks = this.seikret.getTicksUsingItem();
                if (itemTicks > 40) {
                    this.seikret.stopUsingItem();
                    this.seikret.performRangedAttack(target, BowItem.getPowerForTime(itemTicks));
                    this.attackTime = this.attackInterval;
                }
            }
        } else if (--this.attackTime <= 0 && this.seeTime >= 40) {
            this.seikret.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.seikret, item -> item instanceof  BowItem));
        }
    }

    public void useCrossbow(boolean hasSight, LivingEntity target) {
        // If crossbow is not loaded and has line of sight, prepare to load crossbow.

        if (this.crossbowState == CrossbowState.UNCHARGED) {
            if (hasSight && seeTime > -20) {
                this.seikret.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.seikret, item -> item instanceof CrossbowItem));
                this.crossbowState = CrossbowState.CHARGING;
            }
        // If the crossbow is charging, check if it gets interrupted and stop, or complete it after the attack delay
        } else if (this.crossbowState == CrossbowState.CHARGING) {
            if (!this.seikret.isUsingItem() || seeTime < -20) {
                this.crossbowState = CrossbowState.UNCHARGED;
            }

            int usingTicks = this.seikret.getTicksUsingItem();
            ItemStack crossbow = this.seikret.getUseItem();
            if (usingTicks >= CrossbowItem.getChargeDuration(crossbow)) {
                this.seikret.releaseUsingItem();;
                this.crossbowState = CrossbowState.CHARGED;
                this.attackTime = 20 + this.seikret.getRandom().nextInt(20);
            }
        // If charged and the attack delay is ready, set ready to attack and prepare to fire.
        } else if (this.crossbowState == CrossbowState.CHARGED) {
            --this.attackTime;
            if (this.attackTime <= 0) {
                this.crossbowState = CrossbowState.READY_TO_ATTACK;
            }
        }
        // Fire the crossbow
        else if (this.crossbowState == CrossbowState.READY_TO_ATTACK && hasSight) {
            this.seikret.performRangedAttack(target, 1.0f);
            ItemStack itemstack1 = this.seikret.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this.seikret, item -> item instanceof CrossbowItem));
            CrossbowItem.setCharged(itemstack1, false);
            this.crossbowState = CrossbowState.UNCHARGED;

        }
    }


    static enum CrossbowState {
        UNCHARGED,
        CHARGING,
        CHARGED,
        READY_TO_ATTACK;
    }

}
