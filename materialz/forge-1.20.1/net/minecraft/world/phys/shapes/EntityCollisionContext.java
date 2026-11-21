package net.minecraft.world.phys.shapes;

import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FluidState;

public class EntityCollisionContext implements CollisionContext {
   protected static final CollisionContext EMPTY;
   private final boolean descending;
   private final double entityBottom;
   private final ItemStack heldItem;
   private final Predicate<FluidState> canStandOnFluid;
   @Nullable
   private final Entity entity;

   protected EntityCollisionContext(boolean p_198916_, double p_198917_, ItemStack p_198918_, Predicate<FluidState> p_198919_, @Nullable Entity p_198920_) {
      this.descending = p_198916_;
      this.entityBottom = p_198917_;
      this.heldItem = p_198918_;
      this.canStandOnFluid = p_198919_;
      this.entity = p_198920_;
   }

   /** @deprecated */
   @Deprecated
   protected EntityCollisionContext(Entity p_82872_) {
      boolean var10001 = p_82872_.isDescending();
      double var10002 = p_82872_.getY();
      ItemStack var10003 = p_82872_ instanceof LivingEntity ? ((LivingEntity)p_82872_).getMainHandItem() : ItemStack.EMPTY;
      Predicate var2;
      if (p_82872_ instanceof LivingEntity var10004) {
         Objects.requireNonNull((LivingEntity)p_82872_);
         var2 = var10004::canStandOnFluid;
      } else {
         var2 = (p_205113_) -> false;
      }

      this(var10001, var10002, var10003, var2, p_82872_);
   }

   public boolean isHoldingItem(Item p_82879_) {
      return this.heldItem.is(p_82879_);
   }

   public boolean canStandOnFluid(FluidState p_205115_, FluidState p_205116_) {
      return this.canStandOnFluid.test(p_205116_) && !p_205115_.getType().isSame(p_205116_.getType());
   }

   public boolean isDescending() {
      return this.descending;
   }

   public boolean isAbove(VoxelShape p_82886_, BlockPos p_82887_, boolean p_82888_) {
      return this.entityBottom > (double)p_82887_.getY() + p_82886_.max(Direction.Axis.Y) - (double)1.0E-5F;
   }

   @Nullable
   public Entity getEntity() {
      return this.entity;
   }

   static {
      EMPTY = new EntityCollisionContext(false, -Double.MAX_VALUE, ItemStack.EMPTY, (p_205118_) -> false, (Entity)null) {
         public boolean isAbove(VoxelShape p_82898_, BlockPos p_82899_, boolean p_82900_) {
            return p_82900_;
         }
      };
   }
}
