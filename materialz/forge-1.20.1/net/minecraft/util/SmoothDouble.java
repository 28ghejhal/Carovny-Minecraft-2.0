package net.minecraft.util;

public class SmoothDouble {
   private double targetValue;
   private double remainingValue;
   private double lastAmount;

   public double getNewDeltaValue(double p_14238_, double p_14239_) {
      this.targetValue += p_14238_;
      double $$2 = this.targetValue - this.remainingValue;
      double $$3 = Mth.lerp((double)0.5F, this.lastAmount, $$2);
      double $$4 = Math.signum($$2);
      if ($$4 * $$2 > $$4 * this.lastAmount) {
         $$2 = $$3;
      }

      this.lastAmount = $$3;
      this.remainingValue += $$2 * p_14239_;
      return $$2 * p_14239_;
   }

   public void reset() {
      this.targetValue = (double)0.0F;
      this.remainingValue = (double)0.0F;
      this.lastAmount = (double)0.0F;
   }
}
