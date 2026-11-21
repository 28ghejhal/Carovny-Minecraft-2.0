package net.minecraft.world.level;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.core.BlockPos;

public class PotentialCalculator {
   private final List<PointCharge> charges = Lists.newArrayList();

   public void addCharge(BlockPos p_47193_, double p_47194_) {
      if (p_47194_ != (double)0.0F) {
         this.charges.add(new PointCharge(p_47193_, p_47194_));
      }

   }

   public double getPotentialEnergyChange(BlockPos p_47196_, double p_47197_) {
      if (p_47197_ == (double)0.0F) {
         return (double)0.0F;
      } else {
         double $$2 = (double)0.0F;

         for(PointCharge $$3 : this.charges) {
            $$2 += $$3.getPotentialChange(p_47196_);
         }

         return $$2 * p_47197_;
      }
   }

   static class PointCharge {
      private final BlockPos pos;
      private final double charge;

      public PointCharge(BlockPos p_47201_, double p_47202_) {
         this.pos = p_47201_;
         this.charge = p_47202_;
      }

      public double getPotentialChange(BlockPos p_47204_) {
         double $$1 = this.pos.distSqr(p_47204_);
         return $$1 == (double)0.0F ? Double.POSITIVE_INFINITY : this.charge / Math.sqrt($$1);
      }
   }
}
