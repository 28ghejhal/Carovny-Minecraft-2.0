package net.minecraft.world.entity.projectile;

import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

public class FireworkRocketEntity extends Projectile implements ItemSupplier {
   private static final EntityDataAccessor<ItemStack> DATA_ID_FIREWORKS_ITEM;
   private static final EntityDataAccessor<OptionalInt> DATA_ATTACHED_TO_TARGET;
   private static final EntityDataAccessor<Boolean> DATA_SHOT_AT_ANGLE;
   private int life;
   private int lifetime;
   @Nullable
   private LivingEntity attachedToEntity;

   public FireworkRocketEntity(EntityType<? extends FireworkRocketEntity> p_37027_, Level p_37028_) {
      super(p_37027_, p_37028_);
   }

   public FireworkRocketEntity(Level p_37030_, double p_37031_, double p_37032_, double p_37033_, ItemStack p_37034_) {
      super(EntityType.FIREWORK_ROCKET, p_37030_);
      this.life = 0;
      this.setPos(p_37031_, p_37032_, p_37033_);
      int i = 1;
      if (!p_37034_.isEmpty() && p_37034_.hasTag()) {
         this.entityData.set(DATA_ID_FIREWORKS_ITEM, p_37034_.copy());
         i += p_37034_.getOrCreateTagElement("Fireworks").getByte("Flight");
      }

      this.setDeltaMovement(this.random.triangle((double)0.0F, 0.002297), 0.05, this.random.triangle((double)0.0F, 0.002297));
      this.lifetime = 10 * i + this.random.nextInt(6) + this.random.nextInt(7);
   }

   public FireworkRocketEntity(Level p_37036_, @Nullable Entity p_37037_, double p_37038_, double p_37039_, double p_37040_, ItemStack p_37041_) {
      this(p_37036_, p_37038_, p_37039_, p_37040_, p_37041_);
      this.setOwner(p_37037_);
   }

   public FireworkRocketEntity(Level p_37058_, ItemStack p_37059_, LivingEntity p_37060_) {
      this(p_37058_, p_37060_, p_37060_.getX(), p_37060_.getY(), p_37060_.getZ(), p_37059_);
      this.entityData.set(DATA_ATTACHED_TO_TARGET, OptionalInt.of(p_37060_.getId()));
      this.attachedToEntity = p_37060_;
   }

   public FireworkRocketEntity(Level p_37043_, ItemStack p_37044_, double p_37045_, double p_37046_, double p_37047_, boolean p_37048_) {
      this(p_37043_, p_37045_, p_37046_, p_37047_, p_37044_);
      this.entityData.set(DATA_SHOT_AT_ANGLE, p_37048_);
   }

   public FireworkRocketEntity(Level p_37050_, ItemStack p_37051_, Entity p_37052_, double p_37053_, double p_37054_, double p_37055_, boolean p_37056_) {
      this(p_37050_, p_37051_, p_37053_, p_37054_, p_37055_, p_37056_);
      this.setOwner(p_37052_);
   }

   protected void defineSynchedData() {
      this.entityData.define(DATA_ID_FIREWORKS_ITEM, ItemStack.EMPTY);
      this.entityData.define(DATA_ATTACHED_TO_TARGET, OptionalInt.empty());
      this.entityData.define(DATA_SHOT_AT_ANGLE, false);
   }

   public boolean shouldRenderAtSqrDistance(double p_37065_) {
      return p_37065_ < (double)4096.0F && !this.isAttachedToEntity();
   }

   public boolean shouldRender(double p_37083_, double p_37084_, double p_37085_) {
      return super.shouldRender(p_37083_, p_37084_, p_37085_) && !this.isAttachedToEntity();
   }

   public void tick() {
      super.tick();
      if (this.isAttachedToEntity()) {
         if (this.attachedToEntity == null) {
            ((OptionalInt)this.entityData.get(DATA_ATTACHED_TO_TARGET)).ifPresent((p_289492_) -> {
               Entity entity = this.level().getEntity(p_289492_);
               if (entity instanceof LivingEntity) {
                  this.attachedToEntity = (LivingEntity)entity;
               }

            });
         }

         if (this.attachedToEntity != null) {
            Vec3 vec3;
            if (this.attachedToEntity.isFallFlying()) {
               Vec3 vec31 = this.attachedToEntity.getLookAngle();
               double d0 = (double)1.5F;
               double d1 = 0.1;
               Vec3 vec32 = this.attachedToEntity.getDeltaMovement();
               this.attachedToEntity.setDeltaMovement(vec32.add(vec31.x * 0.1 + (vec31.x * (double)1.5F - vec32.x) * (double)0.5F, vec31.y * 0.1 + (vec31.y * (double)1.5F - vec32.y) * (double)0.5F, vec31.z * 0.1 + (vec31.z * (double)1.5F - vec32.z) * (double)0.5F));
               vec3 = this.attachedToEntity.getHandHoldingItemAngle(Items.FIREWORK_ROCKET);
            } else {
               vec3 = Vec3.ZERO;
            }

            this.setPos(this.attachedToEntity.getX() + vec3.x, this.attachedToEntity.getY() + vec3.y, this.attachedToEntity.getZ() + vec3.z);
            this.setDeltaMovement(this.attachedToEntity.getDeltaMovement());
         }
      } else {
         if (!this.isShotAtAngle()) {
            double d2 = this.horizontalCollision ? (double)1.0F : 1.15;
            this.setDeltaMovement(this.getDeltaMovement().multiply(d2, (double)1.0F, d2).add((double)0.0F, 0.04, (double)0.0F));
         }

         Vec3 vec33 = this.getDeltaMovement();
         this.move(MoverType.SELF, vec33);
         this.setDeltaMovement(vec33);
      }

      HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
      if (!this.noPhysics) {
         this.onHit(hitresult);
         this.hasImpulse = true;
      }

      this.updateRotation();
      if (this.life == 0 && !this.isSilent()) {
         this.level().playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.AMBIENT, 3.0F, 1.0F);
      }

      ++this.life;
      if (this.level().isClientSide && this.life % 2 < 2) {
         this.level().addParticle(ParticleTypes.FIREWORK, this.getX(), this.getY(), this.getZ(), this.random.nextGaussian() * 0.05, -this.getDeltaMovement().y * (double)0.5F, this.random.nextGaussian() * 0.05);
      }

      if (!this.level().isClientSide && this.life > this.lifetime) {
         this.explode();
      }

   }

   protected void onHit(HitResult result) {
      if (result.getType() == HitResult.Type.MISS || !ForgeEventFactory.onProjectileImpact(this, result)) {
         super.onHit(result);
      }

   }

   private void explode() {
      this.level().broadcastEntityEvent(this, (byte)17);
      this.gameEvent(GameEvent.EXPLODE, this.getOwner());
      this.dealExplosionDamage();
      this.discard();
   }

   protected void onHitEntity(EntityHitResult p_37071_) {
      super.onHitEntity(p_37071_);
      if (!this.level().isClientSide) {
         this.explode();
      }

   }

   protected void onHitBlock(BlockHitResult p_37069_) {
      BlockPos blockpos = new BlockPos(p_37069_.getBlockPos());
      this.level().getBlockState(blockpos).entityInside(this.level(), blockpos, this);
      if (!this.level().isClientSide() && this.hasExplosion()) {
         this.explode();
      }

      super.onHitBlock(p_37069_);
   }

   private boolean hasExplosion() {
      ItemStack itemstack = (ItemStack)this.entityData.get(DATA_ID_FIREWORKS_ITEM);
      CompoundTag compoundtag = itemstack.isEmpty() ? null : itemstack.getTagElement("Fireworks");
      ListTag listtag = compoundtag != null ? compoundtag.getList("Explosions", 10) : null;
      return listtag != null && !listtag.isEmpty();
   }

   private void dealExplosionDamage() {
      float f = 0.0F;
      ItemStack itemstack = (ItemStack)this.entityData.get(DATA_ID_FIREWORKS_ITEM);
      CompoundTag compoundtag = itemstack.isEmpty() ? null : itemstack.getTagElement("Fireworks");
      ListTag listtag = compoundtag != null ? compoundtag.getList("Explosions", 10) : null;
      if (listtag != null && !listtag.isEmpty()) {
         f = 5.0F + (float)(listtag.size() * 2);
      }

      if (f > 0.0F) {
         if (this.attachedToEntity != null) {
            this.attachedToEntity.hurt(this.damageSources().fireworks(this, this.getOwner()), 5.0F + (float)(listtag.size() * 2));
         }

         double d0 = (double)5.0F;
         Vec3 vec3 = this.position();

         for(LivingEntity livingentity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate((double)5.0F))) {
            if (livingentity != this.attachedToEntity && !(this.distanceToSqr(livingentity) > (double)25.0F)) {
               boolean flag = false;

               for(int i = 0; i < 2; ++i) {
                  Vec3 vec31 = new Vec3(livingentity.getX(), livingentity.getY((double)0.5F * (double)i), livingentity.getZ());
                  HitResult hitresult = this.level().clip(new ClipContext(vec3, vec31, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
                  if (hitresult.getType() == HitResult.Type.MISS) {
                     flag = true;
                     break;
                  }
               }

               if (flag) {
                  float f1 = f * (float)Math.sqrt(((double)5.0F - (double)this.distanceTo(livingentity)) / (double)5.0F);
                  livingentity.hurt(this.damageSources().fireworks(this, this.getOwner()), f1);
               }
            }
         }
      }

   }

   private boolean isAttachedToEntity() {
      return ((OptionalInt)this.entityData.get(DATA_ATTACHED_TO_TARGET)).isPresent();
   }

   public boolean isShotAtAngle() {
      return (Boolean)this.entityData.get(DATA_SHOT_AT_ANGLE);
   }

   public void handleEntityEvent(byte p_37063_) {
      if (p_37063_ == 17 && this.level().isClientSide) {
         if (!this.hasExplosion()) {
            for(int i = 0; i < this.random.nextInt(3) + 2; ++i) {
               this.level().addParticle(ParticleTypes.POOF, this.getX(), this.getY(), this.getZ(), this.random.nextGaussian() * 0.05, 0.005, this.random.nextGaussian() * 0.05);
            }
         } else {
            ItemStack itemstack = (ItemStack)this.entityData.get(DATA_ID_FIREWORKS_ITEM);
            CompoundTag compoundtag = itemstack.isEmpty() ? null : itemstack.getTagElement("Fireworks");
            Vec3 vec3 = this.getDeltaMovement();
            this.level().createFireworks(this.getX(), this.getY(), this.getZ(), vec3.x, vec3.y, vec3.z, compoundtag);
         }
      }

      super.handleEntityEvent(p_37063_);
   }

   public void addAdditionalSaveData(CompoundTag p_37075_) {
      super.addAdditionalSaveData(p_37075_);
      p_37075_.putInt("Life", this.life);
      p_37075_.putInt("LifeTime", this.lifetime);
      ItemStack itemstack = (ItemStack)this.entityData.get(DATA_ID_FIREWORKS_ITEM);
      if (!itemstack.isEmpty()) {
         p_37075_.put("FireworksItem", itemstack.save(new CompoundTag()));
      }

      p_37075_.putBoolean("ShotAtAngle", (Boolean)this.entityData.get(DATA_SHOT_AT_ANGLE));
   }

   public void readAdditionalSaveData(CompoundTag p_37073_) {
      super.readAdditionalSaveData(p_37073_);
      this.life = p_37073_.getInt("Life");
      this.lifetime = p_37073_.getInt("LifeTime");
      ItemStack itemstack = ItemStack.of(p_37073_.getCompound("FireworksItem"));
      if (!itemstack.isEmpty()) {
         this.entityData.set(DATA_ID_FIREWORKS_ITEM, itemstack);
      }

      if (p_37073_.contains("ShotAtAngle")) {
         this.entityData.set(DATA_SHOT_AT_ANGLE, p_37073_.getBoolean("ShotAtAngle"));
      }

   }

   public ItemStack getItem() {
      ItemStack itemstack = (ItemStack)this.entityData.get(DATA_ID_FIREWORKS_ITEM);
      return itemstack.isEmpty() ? new ItemStack(Items.FIREWORK_ROCKET) : itemstack;
   }

   public boolean isAttackable() {
      return false;
   }

   static {
      DATA_ID_FIREWORKS_ITEM = SynchedEntityData.<ItemStack>defineId(FireworkRocketEntity.class, EntityDataSerializers.ITEM_STACK);
      DATA_ATTACHED_TO_TARGET = SynchedEntityData.<OptionalInt>defineId(FireworkRocketEntity.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);
      DATA_SHOT_AT_ANGLE = SynchedEntityData.<Boolean>defineId(FireworkRocketEntity.class, EntityDataSerializers.BOOLEAN);
   }
}
