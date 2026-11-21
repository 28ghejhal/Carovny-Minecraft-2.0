package net.minecraft.world.level.levelgen.synth;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class SimplexNoise {
   protected static final int[][] GRADIENT = new int[][]{{1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1}, {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}, {1, 1, 0}, {0, -1, 1}, {-1, 1, 0}, {0, -1, -1}};
   private static final double SQRT_3 = Math.sqrt((double)3.0F);
   private static final double F2;
   private static final double G2;
   private final int[] p = new int[512];
   public final double xo;
   public final double yo;
   public final double zo;

   public SimplexNoise(RandomSource p_230549_) {
      this.xo = p_230549_.nextDouble() * (double)256.0F;
      this.yo = p_230549_.nextDouble() * (double)256.0F;
      this.zo = p_230549_.nextDouble() * (double)256.0F;

      for(int $$1 = 0; $$1 < 256; this.p[$$1] = $$1++) {
      }

      for(int $$2 = 0; $$2 < 256; ++$$2) {
         int $$3 = p_230549_.nextInt(256 - $$2);
         int $$4 = this.p[$$2];
         this.p[$$2] = this.p[$$3 + $$2];
         this.p[$$3 + $$2] = $$4;
      }

   }

   private int p(int p_75472_) {
      return this.p[p_75472_ & 255];
   }

   protected static double dot(int[] p_75480_, double p_75481_, double p_75482_, double p_75483_) {
      return (double)p_75480_[0] * p_75481_ + (double)p_75480_[1] * p_75482_ + (double)p_75480_[2] * p_75483_;
   }

   private double getCornerNoise3D(int p_75474_, double p_75475_, double p_75476_, double p_75477_, double p_75478_) {
      double $$5 = p_75478_ - p_75475_ * p_75475_ - p_75476_ * p_75476_ - p_75477_ * p_75477_;
      double $$6;
      if ($$5 < (double)0.0F) {
         $$6 = (double)0.0F;
      } else {
         $$5 *= $$5;
         $$6 = $$5 * $$5 * dot(GRADIENT[p_75474_], p_75475_, p_75476_, p_75477_);
      }

      return $$6;
   }

   public double getValue(double p_75465_, double p_75466_) {
      double $$2 = (p_75465_ + p_75466_) * F2;
      int $$3 = Mth.floor(p_75465_ + $$2);
      int $$4 = Mth.floor(p_75466_ + $$2);
      double $$5 = (double)($$3 + $$4) * G2;
      double $$6 = (double)$$3 - $$5;
      double $$7 = (double)$$4 - $$5;
      double $$8 = p_75465_ - $$6;
      double $$9 = p_75466_ - $$7;
      int $$10;
      int $$11;
      if ($$8 > $$9) {
         $$10 = 1;
         $$11 = 0;
      } else {
         $$10 = 0;
         $$11 = 1;
      }

      double $$14 = $$8 - (double)$$10 + G2;
      double $$15 = $$9 - (double)$$11 + G2;
      double $$16 = $$8 - (double)1.0F + (double)2.0F * G2;
      double $$17 = $$9 - (double)1.0F + (double)2.0F * G2;
      int $$18 = $$3 & 255;
      int $$19 = $$4 & 255;
      int $$20 = this.p($$18 + this.p($$19)) % 12;
      int $$21 = this.p($$18 + $$10 + this.p($$19 + $$11)) % 12;
      int $$22 = this.p($$18 + 1 + this.p($$19 + 1)) % 12;
      double $$23 = this.getCornerNoise3D($$20, $$8, $$9, (double)0.0F, (double)0.5F);
      double $$24 = this.getCornerNoise3D($$21, $$14, $$15, (double)0.0F, (double)0.5F);
      double $$25 = this.getCornerNoise3D($$22, $$16, $$17, (double)0.0F, (double)0.5F);
      return (double)70.0F * ($$23 + $$24 + $$25);
   }

   public double getValue(double p_75468_, double p_75469_, double p_75470_) {
      double $$3 = 0.3333333333333333;
      double $$4 = (p_75468_ + p_75469_ + p_75470_) * 0.3333333333333333;
      int $$5 = Mth.floor(p_75468_ + $$4);
      int $$6 = Mth.floor(p_75469_ + $$4);
      int $$7 = Mth.floor(p_75470_ + $$4);
      double $$8 = 0.16666666666666666;
      double $$9 = (double)($$5 + $$6 + $$7) * 0.16666666666666666;
      double $$10 = (double)$$5 - $$9;
      double $$11 = (double)$$6 - $$9;
      double $$12 = (double)$$7 - $$9;
      double $$13 = p_75468_ - $$10;
      double $$14 = p_75469_ - $$11;
      double $$15 = p_75470_ - $$12;
      int $$16;
      int $$17;
      int $$18;
      int $$19;
      int $$20;
      int $$21;
      if ($$13 >= $$14) {
         if ($$14 >= $$15) {
            $$16 = 1;
            $$17 = 0;
            $$18 = 0;
            $$19 = 1;
            $$20 = 1;
            $$21 = 0;
         } else if ($$13 >= $$15) {
            $$16 = 1;
            $$17 = 0;
            $$18 = 0;
            $$19 = 1;
            $$20 = 0;
            $$21 = 1;
         } else {
            $$16 = 0;
            $$17 = 0;
            $$18 = 1;
            $$19 = 1;
            $$20 = 0;
            $$21 = 1;
         }
      } else if ($$14 < $$15) {
         $$16 = 0;
         $$17 = 0;
         $$18 = 1;
         $$19 = 0;
         $$20 = 1;
         $$21 = 1;
      } else if ($$13 < $$15) {
         $$16 = 0;
         $$17 = 1;
         $$18 = 0;
         $$19 = 0;
         $$20 = 1;
         $$21 = 1;
      } else {
         $$16 = 0;
         $$17 = 1;
         $$18 = 0;
         $$19 = 1;
         $$20 = 1;
         $$21 = 0;
      }

      double $$52 = $$13 - (double)$$16 + 0.16666666666666666;
      double $$53 = $$14 - (double)$$17 + 0.16666666666666666;
      double $$54 = $$15 - (double)$$18 + 0.16666666666666666;
      double $$55 = $$13 - (double)$$19 + 0.3333333333333333;
      double $$56 = $$14 - (double)$$20 + 0.3333333333333333;
      double $$57 = $$15 - (double)$$21 + 0.3333333333333333;
      double $$58 = $$13 - (double)1.0F + (double)0.5F;
      double $$59 = $$14 - (double)1.0F + (double)0.5F;
      double $$60 = $$15 - (double)1.0F + (double)0.5F;
      int $$61 = $$5 & 255;
      int $$62 = $$6 & 255;
      int $$63 = $$7 & 255;
      int $$64 = this.p($$61 + this.p($$62 + this.p($$63))) % 12;
      int $$65 = this.p($$61 + $$16 + this.p($$62 + $$17 + this.p($$63 + $$18))) % 12;
      int $$66 = this.p($$61 + $$19 + this.p($$62 + $$20 + this.p($$63 + $$21))) % 12;
      int $$67 = this.p($$61 + 1 + this.p($$62 + 1 + this.p($$63 + 1))) % 12;
      double $$68 = this.getCornerNoise3D($$64, $$13, $$14, $$15, 0.6);
      double $$69 = this.getCornerNoise3D($$65, $$52, $$53, $$54, 0.6);
      double $$70 = this.getCornerNoise3D($$66, $$55, $$56, $$57, 0.6);
      double $$71 = this.getCornerNoise3D($$67, $$58, $$59, $$60, 0.6);
      return (double)32.0F * ($$68 + $$69 + $$70 + $$71);
   }

   static {
      F2 = (double)0.5F * (SQRT_3 - (double)1.0F);
      G2 = ((double)3.0F - SQRT_3) / (double)6.0F;
   }
}
