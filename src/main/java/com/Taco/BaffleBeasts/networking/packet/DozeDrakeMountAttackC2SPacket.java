package com.taco.bafflebeasts.networking.packet;

import com.taco.bafflebeasts.BaffleBeasts;
import com.taco.bafflebeasts.entity.ModEntityTypes;
import com.taco.bafflebeasts.entity.custom.BubbleProjectile;
import com.taco.bafflebeasts.entity.custom.DozeDrakeEntity;
import com.taco.bafflebeasts.sound.CustomSoundEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class DozeDrakeMountAttackC2SPacket {
    private Vec3 pLook;
    private double xLook;
    private double yLook;
    private double zLook;

    public DozeDrakeMountAttackC2SPacket() {

    }

    public DozeDrakeMountAttackC2SPacket(double x, double y, double z) {
        this.xLook = x;
        this.yLook = y;
        this.zLook = z;
    }

    public DozeDrakeMountAttackC2SPacket(FriendlyByteBuf buf) {
        this.xLook = buf.readDouble();
        this.yLook = buf.readDouble();
        this.zLook = buf.readDouble();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeDouble(this.xLook);
        buf.writeDouble(this.yLook);
        buf.writeDouble(this.zLook);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        ServerPlayer player = context.getSender();
        ServerLevel server = context.getSender().serverLevel();

        context.enqueueWork(() -> {
            Vec3 clientPlayerLook = new Vec3(this.xLook,this.yLook,this.zLook);
            clientPlayerLook = clientPlayerLook.normalize();
            clientPlayerLook = clientPlayerLook.multiply(20,20,20);
            // Check if the client player look is looking at a mob to send a homing bubble towards.
            BaffleBeasts.MAIN_LOGGER.debug("Packet recieved for DozeDrakeMountAttack key");
            BaffleBeasts.MAIN_LOGGER.debug("lookVector is : (" + clientPlayerLook.x + ", " + clientPlayerLook.y + ", " + clientPlayerLook.z + ")");

            // rayCastCheck will create a AABB box from the clientPlayerLook to a certain range of clientPlayerLook, and then check if there's a mob in it.
            AABB rayCastCheck = AABB.ofSize(clientPlayerLook, clientPlayerLook.x, clientPlayerLook.y * 3, clientPlayerLook.z);
            rayCastCheck = rayCastCheck.move(player.getOnPos());

            BaffleBeasts.MAIN_LOGGER.debug("rayCastCheck AABB is : " + rayCastCheck.toString());
            BaffleBeasts.MAIN_LOGGER.debug("rayCastCheck AABB is : " + rayCastCheck.toString());



            ArrayList<Entity> entities = new ArrayList<>(server.getEntities(player,rayCastCheck));
            BaffleBeasts.MAIN_LOGGER.debug("Entities found in box are...");
            for (Entity e : entities) {
                BaffleBeasts.MAIN_LOGGER.debug(e.getName().getString());
            }

            DozeDrakeEntity shooter = this.getDozeDrake(player);
            // If the entity list is not null, create a homing bubble targeting the first entity in the list.
            if (!entities.isEmpty()) {
                if (shooter != null) {
//                    if (entities.get(0) instanceof LivingEntity) {
//                        LivingEntity target = (LivingEntity) entities.get(0);
//                        double d0 = shooter.distanceToSqr(entities.get(0));
//                        double xdist = entities.get(0).getX() - shooter.getX();
//                        double d2 = entities.get(0).getY(0.5D) - shooter.getY(0.5D);
//                        double d3 = entities.get(0).getZ() - shooter.getZ();
//                        double d4 = Math.sqrt(Math.sqrt(d0)) * 0.5D;
//
//                        BaffleBeasts.MAIN_LOGGER.debug("Firing Bubble at target : " + target.getName().getString());
//
//                        server.addFreshEntity(new BubbleProjectile(ModEntityTypes.BubbleProjectile.get(), shooter,
//                                target,shooter.getRandom().triangle(xdist, d4),d2,
//                                shooter.getRandom().triangle(d3, d4),shooter.level()));
//                    }
                    for (Entity target : entities) {
                        if (target instanceof LivingEntity) {
                            // If the mob is the shooter itself, skip
                            if (target.getId() == shooter.getId()) {
                                continue;
                            }

                            double d0 = shooter.distanceToSqr(target);
                            double xdist = target.getX() - shooter.getX();
                            double d2 = target.getY(0.5D) - shooter.getY(0.5D);
                            double d3 = target.getZ() - shooter.getZ();
                            double d4 = Math.sqrt(Math.sqrt(d0)) * 0.5D;

                            BaffleBeasts.MAIN_LOGGER.debug("Firing Bubble at target : " + target.getName().getString());

                            server.addFreshEntity(new BubbleProjectile(ModEntityTypes.BubbleProjectile.get(), shooter,
                                    (LivingEntity)target,shooter.getRandom().triangle(xdist, d4),d2,
                                    shooter.getRandom().triangle(d3, d4),shooter.level()));

                            shooter.setBubbleBlast(false);

                            shooter.getServer().getLevel(shooter.getCommandSenderWorld().dimension())
                                    .playSound(shooter,shooter.blockPosition(), CustomSoundEvents.DOZEDRAKE_BUBBLE_SHOOT
                                            ,shooter.getSoundSource(), 1.0f, 1.0f);
                            break;

                        }
                    }

                }
            // If there are no entities found, fire a bubble that will fly slowly with no homing target.
            } else {
               BaffleBeasts.MAIN_LOGGER.debug("Target not found, firing bubble : ");
            }



        });

        return true;
    }

    // Assume if this packet is called, it passed the check to be riding a dozedrake.
    public DozeDrakeEntity getDozeDrake(ServerPlayer player) {
        DozeDrakeEntity dozeDrake = (DozeDrakeEntity) player.getVehicle();

        if (dozeDrake != null) {
            return dozeDrake;
        }

        return null;
    }
}
