package net.minecraft.world.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;

public class TryFindWaterGoal extends Goal {
   private final PathfinderMob mob;

   public TryFindWaterGoal(PathfinderMob p_25964_) {
      this.mob = p_25964_;
   }

   public boolean canUse() {
      return this.mob.onGround() && !this.mob.level().getFluidState(this.mob.blockPosition()).is(FluidTags.WATER);
   }

   public void start() {
      BlockPos $$0 = null;

      for(BlockPos $$2 : BlockPos.betweenClosed(Mth.floor(this.mob.getX() - (double)2.0F), Mth.floor(this.mob.getY() - (double)2.0F), Mth.floor(this.mob.getZ() - (double)2.0F), Mth.floor(this.mob.getX() + (double)2.0F), this.mob.getBlockY(), Mth.floor(this.mob.getZ() + (double)2.0F))) {
         if (this.mob.level().getFluidState($$2).is(FluidTags.WATER)) {
            $$0 = $$2;
            break;
         }
      }

      if ($$0 != null) {
         this.mob.getMoveControl().setWantedPosition((double)$$0.getX(), (double)$$0.getY(), (double)$$0.getZ(), (double)1.0F);
      }

   }
}
