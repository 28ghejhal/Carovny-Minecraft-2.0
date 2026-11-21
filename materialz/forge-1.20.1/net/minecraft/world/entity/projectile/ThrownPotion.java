package net.minecraft.world.entity.projectile;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ThrownPotion extends ThrowableItemProjectile implements ItemSupplier {
   public static final double SPLASH_RANGE = (double)4.0F;
   private static final double SPLASH_RANGE_SQ = (double)16.0F;
   public static final Predicate<LivingEntity> WATER_SENSITIVE_OR_ON_FIRE = (p_287524_) -> p_287524_.isSensitiveToWater() || p_287524_.isOnFire();

   public ThrownPotion(EntityType<? extends ThrownPotion> p_37527_, Level p_37528_) {
      super(p_37527_, p_37528_);
   }

   public ThrownPotion(Level p_37535_, LivingEntity p_37536_) {
      super(EntityType.POTION, p_37536_, p_37535_);
   }

   public ThrownPotion(Level p_37530_, double p_37531_, double p_37532_, double p_37533_) {
      super(EntityType.POTION, p_37531_, p_37532_, p_37533_, p_37530_);
   }

   protected Item getDefaultItem() {
      return Items.SPLASH_POTION;
   }

   protected float getGravity() {
      return 0.05F;
   }

   protected void onHitBlock(BlockHitResult p_37541_) {
      super.onHitBlock(p_37541_);
      if (!this.level().isClientSide) {
         ItemStack $$1 = this.getItem();
         Potion $$2 = PotionUtils.getPotion($$1);
         List<MobEffectInstance> $$3 = PotionUtils.getMobEffects($$1);
         boolean $$4 = $$2 == Potions.WATER && $$3.isEmpty();
         Direction $$5 = p_37541_.getDirection();
         BlockPos $$6 = p_37541_.getBlockPos();
         BlockPos $$7 = $$6.relative($$5);
         if ($$4) {
            this.dowseFire($$7);
            this.dowseFire($$7.relative($$5.getOpposite()));

            for(Direction $$8 : Direction.Plane.HORIZONTAL) {
               this.dowseFire($$7.relative($$8));
            }
         }

      }
   }

   protected void onHit(HitResult p_37543_) {
      super.onHit(p_37543_);
      if (!this.level().isClientSide) {
         ItemStack $$1 = this.getItem();
         Potion $$2 = PotionUtils.getPotion($$1);
         List<MobEffectInstance> $$3 = PotionUtils.getMobEffects($$1);
         boolean $$4 = $$2 == Potions.WATER && $$3.isEmpty();
         if ($$4) {
            this.applyWater();
         } else if (!$$3.isEmpty()) {
            if (this.isLingering()) {
               this.makeAreaOfEffectCloud($$1, $$2);
            } else {
               this.applySplash($$3, p_37543_.getType() == HitResult.Type.ENTITY ? ((EntityHitResult)p_37543_).getEntity() : null);
            }
         }

         int $$5 = $$2.hasInstantEffects() ? 2007 : 2002;
         this.level().levelEvent($$5, this.blockPosition(), PotionUtils.getColor($$1));
         this.discard();
      }
   }

   private void applyWater() {
      AABB $$0 = this.getBoundingBox().inflate((double)4.0F, (double)2.0F, (double)4.0F);

      for(LivingEntity $$2 : this.level().getEntitiesOfClass(LivingEntity.class, $$0, WATER_SENSITIVE_OR_ON_FIRE)) {
         double $$3 = this.distanceToSqr($$2);
         if ($$3 < (double)16.0F) {
            if ($$2.isSensitiveToWater()) {
               $$2.hurt(this.damageSources().indirectMagic(this, this.getOwner()), 1.0F);
            }

            if ($$2.isOnFire() && $$2.isAlive()) {
               $$2.extinguishFire();
            }
         }
      }

      for(Axolotl $$5 : this.level().getEntitiesOfClass(Axolotl.class, $$0)) {
         $$5.rehydrate();
      }

   }

   private void applySplash(List<MobEffectInstance> p_37548_, @Nullable Entity p_37549_) {
      AABB $$2 = this.getBoundingBox().inflate((double)4.0F, (double)2.0F, (double)4.0F);
      List<LivingEntity> $$3 = this.level().getEntitiesOfClass(LivingEntity.class, $$2);
      if (!$$3.isEmpty()) {
         Entity $$4 = this.getEffectSource();

         for(LivingEntity $$5 : $$3) {
            if ($$5.isAffectedByPotions()) {
               double $$6 = this.distanceToSqr($$5);
               if ($$6 < (double)16.0F) {
                  double $$7;
                  if ($$5 == p_37549_) {
                     $$7 = (double)1.0F;
                  } else {
                     $$7 = (double)1.0F - Math.sqrt($$6) / (double)4.0F;
                  }

                  for(MobEffectInstance $$9 : p_37548_) {
                     MobEffect $$10 = $$9.getEffect();
                     if ($$10.isInstantenous()) {
                        $$10.applyInstantenousEffect(this, this.getOwner(), $$5, $$9.getAmplifier(), $$7);
                     } else {
                        int $$11 = $$9.mapDuration((p_267930_) -> (int)($$7 * (double)p_267930_ + (double)0.5F));
                        MobEffectInstance $$12 = new MobEffectInstance($$10, $$11, $$9.getAmplifier(), $$9.isAmbient(), $$9.isVisible());
                        if (!$$12.endsWithin(20)) {
                           $$5.addEffect($$12, $$4);
                        }
                     }
                  }
               }
            }
         }
      }

   }

   private void makeAreaOfEffectCloud(ItemStack p_37538_, Potion p_37539_) {
      AreaEffectCloud $$2 = new AreaEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
      Entity $$3 = this.getOwner();
      if ($$3 instanceof LivingEntity) {
         $$2.setOwner((LivingEntity)$$3);
      }

      $$2.setRadius(3.0F);
      $$2.setRadiusOnUse(-0.5F);
      $$2.setWaitTime(10);
      $$2.setRadiusPerTick(-$$2.getRadius() / (float)$$2.getDuration());
      $$2.setPotion(p_37539_);

      for(MobEffectInstance $$4 : PotionUtils.getCustomEffects(p_37538_)) {
         $$2.addEffect(new MobEffectInstance($$4));
      }

      CompoundTag $$5 = p_37538_.getTag();
      if ($$5 != null && $$5.contains("CustomPotionColor", 99)) {
         $$2.setFixedColor($$5.getInt("CustomPotionColor"));
      }

      this.level().addFreshEntity($$2);
   }

   private boolean isLingering() {
      return this.getItem().is(Items.LINGERING_POTION);
   }

   private void dowseFire(BlockPos p_150193_) {
      BlockState $$1 = this.level().getBlockState(p_150193_);
      if ($$1.is(BlockTags.FIRE)) {
         this.level().removeBlock(p_150193_, false);
      } else if (AbstractCandleBlock.isLit($$1)) {
         AbstractCandleBlock.extinguish((Player)null, $$1, this.level(), p_150193_);
      } else if (CampfireBlock.isLitCampfire($$1)) {
         this.level().levelEvent((Player)null, 1009, p_150193_, 0);
         CampfireBlock.dowse(this.getOwner(), this.level(), p_150193_, $$1);
         this.level().setBlockAndUpdate(p_150193_, (BlockState)$$1.setValue(CampfireBlock.LIT, false));
      }

   }
}
