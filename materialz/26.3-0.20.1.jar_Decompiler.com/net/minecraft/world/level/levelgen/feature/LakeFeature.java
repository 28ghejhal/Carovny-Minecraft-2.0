package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

/** @deprecated */
@Deprecated
public record LakeFeature(Holder<BlockStateProvider> fluid, Holder<BlockStateProvider> barrier, BlockPredicate canPlaceFeature, BlockPredicate canReplaceWithAirOrFluid, BlockPredicate canReplaceWithBarrier) implements Feature {
   public static final MapCodec<LakeFeature> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(BlockStateProvider.CODEC.fieldOf("fluid").forGetter(LakeFeature::fluid), BlockStateProvider.CODEC.fieldOf("barrier").forGetter(LakeFeature::barrier), BlockPredicate.CODEC.fieldOf("can_place_feature").forGetter(LakeFeature::canPlaceFeature), BlockPredicate.CODEC.fieldOf("can_replace_with_air_or_fluid").forGetter(LakeFeature::canReplaceWithAirOrFluid), BlockPredicate.CODEC.fieldOf("can_replace_with_barrier").forGetter(LakeFeature::canReplaceWithBarrier)).apply(i, LakeFeature::new));
   private static final BlockState AIR;

   public MapCodec<LakeFeature> codec() {
      return CODEC;
   }

   public boolean place(final WorldGenLevel level, final ChunkGenerator chunkGenerator, final RandomSource random, BlockPos origin) {
      if (origin.getY() <= level.getMinY() + 4) {
         return false;
      } else {
         origin = origin.offset(-8, -4, -8);
         boolean[] grid = new boolean[2048];
         int spots = random.nextInt(4) + 4;

         for(int i = 0; i < spots; ++i) {
            double xr = random.nextDouble() * (double)6.0F + (double)3.0F;
            double yr = random.nextDouble() * (double)4.0F + (double)2.0F;
            double zr = random.nextDouble() * (double)6.0F + (double)3.0F;
            double xp = random.nextDouble() * ((double)16.0F - xr - (double)2.0F) + (double)1.0F + xr / (double)2.0F;
            double yp = random.nextDouble() * ((double)8.0F - yr - (double)4.0F) + (double)2.0F + yr / (double)2.0F;
            double zp = random.nextDouble() * ((double)16.0F - zr - (double)2.0F) + (double)1.0F + zr / (double)2.0F;

            for(int xx = 1; xx < 15; ++xx) {
               for(int zz = 1; zz < 15; ++zz) {
                  for(int yy = 1; yy < 7; ++yy) {
                     double xd = ((double)xx - xp) / (xr / (double)2.0F);
                     double yd = ((double)yy - yp) / (yr / (double)2.0F);
                     double zd = ((double)zz - zp) / (zr / (double)2.0F);
                     double d = xd * xd + yd * yd + zd * zd;
                     if (d < (double)1.0F) {
                        grid[(xx * 16 + zz) * 8 + yy] = true;
                     }
                  }
               }
            }
         }

         BlockState fluid = ((BlockStateProvider)this.fluid.value()).getState(level, random, origin);

         for(int xx = 0; xx < 16; ++xx) {
            for(int zz = 0; zz < 16; ++zz) {
               for(int yy = 0; yy < 8; ++yy) {
                  boolean check = !grid[(xx * 16 + zz) * 8 + yy] && (xx < 15 && grid[((xx + 1) * 16 + zz) * 8 + yy] || xx > 0 && grid[((xx - 1) * 16 + zz) * 8 + yy] || zz < 15 && grid[(xx * 16 + zz + 1) * 8 + yy] || zz > 0 && grid[(xx * 16 + (zz - 1)) * 8 + yy] || yy < 7 && grid[(xx * 16 + zz) * 8 + yy + 1] || yy > 0 && grid[(xx * 16 + zz) * 8 + (yy - 1)]);
                  if (check) {
                     BlockPos offsetPos = origin.offset(xx, yy, zz);
                     BlockState blockState = level.getBlockState(offsetPos);
                     if (yy >= 4 && blockState.liquid()) {
                        return false;
                     }

                     if (yy < 4 && !blockState.isSolid() && blockState != fluid) {
                        return false;
                     }

                     if (!this.canPlaceFeature.test(level, offsetPos)) {
                        return false;
                     }
                  }
               }
            }
         }

         for(int xx = 0; xx < 16; ++xx) {
            for(int zz = 0; zz < 16; ++zz) {
               for(int yy = 0; yy < 8; ++yy) {
                  if (grid[(xx * 16 + zz) * 8 + yy]) {
                     BlockPos placePos = origin.offset(xx, yy, zz);
                     if (this.canReplaceWithAirOrFluid.test(level, placePos)) {
                        boolean placeAir = yy >= 4;
                        level.setBlock(placePos, placeAir ? AIR : fluid, 2);
                        if (placeAir) {
                           level.scheduleTick(placePos, AIR.getBlock(), 0);
                           this.markAboveForPostProcessing(level, placePos);
                        }
                     }
                  }
               }
            }
         }

         BlockState barrier = ((BlockStateProvider)this.barrier.value()).getState(level, random, origin);
         if (!barrier.isAir()) {
            for(int xx = 0; xx < 16; ++xx) {
               for(int zz = 0; zz < 16; ++zz) {
                  for(int yy = 0; yy < 8; ++yy) {
                     boolean check = !grid[(xx * 16 + zz) * 8 + yy] && (xx < 15 && grid[((xx + 1) * 16 + zz) * 8 + yy] || xx > 0 && grid[((xx - 1) * 16 + zz) * 8 + yy] || zz < 15 && grid[(xx * 16 + zz + 1) * 8 + yy] || zz > 0 && grid[(xx * 16 + (zz - 1)) * 8 + yy] || yy < 7 && grid[(xx * 16 + zz) * 8 + yy + 1] || yy > 0 && grid[(xx * 16 + zz) * 8 + (yy - 1)]);
                     if (check && (yy < 4 || random.nextInt(2) != 0)) {
                        BlockPos offset = origin.offset(xx, yy, zz);
                        BlockState blockState = level.getBlockState(offset);
                        if (blockState.isSolid() && this.canReplaceWithBarrier.test(level, offset)) {
                           BlockPos barrierPos = origin.offset(xx, yy, zz);
                           level.setBlock(barrierPos, barrier, 2);
                           this.markAboveForPostProcessing(level, barrierPos);
                        }
                     }
                  }
               }
            }
         }

         if (fluid.getFluidState().is(FluidTags.WATER)) {
            for(int xx = 0; xx < 16; ++xx) {
               for(int zz = 0; zz < 16; ++zz) {
                  int yy = 4;
                  BlockPos offset = origin.offset(xx, 4, zz);
                  if (((Biome)level.getBiome(offset).value()).shouldFreeze(level, offset, false) && this.canReplaceWithAirOrFluid.test(level, offset)) {
                     level.setBlock(offset, Blocks.ICE.defaultBlockState(), 2);
                  }
               }
            }
         }

         return true;
      }
   }

   static {
      AIR = Blocks.CAVE_AIR.defaultBlockState();
   }
}
