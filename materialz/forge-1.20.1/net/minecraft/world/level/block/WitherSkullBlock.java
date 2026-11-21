package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;

public class WitherSkullBlock extends SkullBlock {
   @Nullable
   private static BlockPattern witherPatternFull;
   @Nullable
   private static BlockPattern witherPatternBase;

   public WitherSkullBlock(BlockBehaviour.Properties p_58254_) {
      super(SkullBlock.Types.WITHER_SKELETON, p_58254_);
   }

   public void setPlacedBy(Level p_58260_, BlockPos p_58261_, BlockState p_58262_, @Nullable LivingEntity p_58263_, ItemStack p_58264_) {
      super.setPlacedBy(p_58260_, p_58261_, p_58262_, p_58263_, p_58264_);
      BlockEntity $$5 = p_58260_.getBlockEntity(p_58261_);
      if ($$5 instanceof SkullBlockEntity) {
         checkSpawn(p_58260_, p_58261_, (SkullBlockEntity)$$5);
      }

   }

   public static void checkSpawn(Level p_58256_, BlockPos p_58257_, SkullBlockEntity p_58258_) {
      if (!p_58256_.isClientSide) {
         BlockState $$3 = p_58258_.getBlockState();
         boolean $$4 = $$3.is(Blocks.WITHER_SKELETON_SKULL) || $$3.is(Blocks.WITHER_SKELETON_WALL_SKULL);
         if ($$4 && p_58257_.getY() >= p_58256_.getMinBuildHeight() && p_58256_.getDifficulty() != Difficulty.PEACEFUL) {
            BlockPattern.BlockPatternMatch $$5 = getOrCreateWitherFull().find(p_58256_, p_58257_);
            if ($$5 != null) {
               WitherBoss $$6 = EntityType.WITHER.create(p_58256_);
               if ($$6 != null) {
                  CarvedPumpkinBlock.clearPatternBlocks(p_58256_, $$5);
                  BlockPos $$7 = $$5.getBlock(1, 2, 0).getPos();
                  $$6.moveTo((double)$$7.getX() + (double)0.5F, (double)$$7.getY() + 0.55, (double)$$7.getZ() + (double)0.5F, $$5.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F, 0.0F);
                  $$6.yBodyRot = $$5.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F;
                  $$6.makeInvulnerable();

                  for(ServerPlayer $$8 : p_58256_.getEntitiesOfClass(ServerPlayer.class, $$6.getBoundingBox().inflate((double)50.0F))) {
                     CriteriaTriggers.SUMMONED_ENTITY.trigger($$8, $$6);
                  }

                  p_58256_.addFreshEntity($$6);
                  CarvedPumpkinBlock.updatePatternBlocks(p_58256_, $$5);
               }

            }
         }
      }
   }

   public static boolean canSpawnMob(Level p_58268_, BlockPos p_58269_, ItemStack p_58270_) {
      if (p_58270_.is(Items.WITHER_SKELETON_SKULL) && p_58269_.getY() >= p_58268_.getMinBuildHeight() + 2 && p_58268_.getDifficulty() != Difficulty.PEACEFUL && !p_58268_.isClientSide) {
         return getOrCreateWitherBase().find(p_58268_, p_58269_) != null;
      } else {
         return false;
      }
   }

   private static BlockPattern getOrCreateWitherFull() {
      if (witherPatternFull == null) {
         witherPatternFull = BlockPatternBuilder.start().aisle("^^^", "###", "~#~").where('#', (p_58272_) -> p_58272_.getState().is(BlockTags.WITHER_SUMMON_BASE_BLOCKS)).where('^', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.WITHER_SKELETON_SKULL).or(BlockStatePredicate.forBlock(Blocks.WITHER_SKELETON_WALL_SKULL)))).where('~', (p_284877_) -> p_284877_.getState().isAir()).build();
      }

      return witherPatternFull;
   }

   private static BlockPattern getOrCreateWitherBase() {
      if (witherPatternBase == null) {
         witherPatternBase = BlockPatternBuilder.start().aisle("   ", "###", "~#~").where('#', (p_58266_) -> p_58266_.getState().is(BlockTags.WITHER_SUMMON_BASE_BLOCKS)).where('~', (p_284878_) -> p_284878_.getState().isAir()).build();
      }

      return witherPatternBase;
   }
}
