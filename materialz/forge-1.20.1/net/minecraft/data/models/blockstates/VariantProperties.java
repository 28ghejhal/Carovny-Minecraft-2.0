package net.minecraft.data.models.blockstates;

import com.google.gson.JsonPrimitive;
import net.minecraft.resources.ResourceLocation;

public class VariantProperties {
   public static final VariantProperty<Rotation> X_ROT = new VariantProperty<Rotation>("x", (p_125529_) -> new JsonPrimitive(p_125529_.value));
   public static final VariantProperty<Rotation> Y_ROT = new VariantProperty<Rotation>("y", (p_125525_) -> new JsonPrimitive(p_125525_.value));
   public static final VariantProperty<ResourceLocation> MODEL = new VariantProperty<ResourceLocation>("model", (p_125527_) -> new JsonPrimitive(p_125527_.toString()));
   public static final VariantProperty<Boolean> UV_LOCK = new VariantProperty<Boolean>("uvlock", JsonPrimitive::new);
   public static final VariantProperty<Integer> WEIGHT = new VariantProperty<Integer>("weight", JsonPrimitive::new);

   public static enum Rotation {
      R0(0),
      R90(90),
      R180(180),
      R270(270);

      final int value;

      private Rotation(int p_125540_) {
         this.value = p_125540_;
      }

      // $FF: synthetic method
      private static Rotation[] $values() {
         return new Rotation[]{R0, R90, R180, R270};
      }
   }
}
