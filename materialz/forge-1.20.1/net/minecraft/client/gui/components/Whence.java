package net.minecraft.client.gui.components;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum Whence {
   ABSOLUTE,
   RELATIVE,
   END;

   // $FF: synthetic method
   private static Whence[] $values() {
      return new Whence[]{ABSOLUTE, RELATIVE, END};
   }
}
