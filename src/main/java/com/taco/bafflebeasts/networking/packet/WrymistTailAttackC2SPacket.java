package com.taco.bafflebeasts.networking.packet;

import com.taco.bafflebeasts.entity.custom.WrymistEntity;
import com.taco.bafflebeasts.particle.*;
import com.taco.bafflebeasts.sound.CustomSoundEvents;
import com.taco.bafflebeasts.util.TailColorCheck;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.function.Supplier;

public class WrymistTailAttackC2SPacket {
    private double lookX;
    private double lookY;
    private double lookZ;

    public WrymistTailAttackC2SPacket(double x, double y, double z) {
        this.lookX = x;
        this.lookY = y;
        this.lookZ = z;
    }

    public WrymistTailAttackC2SPacket(FriendlyByteBuf buff) {
        this.lookX = buff.readDouble();
        this.lookY = buff.readDouble();
        this.lookZ = buff.readDouble();
    }

    public void toBytes(FriendlyByteBuf buff) {
        buff.writeDouble(this.lookX);
        buff.writeDouble(this.lookY);
        buff.writeDouble(this.lookZ);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        ServerPlayer player = context.getSender();
        ServerLevel server = context.getSender().serverLevel();

        context.enqueueWork(() -> {
            //Create a hitbox that is the width of the mob * 2 and see if any mob is inside of it.
            WrymistEntity wrymist = getWrymistEntity(player);
            boolean hitMob = false;

            if (wrymist != null) {
                // Move to the hitbox infront of the mob.
                AABB tailHitBox = wrymist.getBoundingBox();
                tailHitBox = tailHitBox.inflate(3.5,1,3.5);

                // Get the mobs within the hitbox
                ArrayList<Entity> targets = new ArrayList<>(server.getEntities(wrymist,tailHitBox));
                for (Entity mobs: targets) {
                    if (mobs instanceof LivingEntity) {
                        // Skip on the player since he won't take damage
                        if (mobs.getId() == player.getId() || (mobs.getId() == wrymist.getId())) {
                            continue;
                        }
                        // Apply damage to each mob
                        hitMob = true;
                        mobs.hurt(mobs.damageSources().mobAttack(wrymist), 10f);
                    }
                }
            }

            // Play a hit sound if a mob is hit!
            if (hitMob) {
                wrymist.getServer().getLevel(wrymist.getCommandSenderWorld().dimension())
                        .playSound(wrymist,wrymist.getOnPos(), CustomSoundEvents.WRYMIST_TAIL_SLAP, SoundSource.HOSTILE, 1.0f, 1.0f);
            }

            wrymist.triggerAnim("attack", "TailAttack");

            float yRotRadians = (float)(wrymist.getYRot() * (Math.PI / 180));
            ColorParticleOptionsBase particle = new ColorParticleOptionsBase(wrymist.getTailDye(),15.0f, yRotRadians);
            server.sendParticles(particle,wrymist.getX(),wrymist.getY() + 1,wrymist.getZ(),
                    1, 0,0,0,0);
            wrymist.setCanTailAttack(false);

            // Paint Blocks CHeck
            BlockHitResult rayCheck = getPlayerPOVHitResult(server,player,ClipContext.Fluid.SOURCE_ONLY);
            if (rayCheck.getType().equals(HitResult.Type.BLOCK)) {
                TailColorCheck.setPaintedSquare(rayCheck.getBlockPos(), rayCheck.getDirection(), server, wrymist.getTailDye());
            }


        });

    }

    // Taken from Item.java
    protected BlockHitResult getPlayerPOVHitResult(Level pLevel, Player pPlayer, ClipContext.Fluid pFluidMode) {
        float f = pPlayer.getXRot();
        float f1 = pPlayer.getYRot();
        Vec3 vec3 = pPlayer.getEyePosition();
        float f2 = Mth.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = Mth.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -Mth.cos(-f * ((float)Math.PI / 180F));
        float f5 = Mth.sin(-f * ((float)Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = pPlayer.getBlockReach() + 2.0d; // Adding 2 extra blocks of reach
        Vec3 vec31 = vec3.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);
        return pLevel.clip(new ClipContext(vec3, vec31, ClipContext.Block.OUTLINE, pFluidMode, pPlayer));
    }

    private WrymistEntity getWrymistEntity(ServerPlayer player) {
        WrymistEntity wrymist = null;
        if (player.getVehicle() instanceof WrymistEntity) {
            wrymist = (WrymistEntity)player.getVehicle();

            if (wrymist != null) {
                return wrymist;
            }

        }
        return wrymist;
    }



}
