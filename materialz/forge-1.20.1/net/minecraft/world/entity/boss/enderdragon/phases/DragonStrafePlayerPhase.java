package net.minecraft.world.entity.boss.enderdragon.phases;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class DragonStrafePlayerPhase extends AbstractDragonPhaseInstance {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int FIREBALL_CHARGE_AMOUNT = 5;
   private int fireballCharge;
   @Nullable
   private Path currentPath;
   @Nullable
   private Vec3 targetLocation;
   @Nullable
   private LivingEntity attackTarget;
   private boolean holdingPatternClockwise;

   public DragonStrafePlayerPhase(EnderDragon p_31357_) {
      super(p_31357_);
   }

   public void doServerTick() {
      if (this.attackTarget == null) {
         LOGGER.warn("Skipping player strafe phase because no player was found");
         this.dragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
      } else {
         if (this.currentPath != null && this.currentPath.isDone()) {
            double $$0 = this.attackTarget.getX();
            double $$1 = this.attackTarget.getZ();
            double $$2 = $$0 - this.dragon.getX();
            double $$3 = $$1 - this.dragon.getZ();
            double $$4 = Math.sqrt($$2 * $$2 + $$3 * $$3);
            double $$5 = Math.min((double)0.4F + $$4 / (double)80.0F - (double)1.0F, (double)10.0F);
            this.targetLocation = new Vec3($$0, this.attackTarget.getY() + $$5, $$1);
         }

         double $$6 = this.targetLocation == null ? (double)0.0F : this.targetLocation.distanceToSqr(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
         if ($$6 < (double)100.0F || $$6 > (double)22500.0F) {
            this.findNewTarget();
         }

         double $$7 = (double)64.0F;
         if (this.attackTarget.distanceToSqr(this.dragon) < (double)4096.0F) {
            if (this.dragon.hasLineOfSight(this.attackTarget)) {
               ++this.fireballCharge;
               Vec3 $$8 = (new Vec3(this.attackTarget.getX() - this.dragon.getX(), (double)0.0F, this.attackTarget.getZ() - this.dragon.getZ())).normalize();
               Vec3 $$9 = (new Vec3((double)Mth.sin(this.dragon.getYRot() * ((float)Math.PI / 180F)), (double)0.0F, (double)(-Mth.cos(this.dragon.getYRot() * ((float)Math.PI / 180F))))).normalize();
               float $$10 = (float)$$9.dot($$8);
               float $$11 = (float)(Math.acos((double)$$10) * (double)(180F / (float)Math.PI));
               $$11 += 0.5F;
               if (this.fireballCharge >= 5 && $$11 >= 0.0F && $$11 < 10.0F) {
                  double $$12 = (double)1.0F;
                  Vec3 $$13 = this.dragon.getViewVector(1.0F);
                  double $$14 = this.dragon.head.getX() - $$13.x * (double)1.0F;
                  double $$15 = this.dragon.head.getY((double)0.5F) + (double)0.5F;
                  double $$16 = this.dragon.head.getZ() - $$13.z * (double)1.0F;
                  double $$17 = this.attackTarget.getX() - $$14;
                  double $$18 = this.attackTarget.getY((double)0.5F) - $$15;
                  double $$19 = this.attackTarget.getZ() - $$16;
                  if (!this.dragon.isSilent()) {
                     this.dragon.level().levelEvent((Player)null, 1017, this.dragon.blockPosition(), 0);
                  }

                  DragonFireball $$20 = new DragonFireball(this.dragon.level(), this.dragon, $$17, $$18, $$19);
                  $$20.moveTo($$14, $$15, $$16, 0.0F, 0.0F);
                  this.dragon.level().addFreshEntity($$20);
                  this.fireballCharge = 0;
                  if (this.currentPath != null) {
                     while(!this.currentPath.isDone()) {
                        this.currentPath.advance();
                     }
                  }

                  this.dragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
               }
            } else if (this.fireballCharge > 0) {
               --this.fireballCharge;
            }
         } else if (this.fireballCharge > 0) {
            --this.fireballCharge;
         }

      }
   }

   private void findNewTarget() {
      if (this.currentPath == null || this.currentPath.isDone()) {
         int $$0 = this.dragon.findClosestNode();
         int $$1 = $$0;
         if (this.dragon.getRandom().nextInt(8) == 0) {
            this.holdingPatternClockwise = !this.holdingPatternClockwise;
            $$1 = $$0 + 6;
         }

         if (this.holdingPatternClockwise) {
            ++$$1;
         } else {
            --$$1;
         }

         if (this.dragon.getDragonFight() != null && this.dragon.getDragonFight().getCrystalsAlive() > 0) {
            $$1 %= 12;
            if ($$1 < 0) {
               $$1 += 12;
            }
         } else {
            $$1 -= 12;
            $$1 &= 7;
            $$1 += 12;
         }

         this.currentPath = this.dragon.findPath($$0, $$1, (Node)null);
         if (this.currentPath != null) {
            this.currentPath.advance();
         }
      }

      this.navigateToNextPathNode();
   }

   private void navigateToNextPathNode() {
      if (this.currentPath != null && !this.currentPath.isDone()) {
         Vec3i $$0 = this.currentPath.getNextNodePos();
         this.currentPath.advance();
         double $$1 = (double)$$0.getX();
         double $$2 = (double)$$0.getZ();

         double $$3;
         do {
            $$3 = (double)((float)$$0.getY() + this.dragon.getRandom().nextFloat() * 20.0F);
         } while($$3 < (double)$$0.getY());

         this.targetLocation = new Vec3($$1, $$3, $$2);
      }

   }

   public void begin() {
      this.fireballCharge = 0;
      this.targetLocation = null;
      this.currentPath = null;
      this.attackTarget = null;
   }

   public void setTarget(LivingEntity p_31359_) {
      this.attackTarget = p_31359_;
      int $$1 = this.dragon.findClosestNode();
      int $$2 = this.dragon.findClosestNode(this.attackTarget.getX(), this.attackTarget.getY(), this.attackTarget.getZ());
      int $$3 = this.attackTarget.getBlockX();
      int $$4 = this.attackTarget.getBlockZ();
      double $$5 = (double)$$3 - this.dragon.getX();
      double $$6 = (double)$$4 - this.dragon.getZ();
      double $$7 = Math.sqrt($$5 * $$5 + $$6 * $$6);
      double $$8 = Math.min((double)0.4F + $$7 / (double)80.0F - (double)1.0F, (double)10.0F);
      int $$9 = Mth.floor(this.attackTarget.getY() + $$8);
      Node $$10 = new Node($$3, $$9, $$4);
      this.currentPath = this.dragon.findPath($$1, $$2, $$10);
      if (this.currentPath != null) {
         this.currentPath.advance();
         this.navigateToNextPathNode();
      }

   }

   @Nullable
   public Vec3 getFlyTargetLocation() {
      return this.targetLocation;
   }

   public EnderDragonPhase<DragonStrafePlayerPhase> getPhase() {
      return EnderDragonPhase.STRAFE_PLAYER;
   }
}
