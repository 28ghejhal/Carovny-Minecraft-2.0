package net.minecraft.world.level.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TwistingVinesPlantBlock extends GrowingPlantBodyBlock {
   private static final VoxelShape SHAPE = Block.column((double)8.0F, (double)0.0F, (double)16.0F);

   public TwistingVinesPlantBlock(final BlockBehaviour.Properties properties) {
      super(properties, Direction.UP, SHAPE, false);
   }

   protected GrowingPlantHeadBlock getHeadBlock() {
      return (GrowingPlantHeadBlock)Blocks.TWISTING_VINES;
   }
}
