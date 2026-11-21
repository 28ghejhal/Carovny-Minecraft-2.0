package net.minecraft.client.gui.narration;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum NarratedElementType {
   TITLE,
   POSITION,
   HINT,
   USAGE;

   // $FF: synthetic method
   private static NarratedElementType[] $values() {
      return new NarratedElementType[]{TITLE, POSITION, HINT, USAGE};
   }
}
