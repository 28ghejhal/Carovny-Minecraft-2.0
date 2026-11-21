package net.minecraft.stats;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;
import net.minecraft.Util;

public interface StatFormatter {
   DecimalFormat DECIMAL_FORMAT = (DecimalFormat)Util.make(new DecimalFormat("########0.00"), (p_12881_) -> p_12881_.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT)));
   StatFormatter DEFAULT;
   StatFormatter DIVIDE_BY_TEN;
   StatFormatter DISTANCE;
   StatFormatter TIME;

   String format(int var1);

   static {
      NumberFormat var10000 = NumberFormat.getIntegerInstance(Locale.US);
      Objects.requireNonNull(var10000);
      DEFAULT = var10000::format;
      DIVIDE_BY_TEN = (p_12885_) -> DECIMAL_FORMAT.format((double)p_12885_ * 0.1);
      DISTANCE = (p_12883_) -> {
         double $$1 = (double)p_12883_ / (double)100.0F;
         double $$2 = $$1 / (double)1000.0F;
         if ($$2 > (double)0.5F) {
            return DECIMAL_FORMAT.format($$2) + " km";
         } else {
            return $$1 > (double)0.5F ? DECIMAL_FORMAT.format($$1) + " m" : p_12883_ + " cm";
         }
      };
      TIME = (p_12879_) -> {
         double $$1 = (double)p_12879_ / (double)20.0F;
         double $$2 = $$1 / (double)60.0F;
         double $$3 = $$2 / (double)60.0F;
         double $$4 = $$3 / (double)24.0F;
         double $$5 = $$4 / (double)365.0F;
         if ($$5 > (double)0.5F) {
            return DECIMAL_FORMAT.format($$5) + " y";
         } else if ($$4 > (double)0.5F) {
            return DECIMAL_FORMAT.format($$4) + " d";
         } else if ($$3 > (double)0.5F) {
            return DECIMAL_FORMAT.format($$3) + " h";
         } else {
            return $$2 > (double)0.5F ? DECIMAL_FORMAT.format($$2) + " m" : $$1 + " s";
         }
      };
   }
}
