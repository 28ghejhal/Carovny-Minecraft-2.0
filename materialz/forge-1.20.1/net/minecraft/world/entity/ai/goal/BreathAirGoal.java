package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;

public class BreathAirGoal extends Goal {
   private final PathfinderMob mob;

   public BreathAirGoal(PathfinderMob p_25103_) {
      this.mob = p_25103_;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
   }

   public boolean canUse() {
      return this.mob.getAirSupply() < 140;
   }

   public boolean canContinueToUse() {
      return this.canUse();
   }

   public boolean isInterruptable() {
      return false;
   }

   public void start() {
      this.findAirPosition();
   }

   private void findAirPosition() {
      Iterable<BlockPos> $$0 = BlockPos.betweenClosed(Mth.floor(this.mob.getX() - (double)1.0F), this.mob.getBlockY(), Mth.floor(this.mob.getZ() - (double)1.0F), Mth.floor(this.mob.getX() + (double)1.0F), Mth.floor(this.mob.getY() + (double)8.0F), Mth.floor(this.mob.getZ() + (double)1.0F));
      BlockPos $$1 = null;

      for(BlockPos $$2 : $$0) {
         if (this.givesAir(this.mob.level(), $$2)) {
            $$1 = $$2;
            break;
         }
      }

      if ($$1 == null) {
         $$1 = BlockPos.containing(this.mob.getX(), this.mob.getY() + (double)8.0F, this.mob.getZ());
      }

      this.mob.getNavigation().moveTo((double)$$1.getX(), (double)($$1.getY() + 1), (double)$$1.getZ(), (double)1.0F);
   }

   public void tick() {
      this.findAirPosition();
      this.mob.moveRelative(0.02F, new Vec3((double)this.mob.xxa, (double)this.mob.yya, (double)this.mob.zza));
      this.mob.move(MoverType.SELF, this.mob.getDeltaMovement());
   }

   private boolean givesAir(LevelReader p_25107_, BlockPos p_25108_) {
      BlockState $$2 = p_25107_.getBlockState(p_25108_);
      return (p_25107_.getFluidState(p_25108_).isEmpty() || $$2.is(Blocks.BUBBLE_COLUMN)) && $$2.isPathfindable(p_25107_, p_25108_, PathComputationType.LAND);
   }
}
