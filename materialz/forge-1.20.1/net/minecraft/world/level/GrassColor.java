package net.minecraft.world.level;

public class GrassColor {
   private static int[] pixels = new int[65536];

   public static void init(int[] p_46419_) {
      pixels = p_46419_;
   }

   public static int get(double p_46416_, double p_46417_) {
      p_46417_ *= p_46416_;
      int $$2 = (int)(((double)1.0F - p_46416_) * (double)255.0F);
      int $$3 = (int)(((double)1.0F - p_46417_) * (double)255.0F);
      int $$4 = $$3 << 8 | $$2;
      return $$4 >= pixels.length ? -65281 : pixels[$$4];
   }

   public static int getDefaultColor() {
      return get((double)0.5F, (double)1.0F);
   }
}
