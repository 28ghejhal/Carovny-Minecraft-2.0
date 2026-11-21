package net.minecraft.world.entity.animal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

public class Squid extends WaterAnimal {
   public float xBodyRot;
   public float xBodyRotO;
   public float zBodyRot;
   public float zBodyRotO;
   public float tentacleMovement;
   public float oldTentacleMovement;
   public float tentacleAngle;
   public float oldTentacleAngle;
   private float speed;
   private float tentacleSpeed;
   private float rotateSpeed;
   private float tx;
   private float ty;
   private float tz;

   public Squid(EntityType<? extends Squid> p_29953_, Level p_29954_) {
      super(p_29953_, p_29954_);
      this.random.setSeed((long)this.getId());
      this.tentacleSpeed = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new SquidRandomMovementGoal(this));
      this.goalSelector.addGoal(1, new SquidFleeGoal());
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, (double)10.0F);
   }

   protected float getStandingEyeHeight(Pose p_29975_, EntityDimensions p_29976_) {
      return p_29976_.height * 0.5F;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.SQUID_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_29980_) {
      return SoundEvents.SQUID_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.SQUID_DEATH;
   }

   protected SoundEvent getSquirtSound() {
      return SoundEvents.SQUID_SQUIRT;
   }

   public boolean canBeLeashed(Player p_149052_) {
      return !this.isLeashed();
   }

   protected float getSoundVolume() {
      return 0.4F;
   }

   protected Entity.MovementEmission getMovementEmission() {
      return Entity.MovementEmission.EVENTS;
   }

   public void aiStep() {
      super.aiStep();
      this.xBodyRotO = this.xBodyRot;
      this.zBodyRotO = this.zBodyRot;
      this.oldTentacleMovement = this.tentacleMovement;
      this.oldTentacleAngle = this.tentacleAngle;
      this.tentacleMovement += this.tentacleSpeed;
      if ((double)this.tentacleMovement > (Math.PI * 2D)) {
         if (this.level().isClientSide) {
            this.tentacleMovement = ((float)Math.PI * 2F);
         } else {
            this.tentacleMovement -= ((float)Math.PI * 2F);
            if (this.random.nextInt(10) == 0) {
               this.tentacleSpeed = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
            }

            this.level().broadcastEntityEvent(this, (byte)19);
         }
      }

      if (this.isInWaterOrBubble()) {
         if (this.tentacleMovement < (float)Math.PI) {
            float $$0 = this.tentacleMovement / (float)Math.PI;
            this.tentacleAngle = Mth.sin($$0 * $$0 * (float)Math.PI) * (float)Math.PI * 0.25F;
            if ((double)$$0 > (double)0.75F) {
               this.speed = 1.0F;
               this.rotateSpeed = 1.0F;
            } else {
               this.rotateSpeed *= 0.8F;
            }
         } else {
            this.tentacleAngle = 0.0F;
            this.speed *= 0.9F;
            this.rotateSpeed *= 0.99F;
         }

         if (!this.level().isClientSide) {
            this.setDeltaMovement((double)(this.tx * this.speed), (double)(this.ty * this.speed), (double)(this.tz * this.speed));
         }

         Vec3 $$1 = this.getDeltaMovement();
         double $$2 = $$1.horizontalDistance();
         this.yBodyRot += (-((float)Mth.atan2($$1.x, $$1.z)) * (180F / (float)Math.PI) - this.yBodyRot) * 0.1F;
         this.setYRot(this.yBodyRot);
         this.zBodyRot += (float)Math.PI * this.rotateSpeed * 1.5F;
         this.xBodyRot += (-((float)Mth.atan2($$2, $$1.y)) * (180F / (float)Math.PI) - this.xBodyRot) * 0.1F;
      } else {
         this.tentacleAngle = Mth.abs(Mth.sin(this.tentacleMovement)) * (float)Math.PI * 0.25F;
         if (!this.level().isClientSide) {
            double $$3 = this.getDeltaMovement().y;
            if (this.hasEffect(MobEffects.LEVITATION)) {
               $$3 = 0.05 * (double)(this.getEffect(MobEffects.LEVITATION).getAmplifier() + 1);
            } else if (!this.isNoGravity()) {
               $$3 -= 0.08;
            }

            this.setDeltaMovement((double)0.0F, $$3 * (double)0.98F, (double)0.0F);
         }

         this.xBodyRot += (-90.0F - this.xBodyRot) * 0.02F;
      }

   }

   public boolean hurt(DamageSource p_29963_, float p_29964_) {
      if (super.hurt(p_29963_, p_29964_) && this.getLastHurtByMob() != null) {
         if (!this.level().isClientSide) {
            this.spawnInk();
         }

         return true;
      } else {
         return false;
      }
   }

   private Vec3 rotateVector(Vec3 p_29986_) {
      Vec3 $$1 = p_29986_.xRot(this.xBodyRotO * ((float)Math.PI / 180F));
      $$1 = $$1.yRot(-this.yBodyRotO * ((float)Math.PI / 180F));
      return $$1;
   }

   private void spawnInk() {
      this.playSound(this.getSquirtSound(), this.getSoundVolume(), this.getVoicePitch());
      Vec3 $$0 = this.rotateVector(new Vec3((double)0.0F, (double)-1.0F, (double)0.0F)).add(this.getX(), this.getY(), this.getZ());

      for(int $$1 = 0; $$1 < 30; ++$$1) {
         Vec3 $$2 = this.rotateVector(new Vec3((double)this.random.nextFloat() * 0.6 - 0.3, (double)-1.0F, (double)this.random.nextFloat() * 0.6 - 0.3));
         Vec3 $$3 = $$2.scale(0.3 + (double)(this.random.nextFloat() * 2.0F));
         ((ServerLevel)this.level()).sendParticles(this.getInkParticle(), $$0.x, $$0.y + (double)0.5F, $$0.z, 0, $$3.x, $$3.y, $$3.z, (double)0.1F);
      }

   }

   protected ParticleOptions getInkParticle() {
      return ParticleTypes.SQUID_INK;
   }

   public void travel(Vec3 p_29984_) {
      this.move(MoverType.SELF, this.getDeltaMovement());
   }

   public void handleEntityEvent(byte p_29957_) {
      if (p_29957_ == 19) {
         this.tentacleMovement = 0.0F;
      } else {
         super.handleEntityEvent(p_29957_);
      }

   }

   public void setMovementVector(float p_29959_, float p_29960_, float p_29961_) {
      this.tx = p_29959_;
      this.ty = p_29960_;
      this.tz = p_29961_;
   }

   public boolean hasMovementVector() {
      return this.tx != 0.0F || this.ty != 0.0F || this.tz != 0.0F;
   }

   class SquidRandomMovementGoal extends Goal {
      private final Squid squid;

      public SquidRandomMovementGoal(Squid p_30004_) {
         this.squid = p_30004_;
      }

      public boolean canUse() {
         return true;
      }

      public void tick() {
         int $$0 = this.squid.getNoActionTime();
         if ($$0 > 100) {
            this.squid.setMovementVector(0.0F, 0.0F, 0.0F);
         } else if (this.squid.getRandom().nextInt(reducedTickDelay(50)) == 0 || !this.squid.wasTouchingWater || !this.squid.hasMovementVector()) {
            float $$1 = this.squid.getRandom().nextFloat() * ((float)Math.PI * 2F);
            float $$2 = Mth.cos($$1) * 0.2F;
            float $$3 = -0.1F + this.squid.getRandom().nextFloat() * 0.2F;
            float $$4 = Mth.sin($$1) * 0.2F;
            this.squid.setMovementVector($$2, $$3, $$4);
         }

      }
   }

   class SquidFleeGoal extends Goal {
      private static final float SQUID_FLEE_SPEED = 3.0F;
      private static final float SQUID_FLEE_MIN_DISTANCE = 5.0F;
      private static final float SQUID_FLEE_MAX_DISTANCE = 10.0F;
      private int fleeTicks;

      public boolean canUse() {
         LivingEntity $$0 = Squid.this.getLastHurtByMob();
         if (Squid.this.isInWater() && $$0 != null) {
            return Squid.this.distanceToSqr($$0) < (double)100.0F;
         } else {
            return false;
         }
      }

      public void start() {
         this.fleeTicks = 0;
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }

      public void tick() {
         ++this.fleeTicks;
         LivingEntity $$0 = Squid.this.getLastHurtByMob();
         if ($$0 != null) {
            Vec3 $$1 = new Vec3(Squid.this.getX() - $$0.getX(), Squid.this.getY() - $$0.getY(), Squid.this.getZ() - $$0.getZ());
            BlockState $$2 = Squid.this.level().getBlockState(BlockPos.containing(Squid.this.getX() + $$1.x, Squid.this.getY() + $$1.y, Squid.this.getZ() + $$1.z));
            FluidState $$3 = Squid.this.level().getFluidState(BlockPos.containing(Squid.this.getX() + $$1.x, Squid.this.getY() + $$1.y, Squid.this.getZ() + $$1.z));
            if ($$3.is(FluidTags.WATER) || $$2.isAir()) {
               double $$4 = $$1.length();
               if ($$4 > (double)0.0F) {
                  $$1.normalize();
                  double $$5 = (double)3.0F;
                  if ($$4 > (double)5.0F) {
                     $$5 -= ($$4 - (double)5.0F) / (double)5.0F;
                  }

                  if ($$5 > (double)0.0F) {
                     $$1 = $$1.scale($$5);
                  }
               }

               if ($$2.isAir()) {
                  $$1 = $$1.subtract((double)0.0F, $$1.y, (double)0.0F);
               }

               Squid.this.setMovementVector((float)$$1.x / 20.0F, (float)$$1.y / 20.0F, (float)$$1.z / 20.0F);
            }

            if (this.fleeTicks % 10 == 5) {
               Squid.this.level().addParticle(ParticleTypes.BUBBLE, Squid.this.getX(), Squid.this.getY(), Squid.this.getZ(), (double)0.0F, (double)0.0F, (double)0.0F);
            }

         }
      }
   }
}
