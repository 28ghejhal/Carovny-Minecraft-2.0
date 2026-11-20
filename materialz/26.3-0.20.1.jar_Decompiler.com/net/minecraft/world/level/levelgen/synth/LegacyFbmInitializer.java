package net.minecraft.world.level.levelgen.synth;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Arrays;
import java.util.Objects;
import net.minecraft.util.RandomSource;

/** @deprecated */
@Deprecated
public class LegacyFbmInitializer {
   public static NoiseStack createForLegacyNetherBiome(final RandomSource random, final int firstOctave, final DoubleList amplitudes) {
      int octaves = amplitudes.size();
      int zeroOctaveIndex = -firstOctave;
      PerlinNoise[] noiseLevels = new PerlinNoise[octaves];
      PerlinNoise zeroOctave = new PerlinNoise(random);
      if (zeroOctaveIndex >= 0 && zeroOctaveIndex < octaves) {
         double zeroOctaveAmplitude = amplitudes.getDouble(zeroOctaveIndex);
         if (zeroOctaveAmplitude != (double)0.0F) {
            noiseLevels[zeroOctaveIndex] = zeroOctave;
         }
      }

      for(int i = zeroOctaveIndex - 1; i >= 0; --i) {
         if (i < octaves) {
            double amplitude = amplitudes.getDouble(i);
            if (amplitude != (double)0.0F) {
               noiseLevels[i] = new PerlinNoise(random);
            } else {
               skipOctave(random);
            }
         } else {
            skipOctave(random);
         }
      }

      if (Arrays.stream(noiseLevels).filter(Objects::nonNull).count() != amplitudes.stream().filter((a) -> a != (double)0.0F).count()) {
         throw new IllegalStateException("Failed to create correct number of noise levels for given non-zero amplitudes");
      } else if (zeroOctaveIndex < octaves - 1) {
         throw new IllegalArgumentException("Positive octaves are temporarily disabled");
      } else {
         double factor = Math.pow((double)2.0F, (double)(-zeroOctaveIndex));
         double valueFactor = Math.pow((double)2.0F, (double)(octaves - 1)) / (Math.pow((double)2.0F, (double)octaves) - (double)1.0F);
         NoiseStack.Builder stack = NoiseStack.builder();

         for(int i = 0; i < noiseLevels.length; ++i) {
            PerlinNoise noise = noiseLevels[i];
            if (noise != null) {
               stack.add(noise, factor, (float)(valueFactor * amplitudes.getDouble(i)));
            }

            factor *= (double)2.0F;
            valueFactor /= (double)2.0F;
         }

         return stack.build();
      }
   }

   private static void skipOctave(final RandomSource random) {
      random.consumeCount(262);
   }
}
