package net.minecraft.world.level.block;

import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WeepingVinesBlock extends GrowingPlantHeadBlock {
   private static final VoxelShape SHAPE = Block.column((double)8.0F, (double)9.0F, (double)16.0F);

   public WeepingVinesBlock(final BlockBehaviour.Properties properties) {
      super(properties, Direction.DOWN, SHAPE, false, 0.1);
   }

   protected int getBlocksToGrowWhenBonemealed(final RandomSource random) {
      return NetherVines.getBlocksToGrowWhenBonemealed(random);
   }

   protected Block getBodyBlock() {
      return Blocks.WEEPING_VINES_PLANT;
   }

   protected boolean canGrowInto(final BlockState state) {
      return NetherVines.isValidGrowthState(state);
   }
}
