package net.minecraft.world.level.levelgen;

import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.TerrainProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class NoiseRouterData {
   public static final float GLOBAL_OFFSET = -0.50375F;
   private static final float ORE_THICKNESS = 0.08F;
   private static final double VEININESS_FREQUENCY = (double)1.5F;
   private static final double NOODLE_SPACING_AND_STRAIGHTNESS = (double)1.5F;
   private static final double SURFACE_DENSITY_THRESHOLD = (double)1.5625F;
   private static final double CHEESE_NOISE_TARGET = (double)-0.703125F;
   public static final int ISLAND_CHUNK_DISTANCE = 64;
   public static final long ISLAND_CHUNK_DISTANCE_SQR = 4096L;
   private static final DensityFunction BLENDING_FACTOR = DensityFunctions.constant((double)10.0F);
   private static final DensityFunction BLENDING_JAGGEDNESS = DensityFunctions.zero();
   private static final ResourceKey<DensityFunction> ZERO = createKey("zero");
   private static final ResourceKey<DensityFunction> Y = createKey("y");
   private static final ResourceKey<DensityFunction> SHIFT_X = createKey("shift_x");
   private static final ResourceKey<DensityFunction> SHIFT_Z = createKey("shift_z");
   private static final ResourceKey<DensityFunction> BASE_3D_NOISE_OVERWORLD = createKey("overworld/base_3d_noise");
   private static final ResourceKey<DensityFunction> BASE_3D_NOISE_NETHER = createKey("nether/base_3d_noise");
   private static final ResourceKey<DensityFunction> BASE_3D_NOISE_END = createKey("end/base_3d_noise");
   public static final ResourceKey<DensityFunction> CONTINENTS = createKey("overworld/continents");
   public static final ResourceKey<DensityFunction> EROSION = createKey("overworld/erosion");
   public static final ResourceKey<DensityFunction> RIDGES = createKey("overworld/ridges");
   public static final ResourceKey<DensityFunction> RIDGES_FOLDED = createKey("overworld/ridges_folded");
   public static final ResourceKey<DensityFunction> OFFSET = createKey("overworld/offset");
   public static final ResourceKey<DensityFunction> FACTOR = createKey("overworld/factor");
   public static final ResourceKey<DensityFunction> JAGGEDNESS = createKey("overworld/jaggedness");
   public static final ResourceKey<DensityFunction> DEPTH = createKey("overworld/depth");
   private static final ResourceKey<DensityFunction> SLOPED_CHEESE = createKey("overworld/sloped_cheese");
   public static final ResourceKey<DensityFunction> CONTINENTS_LARGE = createKey("overworld_large_biomes/continents");
   public static final ResourceKey<DensityFunction> EROSION_LARGE = createKey("overworld_large_biomes/erosion");
   private static final ResourceKey<DensityFunction> OFFSET_LARGE = createKey("overworld_large_biomes/offset");
   private static final ResourceKey<DensityFunction> FACTOR_LARGE = createKey("overworld_large_biomes/factor");
   private static final ResourceKey<DensityFunction> JAGGEDNESS_LARGE = createKey("overworld_large_biomes/jaggedness");
   private static final ResourceKey<DensityFunction> DEPTH_LARGE = createKey("overworld_large_biomes/depth");
   private static final ResourceKey<DensityFunction> SLOPED_CHEESE_LARGE = createKey("overworld_large_biomes/sloped_cheese");
   private static final ResourceKey<DensityFunction> OFFSET_AMPLIFIED = createKey("overworld_amplified/offset");
   private static final ResourceKey<DensityFunction> FACTOR_AMPLIFIED = createKey("overworld_amplified/factor");
   private static final ResourceKey<DensityFunction> JAGGEDNESS_AMPLIFIED = createKey("overworld_amplified/jaggedness");
   private static final ResourceKey<DensityFunction> DEPTH_AMPLIFIED = createKey("overworld_amplified/depth");
   private static final ResourceKey<DensityFunction> SLOPED_CHEESE_AMPLIFIED = createKey("overworld_amplified/sloped_cheese");
   private static final ResourceKey<DensityFunction> SLOPED_CHEESE_END = createKey("end/sloped_cheese");
   private static final ResourceKey<DensityFunction> SPAGHETTI_ROUGHNESS_FUNCTION = createKey("overworld/caves/spaghetti_roughness_function");
   private static final ResourceKey<DensityFunction> ENTRANCES = createKey("overworld/caves/entrances");
   private static final ResourceKey<DensityFunction> NOODLE = createKey("overworld/caves/noodle");
   private static final ResourceKey<DensityFunction> PILLARS = createKey("overworld/caves/pillars");
   private static final ResourceKey<DensityFunction> SPAGHETTI_2D_THICKNESS_MODULATOR = createKey("overworld/caves/spaghetti_2d_thickness_modulator");
   private static final ResourceKey<DensityFunction> SPAGHETTI_2D = createKey("overworld/caves/spaghetti_2d");

   private static ResourceKey<DensityFunction> createKey(String p_209537_) {
      return ResourceKey.create(Registries.DENSITY_FUNCTION, new ResourceLocation(p_209537_));
   }

   public static Holder<? extends DensityFunction> bootstrap(BootstapContext<DensityFunction> p_256220_) {
      HolderGetter<NormalNoise.NoiseParameters> $$1 = p_256220_.<NormalNoise.NoiseParameters>lookup(Registries.NOISE);
      HolderGetter<DensityFunction> $$2 = p_256220_.<DensityFunction>lookup(Registries.DENSITY_FUNCTION);
      p_256220_.register(ZERO, DensityFunctions.zero());
      int $$3 = DimensionType.MIN_Y * 2;
      int $$4 = DimensionType.MAX_Y * 2;
      p_256220_.register(Y, DensityFunctions.yClampedGradient($$3, $$4, (double)$$3, (double)$$4));
      DensityFunction $$5 = registerAndWrap(p_256220_, SHIFT_X, DensityFunctions.flatCache(DensityFunctions.cache2d(DensityFunctions.shiftA($$1.getOrThrow(Noises.SHIFT)))));
      DensityFunction $$6 = registerAndWrap(p_256220_, SHIFT_Z, DensityFunctions.flatCache(DensityFunctions.cache2d(DensityFunctions.shiftB($$1.getOrThrow(Noises.SHIFT)))));
      p_256220_.register(BASE_3D_NOISE_OVERWORLD, BlendedNoise.createUnseeded((double)0.25F, (double)0.125F, (double)80.0F, (double)160.0F, (double)8.0F));
      p_256220_.register(BASE_3D_NOISE_NETHER, BlendedNoise.createUnseeded((double)0.25F, (double)0.375F, (double)80.0F, (double)60.0F, (double)8.0F));
      p_256220_.register(BASE_3D_NOISE_END, BlendedNoise.createUnseeded((double)0.25F, (double)0.25F, (double)80.0F, (double)160.0F, (double)4.0F));
      Holder<DensityFunction> $$7 = p_256220_.register(CONTINENTS, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d($$5, $$6, (double)0.25F, $$1.getOrThrow(Noises.CONTINENTALNESS))));
      Holder<DensityFunction> $$8 = p_256220_.register(EROSION, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d($$5, $$6, (double)0.25F, $$1.getOrThrow(Noises.EROSION))));
      DensityFunction $$9 = registerAndWrap(p_256220_, RIDGES, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d($$5, $$6, (double)0.25F, $$1.getOrThrow(Noises.RIDGE))));
      p_256220_.register(RIDGES_FOLDED, peaksAndValleys($$9));
      DensityFunction $$10 = DensityFunctions.noise($$1.getOrThrow(Noises.JAGGED), (double)1500.0F, (double)0.0F);
      registerTerrainNoises(p_256220_, $$2, $$10, $$7, $$8, OFFSET, FACTOR, JAGGEDNESS, DEPTH, SLOPED_CHEESE, false);
      Holder<DensityFunction> $$11 = p_256220_.register(CONTINENTS_LARGE, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d($$5, $$6, (double)0.25F, $$1.getOrThrow(Noises.CONTINENTALNESS_LARGE))));
      Holder<DensityFunction> $$12 = p_256220_.register(EROSION_LARGE, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d($$5, $$6, (double)0.25F, $$1.getOrThrow(Noises.EROSION_LARGE))));
      registerTerrainNoises(p_256220_, $$2, $$10, $$11, $$12, OFFSET_LARGE, FACTOR_LARGE, JAGGEDNESS_LARGE, DEPTH_LARGE, SLOPED_CHEESE_LARGE, false);
      registerTerrainNoises(p_256220_, $$2, $$10, $$7, $$8, OFFSET_AMPLIFIED, FACTOR_AMPLIFIED, JAGGEDNESS_AMPLIFIED, DEPTH_AMPLIFIED, SLOPED_CHEESE_AMPLIFIED, true);
      p_256220_.register(SLOPED_CHEESE_END, DensityFunctions.add(DensityFunctions.endIslands(0L), getFunction($$2, BASE_3D_NOISE_END)));
      p_256220_.register(SPAGHETTI_ROUGHNESS_FUNCTION, spaghettiRoughnessFunction($$1));
      p_256220_.register(SPAGHETTI_2D_THICKNESS_MODULATOR, DensityFunctions.cacheOnce(DensityFunctions.mappedNoise($$1.getOrThrow(Noises.SPAGHETTI_2D_THICKNESS), (double)2.0F, (double)1.0F, -0.6, -1.3)));
      p_256220_.register(SPAGHETTI_2D, spaghetti2D($$2, $$1));
      p_256220_.register(ENTRANCES, entrances($$2, $$1));
      p_256220_.register(NOODLE, noodle($$2, $$1));
      return p_256220_.register(PILLARS, pillars($$1));
   }

   private static void registerTerrainNoises(BootstapContext<DensityFunction> p_256336_, HolderGetter<DensityFunction> p_256393_, DensityFunction p_224476_, Holder<DensityFunction> p_224477_, Holder<DensityFunction> p_224478_, ResourceKey<DensityFunction> p_224479_, ResourceKey<DensityFunction> p_224480_, ResourceKey<DensityFunction> p_224481_, ResourceKey<DensityFunction> p_224482_, ResourceKey<DensityFunction> p_224483_, boolean p_224484_) {
      DensityFunctions.Spline.Coordinate $$11 = new DensityFunctions.Spline.Coordinate(p_224477_);
      DensityFunctions.Spline.Coordinate $$12 = new DensityFunctions.Spline.Coordinate(p_224478_);
      DensityFunctions.Spline.Coordinate $$13 = new DensityFunctions.Spline.Coordinate(p_256393_.getOrThrow(RIDGES));
      DensityFunctions.Spline.Coordinate $$14 = new DensityFunctions.Spline.Coordinate(p_256393_.getOrThrow(RIDGES_FOLDED));
      DensityFunction $$15 = registerAndWrap(p_256336_, p_224479_, splineWithBlending(DensityFunctions.add(DensityFunctions.constant((double)-0.50375F), DensityFunctions.spline(TerrainProvider.overworldOffset($$11, $$12, $$14, p_224484_))), DensityFunctions.blendOffset()));
      DensityFunction $$16 = registerAndWrap(p_256336_, p_224480_, splineWithBlending(DensityFunctions.spline(TerrainProvider.overworldFactor($$11, $$12, $$13, $$14, p_224484_)), BLENDING_FACTOR));
      DensityFunction $$17 = registerAndWrap(p_256336_, p_224482_, DensityFunctions.add(DensityFunctions.yClampedGradient(-64, 320, (double)1.5F, (double)-1.5F), $$15));
      DensityFunction $$18 = registerAndWrap(p_256336_, p_224481_, splineWithBlending(DensityFunctions.spline(TerrainProvider.overworldJaggedness($$11, $$12, $$13, $$14, p_224484_)), BLENDING_JAGGEDNESS));
      DensityFunction $$19 = DensityFunctions.mul($$18, p_224476_.halfNegative());
      DensityFunction $$20 = noiseGradientDensity($$16, DensityFunctions.add($$17, $$19));
      p_256336_.register(p_224483_, DensityFunctions.add($$20, getFunction(p_256393_, BASE_3D_NOISE_OVERWORLD)));
   }

   private static DensityFunction registerAndWrap(BootstapContext<DensityFunction> p_256149_, ResourceKey<DensityFunction> p_255905_, DensityFunction p_255856_) {
      return new DensityFunctions.HolderHolder(p_256149_.register(p_255905_, p_255856_));
   }

   private static DensityFunction getFunction(HolderGetter<DensityFunction> p_256312_, ResourceKey<DensityFunction> p_256077_) {
      return new DensityFunctions.HolderHolder(p_256312_.getOrThrow(p_256077_));
   }

   private static DensityFunction peaksAndValleys(DensityFunction p_224438_) {
      return DensityFunctions.mul(DensityFunctions.add(DensityFunctions.add(p_224438_.abs(), DensityFunctions.constant(-0.6666666666666666)).abs(), DensityFunctions.constant(-0.3333333333333333)), DensityFunctions.constant((double)-3.0F));
   }

   public static float peaksAndValleys(float p_224436_) {
      return -(Math.abs(Math.abs(p_224436_) - 0.6666667F) - 0.33333334F) * 3.0F;
   }

   private static DensityFunction spaghettiRoughnessFunction(HolderGetter<NormalNoise.NoiseParameters> p_255763_) {
      DensityFunction $$1 = DensityFunctions.noise(p_255763_.getOrThrow(Noises.SPAGHETTI_ROUGHNESS));
      DensityFunction $$2 = DensityFunctions.mappedNoise(p_255763_.getOrThrow(Noises.SPAGHETTI_ROUGHNESS_MODULATOR), (double)0.0F, -0.1);
      return DensityFunctions.cacheOnce(DensityFunctions.mul($$2, DensityFunctions.add($$1.abs(), DensityFunctions.constant(-0.4))));
   }

   private static DensityFunction entrances(HolderGetter<DensityFunction> p_256511_, HolderGetter<NormalNoise.NoiseParameters> p_255899_) {
      DensityFunction $$2 = DensityFunctions.cacheOnce(DensityFunctions.noise(p_255899_.getOrThrow(Noises.SPAGHETTI_3D_RARITY), (double)2.0F, (double)1.0F));
      DensityFunction $$3 = DensityFunctions.mappedNoise(p_255899_.getOrThrow(Noises.SPAGHETTI_3D_THICKNESS), -0.065, -0.088);
      DensityFunction $$4 = DensityFunctions.weirdScaledSampler($$2, p_255899_.getOrThrow(Noises.SPAGHETTI_3D_1), DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE1);
      DensityFunction $$5 = DensityFunctions.weirdScaledSampler($$2, p_255899_.getOrThrow(Noises.SPAGHETTI_3D_2), DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE1);
      DensityFunction $$6 = DensityFunctions.add(DensityFunctions.max($$4, $$5), $$3).clamp((double)-1.0F, (double)1.0F);
      DensityFunction $$7 = getFunction(p_256511_, SPAGHETTI_ROUGHNESS_FUNCTION);
      DensityFunction $$8 = DensityFunctions.noise(p_255899_.getOrThrow(Noises.CAVE_ENTRANCE), (double)0.75F, (double)0.5F);
      DensityFunction $$9 = DensityFunctions.add(DensityFunctions.add($$8, DensityFunctions.constant(0.37)), DensityFunctions.yClampedGradient(-10, 30, 0.3, (double)0.0F));
      return DensityFunctions.cacheOnce(DensityFunctions.min($$9, DensityFunctions.add($$7, $$6)));
   }

   private static DensityFunction noodle(HolderGetter<DensityFunction> p_256402_, HolderGetter<NormalNoise.NoiseParameters> p_255632_) {
      DensityFunction $$2 = getFunction(p_256402_, Y);
      int $$3 = -64;
      int $$4 = -60;
      int $$5 = 320;
      DensityFunction $$6 = yLimitedInterpolatable($$2, DensityFunctions.noise(p_255632_.getOrThrow(Noises.NOODLE), (double)1.0F, (double)1.0F), -60, 320, -1);
      DensityFunction $$7 = yLimitedInterpolatable($$2, DensityFunctions.mappedNoise(p_255632_.getOrThrow(Noises.NOODLE_THICKNESS), (double)1.0F, (double)1.0F, -0.05, -0.1), -60, 320, 0);
      double $$8 = 2.6666666666666665;
      DensityFunction $$9 = yLimitedInterpolatable($$2, DensityFunctions.noise(p_255632_.getOrThrow(Noises.NOODLE_RIDGE_A), 2.6666666666666665, 2.6666666666666665), -60, 320, 0);
      DensityFunction $$10 = yLimitedInterpolatable($$2, DensityFunctions.noise(p_255632_.getOrThrow(Noises.NOODLE_RIDGE_B), 2.6666666666666665, 2.6666666666666665), -60, 320, 0);
      DensityFunction $$11 = DensityFunctions.mul(DensityFunctions.constant((double)1.5F), DensityFunctions.max($$9.abs(), $$10.abs()));
      return DensityFunctions.rangeChoice($$6, (double)-1000000.0F, (double)0.0F, DensityFunctions.constant((double)64.0F), DensityFunctions.add($$7, $$11));
   }

   private static DensityFunction pillars(HolderGetter<NormalNoise.NoiseParameters> p_255985_) {
      double $$1 = (double)25.0F;
      double $$2 = 0.3;
      DensityFunction $$3 = DensityFunctions.noise(p_255985_.getOrThrow(Noises.PILLAR), (double)25.0F, 0.3);
      DensityFunction $$4 = DensityFunctions.mappedNoise(p_255985_.getOrThrow(Noises.PILLAR_RARENESS), (double)0.0F, (double)-2.0F);
      DensityFunction $$5 = DensityFunctions.mappedNoise(p_255985_.getOrThrow(Noises.PILLAR_THICKNESS), (double)0.0F, 1.1);
      DensityFunction $$6 = DensityFunctions.add(DensityFunctions.mul($$3, DensityFunctions.constant((double)2.0F)), $$4);
      return DensityFunctions.cacheOnce(DensityFunctions.mul($$6, $$5.cube()));
   }

   private static DensityFunction spaghetti2D(HolderGetter<DensityFunction> p_256535_, HolderGetter<NormalNoise.NoiseParameters> p_255650_) {
      DensityFunction $$2 = DensityFunctions.noise(p_255650_.getOrThrow(Noises.SPAGHETTI_2D_MODULATOR), (double)2.0F, (double)1.0F);
      DensityFunction $$3 = DensityFunctions.weirdScaledSampler($$2, p_255650_.getOrThrow(Noises.SPAGHETTI_2D), DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE2);
      DensityFunction $$4 = DensityFunctions.mappedNoise(p_255650_.getOrThrow(Noises.SPAGHETTI_2D_ELEVATION), (double)0.0F, (double)Math.floorDiv(-64, 8), (double)8.0F);
      DensityFunction $$5 = getFunction(p_256535_, SPAGHETTI_2D_THICKNESS_MODULATOR);
      DensityFunction $$6 = DensityFunctions.add($$4, DensityFunctions.yClampedGradient(-64, 320, (double)8.0F, (double)-40.0F)).abs();
      DensityFunction $$7 = DensityFunctions.add($$6, $$5).cube();
      double $$8 = 0.083;
      DensityFunction $$9 = DensityFunctions.add($$3, DensityFunctions.mul(DensityFunctions.constant(0.083), $$5));
      return DensityFunctions.max($$9, $$7).clamp((double)-1.0F, (double)1.0F);
   }

   private static DensityFunction underground(HolderGetter<DensityFunction> p_256548_, HolderGetter<NormalNoise.NoiseParameters> p_256236_, DensityFunction p_256658_) {
      DensityFunction $$3 = getFunction(p_256548_, SPAGHETTI_2D);
      DensityFunction $$4 = getFunction(p_256548_, SPAGHETTI_ROUGHNESS_FUNCTION);
      DensityFunction $$5 = DensityFunctions.noise(p_256236_.getOrThrow(Noises.CAVE_LAYER), (double)8.0F);
      DensityFunction $$6 = DensityFunctions.mul(DensityFunctions.constant((double)4.0F), $$5.square());
      DensityFunction $$7 = DensityFunctions.noise(p_256236_.getOrThrow(Noises.CAVE_CHEESE), 0.6666666666666666);
      DensityFunction $$8 = DensityFunctions.add(DensityFunctions.add(DensityFunctions.constant(0.27), $$7).clamp((double)-1.0F, (double)1.0F), DensityFunctions.add(DensityFunctions.constant((double)1.5F), DensityFunctions.mul(DensityFunctions.constant(-0.64), p_256658_)).clamp((double)0.0F, (double)0.5F));
      DensityFunction $$9 = DensityFunctions.add($$6, $$8);
      DensityFunction $$10 = DensityFunctions.min(DensityFunctions.min($$9, getFunction(p_256548_, ENTRANCES)), DensityFunctions.add($$3, $$4));
      DensityFunction $$11 = getFunction(p_256548_, PILLARS);
      DensityFunction $$12 = DensityFunctions.rangeChoice($$11, (double)-1000000.0F, 0.03, DensityFunctions.constant((double)-1000000.0F), $$11);
      return DensityFunctions.max($$10, $$12);
   }

   private static DensityFunction postProcess(DensityFunction p_224493_) {
      DensityFunction $$1 = DensityFunctions.blendDensity(p_224493_);
      return DensityFunctions.mul(DensityFunctions.interpolated($$1), DensityFunctions.constant(0.64)).squeeze();
   }

   protected static NoiseRouter overworld(HolderGetter<DensityFunction> p_255681_, HolderGetter<NormalNoise.NoiseParameters> p_256005_, boolean p_255649_, boolean p_255617_) {
      DensityFunction $$4 = DensityFunctions.noise(p_256005_.getOrThrow(Noises.AQUIFER_BARRIER), (double)0.5F);
      DensityFunction $$5 = DensityFunctions.noise(p_256005_.getOrThrow(Noises.AQUIFER_FLUID_LEVEL_FLOODEDNESS), 0.67);
      DensityFunction $$6 = DensityFunctions.noise(p_256005_.getOrThrow(Noises.AQUIFER_FLUID_LEVEL_SPREAD), 0.7142857142857143);
      DensityFunction $$7 = DensityFunctions.noise(p_256005_.getOrThrow(Noises.AQUIFER_LAVA));
      DensityFunction $$8 = getFunction(p_255681_, SHIFT_X);
      DensityFunction $$9 = getFunction(p_255681_, SHIFT_Z);
      DensityFunction $$10 = DensityFunctions.shiftedNoise2d($$8, $$9, (double)0.25F, p_256005_.getOrThrow(p_255649_ ? Noises.TEMPERATURE_LARGE : Noises.TEMPERATURE));
      DensityFunction $$11 = DensityFunctions.shiftedNoise2d($$8, $$9, (double)0.25F, p_256005_.getOrThrow(p_255649_ ? Noises.VEGETATION_LARGE : Noises.VEGETATION));
      DensityFunction $$12 = getFunction(p_255681_, p_255649_ ? FACTOR_LARGE : (p_255617_ ? FACTOR_AMPLIFIED : FACTOR));
      DensityFunction $$13 = getFunction(p_255681_, p_255649_ ? DEPTH_LARGE : (p_255617_ ? DEPTH_AMPLIFIED : DEPTH));
      DensityFunction $$14 = noiseGradientDensity(DensityFunctions.cache2d($$12), $$13);
      DensityFunction $$15 = getFunction(p_255681_, p_255649_ ? SLOPED_CHEESE_LARGE : (p_255617_ ? SLOPED_CHEESE_AMPLIFIED : SLOPED_CHEESE));
      DensityFunction $$16 = DensityFunctions.min($$15, DensityFunctions.mul(DensityFunctions.constant((double)5.0F), getFunction(p_255681_, ENTRANCES)));
      DensityFunction $$17 = DensityFunctions.rangeChoice($$15, (double)-1000000.0F, (double)1.5625F, $$16, underground(p_255681_, p_256005_, $$15));
      DensityFunction $$18 = DensityFunctions.min(postProcess(slideOverworld(p_255617_, $$17)), getFunction(p_255681_, NOODLE));
      DensityFunction $$19 = getFunction(p_255681_, Y);
      int $$20 = Stream.of(OreVeinifier.VeinType.values()).mapToInt((p_224495_) -> p_224495_.minY).min().orElse(-DimensionType.MIN_Y * 2);
      int $$21 = Stream.of(OreVeinifier.VeinType.values()).mapToInt((p_224457_) -> p_224457_.maxY).max().orElse(-DimensionType.MIN_Y * 2);
      DensityFunction $$22 = yLimitedInterpolatable($$19, DensityFunctions.noise(p_256005_.getOrThrow(Noises.ORE_VEININESS), (double)1.5F, (double)1.5F), $$20, $$21, 0);
      float $$23 = 4.0F;
      DensityFunction $$24 = yLimitedInterpolatable($$19, DensityFunctions.noise(p_256005_.getOrThrow(Noises.ORE_VEIN_A), (double)4.0F, (double)4.0F), $$20, $$21, 0).abs();
      DensityFunction $$25 = yLimitedInterpolatable($$19, DensityFunctions.noise(p_256005_.getOrThrow(Noises.ORE_VEIN_B), (double)4.0F, (double)4.0F), $$20, $$21, 0).abs();
      DensityFunction $$26 = DensityFunctions.add(DensityFunctions.constant((double)-0.08F), DensityFunctions.max($$24, $$25));
      DensityFunction $$27 = DensityFunctions.noise(p_256005_.getOrThrow(Noises.ORE_GAP));
      return new NoiseRouter($$4, $$5, $$6, $$7, $$10, $$11, getFunction(p_255681_, p_255649_ ? CONTINENTS_LARGE : CONTINENTS), getFunction(p_255681_, p_255649_ ? EROSION_LARGE : EROSION), $$13, getFunction(p_255681_, RIDGES), slideOverworld(p_255617_, DensityFunctions.add($$14, DensityFunctions.constant((double)-0.703125F)).clamp((double)-64.0F, (double)64.0F)), $$18, $$22, $$26, $$27);
   }

   private static NoiseRouter noNewCaves(HolderGetter<DensityFunction> p_255724_, HolderGetter<NormalNoise.NoiseParameters> p_255986_, DensityFunction p_256378_) {
      DensityFunction $$3 = getFunction(p_255724_, SHIFT_X);
      DensityFunction $$4 = getFunction(p_255724_, SHIFT_Z);
      DensityFunction $$5 = DensityFunctions.shiftedNoise2d($$3, $$4, (double)0.25F, p_255986_.getOrThrow(Noises.TEMPERATURE));
      DensityFunction $$6 = DensityFunctions.shiftedNoise2d($$3, $$4, (double)0.25F, p_255986_.getOrThrow(Noises.VEGETATION));
      DensityFunction $$7 = postProcess(p_256378_);
      return new NoiseRouter(DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), $$5, $$6, DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), $$7, DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero());
   }

   private static DensityFunction slideOverworld(boolean p_224490_, DensityFunction p_224491_) {
      return slide(p_224491_, -64, 384, p_224490_ ? 16 : 80, p_224490_ ? 0 : 64, (double)-0.078125F, 0, 24, p_224490_ ? 0.4 : (double)0.1171875F);
   }

   private static DensityFunction slideNetherLike(HolderGetter<DensityFunction> p_256084_, int p_255802_, int p_255834_) {
      return slide(getFunction(p_256084_, BASE_3D_NOISE_NETHER), p_255802_, p_255834_, 24, 0, (double)0.9375F, -8, 24, (double)2.5F);
   }

   private static DensityFunction slideEndLike(DensityFunction p_224440_, int p_224441_, int p_224442_) {
      return slide(p_224440_, p_224441_, p_224442_, 72, -184, (double)-23.4375F, 4, 32, (double)-0.234375F);
   }

   protected static NoiseRouter nether(HolderGetter<DensityFunction> p_256256_, HolderGetter<NormalNoise.NoiseParameters> p_256169_) {
      return noNewCaves(p_256256_, p_256169_, slideNetherLike(p_256256_, 0, 128));
   }

   protected static NoiseRouter caves(HolderGetter<DensityFunction> p_256088_, HolderGetter<NormalNoise.NoiseParameters> p_255675_) {
      return noNewCaves(p_256088_, p_255675_, slideNetherLike(p_256088_, -64, 192));
   }

   protected static NoiseRouter floatingIslands(HolderGetter<DensityFunction> p_256633_, HolderGetter<NormalNoise.NoiseParameters> p_255902_) {
      return noNewCaves(p_256633_, p_255902_, slideEndLike(getFunction(p_256633_, BASE_3D_NOISE_END), 0, 256));
   }

   private static DensityFunction slideEnd(DensityFunction p_224506_) {
      return slideEndLike(p_224506_, 0, 128);
   }

   protected static NoiseRouter end(HolderGetter<DensityFunction> p_256079_) {
      DensityFunction $$1 = DensityFunctions.cache2d(DensityFunctions.endIslands(0L));
      DensityFunction $$2 = postProcess(slideEnd(getFunction(p_256079_, SLOPED_CHEESE_END)));
      return new NoiseRouter(DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), $$1, DensityFunctions.zero(), DensityFunctions.zero(), slideEnd(DensityFunctions.add($$1, DensityFunctions.constant((double)-0.703125F))), $$2, DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero());
   }

   protected static NoiseRouter none() {
      return new NoiseRouter(DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero());
   }

   private static DensityFunction splineWithBlending(DensityFunction p_224454_, DensityFunction p_224455_) {
      DensityFunction $$2 = DensityFunctions.lerp(DensityFunctions.blendAlpha(), p_224455_, p_224454_);
      return DensityFunctions.flatCache(DensityFunctions.cache2d($$2));
   }

   private static DensityFunction noiseGradientDensity(DensityFunction p_212272_, DensityFunction p_212273_) {
      DensityFunction $$2 = DensityFunctions.mul(p_212273_, p_212272_);
      return DensityFunctions.mul(DensityFunctions.constant((double)4.0F), $$2.quarterNegative());
   }

   private static DensityFunction yLimitedInterpolatable(DensityFunction p_209472_, DensityFunction p_209473_, int p_209474_, int p_209475_, int p_209476_) {
      return DensityFunctions.interpolated(DensityFunctions.rangeChoice(p_209472_, (double)p_209474_, (double)(p_209475_ + 1), p_209473_, DensityFunctions.constant((double)p_209476_)));
   }

   private static DensityFunction slide(DensityFunction p_224444_, int p_224445_, int p_224446_, int p_224447_, int p_224448_, double p_224449_, int p_224450_, int p_224451_, double p_224452_) {
      DensityFunction $$10 = DensityFunctions.yClampedGradient(p_224445_ + p_224446_ - p_224447_, p_224445_ + p_224446_ - p_224448_, (double)1.0F, (double)0.0F);
      DensityFunction $$9 = DensityFunctions.lerp($$10, p_224449_, p_224444_);
      DensityFunction $$11 = DensityFunctions.yClampedGradient(p_224445_ + p_224450_, p_224445_ + p_224451_, (double)0.0F, (double)1.0F);
      $$9 = DensityFunctions.lerp($$11, p_224452_, $$9);
      return $$9;
   }

   protected static final class QuantizedSpaghettiRarity {
      protected static double getSphaghettiRarity2D(double p_209564_) {
         if (p_209564_ < (double)-0.75F) {
            return (double)0.5F;
         } else if (p_209564_ < (double)-0.5F) {
            return (double)0.75F;
         } else if (p_209564_ < (double)0.5F) {
            return (double)1.0F;
         } else {
            return p_209564_ < (double)0.75F ? (double)2.0F : (double)3.0F;
         }
      }

      protected static double getSpaghettiRarity3D(double p_209566_) {
         if (p_209566_ < (double)-0.5F) {
            return (double)0.75F;
         } else if (p_209566_ < (double)0.0F) {
            return (double)1.0F;
         } else {
            return p_209566_ < (double)0.5F ? (double)1.5F : (double)2.0F;
         }
      }
   }
}
