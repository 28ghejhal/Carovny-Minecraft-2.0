package net.minecraft.world.level.block;

import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TwistingVinesBlock extends GrowingPlantHeadBlock {
   public static final VoxelShape SHAPE = Block.box((double)4.0F, (double)0.0F, (double)4.0F, (double)12.0F, (double)15.0F, (double)12.0F);

   public TwistingVinesBlock(BlockBehaviour.Properties p_154864_) {
      super(p_154864_, Direction.UP, SHAPE, false, 0.1);
   }

   protected int getBlocksToGrowWhenBonemealed(RandomSource p_222649_) {
      return NetherVines.getBlocksToGrowWhenBonemealed(p_222649_);
   }

   protected Block getBodyBlock() {
      return Blocks.TWISTING_VINES_PLANT;
   }

   protected boolean canGrowInto(BlockState p_154869_) {
      return NetherVines.isValidGrowthState(p_154869_);
   }
}
