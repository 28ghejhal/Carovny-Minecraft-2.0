package net.minecraft.world.level.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PiglinWallSkullBlock extends WallSkullBlock {
   private static final Map<Direction, VoxelShape> AABBS;

   public PiglinWallSkullBlock(BlockBehaviour.Properties p_261530_) {
      super(SkullBlock.Types.PIGLIN, p_261530_);
   }

   public VoxelShape getShape(BlockState p_261765_, BlockGetter p_261604_, BlockPos p_261948_, CollisionContext p_261889_) {
      return (VoxelShape)AABBS.get(p_261765_.getValue(FACING));
   }

   static {
      AABBS = Maps.immutableEnumMap(Map.of(Direction.NORTH, Block.box((double)3.0F, (double)4.0F, (double)8.0F, (double)13.0F, (double)12.0F, (double)16.0F), Direction.SOUTH, Block.box((double)3.0F, (double)4.0F, (double)0.0F, (double)13.0F, (double)12.0F, (double)8.0F), Direction.EAST, Block.box((double)0.0F, (double)4.0F, (double)3.0F, (double)8.0F, (double)12.0F, (double)13.0F), Direction.WEST, Block.box((double)8.0F, (double)4.0F, (double)3.0F, (double)16.0F, (double)12.0F, (double)13.0F)));
   }
}
