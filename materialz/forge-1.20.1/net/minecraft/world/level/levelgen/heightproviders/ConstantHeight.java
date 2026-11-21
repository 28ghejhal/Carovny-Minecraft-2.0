package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;

public class ConstantHeight extends HeightProvider {
   public static final ConstantHeight ZERO = new ConstantHeight(VerticalAnchor.absolute(0));
   public static final Codec<ConstantHeight> CODEC;
   private final VerticalAnchor value;

   public static ConstantHeight of(VerticalAnchor p_161957_) {
      return new ConstantHeight(p_161957_);
   }

   private ConstantHeight(VerticalAnchor p_161950_) {
      this.value = p_161950_;
   }

   public VerticalAnchor getValue() {
      return this.value;
   }

   public int sample(RandomSource p_226300_, WorldGenerationContext p_226301_) {
      return this.value.resolveY(p_226301_);
   }

   public HeightProviderType<?> getType() {
      return HeightProviderType.CONSTANT;
   }

   public String toString() {
      return this.value.toString();
   }

   static {
      CODEC = Codec.either(VerticalAnchor.CODEC, RecordCodecBuilder.create((p_161955_) -> p_161955_.group(VerticalAnchor.CODEC.fieldOf("value").forGetter((p_161967_) -> p_161967_.value)).apply(p_161955_, ConstantHeight::new))).xmap((p_161953_) -> (ConstantHeight)p_161953_.map(ConstantHeight::of, (p_161965_) -> p_161965_), (p_161959_) -> Either.left(p_161959_.value));
   }
}
