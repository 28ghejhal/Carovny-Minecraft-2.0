package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BaseCoralFanBlock extends BaseCoralPlantTypeBlock {
   private static final VoxelShape AABB = Block.box((double)2.0F, (double)0.0F, (double)2.0F, (double)14.0F, (double)4.0F, (double)14.0F);

   public BaseCoralFanBlock(BlockBehaviour.Properties p_49106_) {
      super(p_49106_);
   }

   public VoxelShape getShape(BlockState p_49108_, BlockGetter p_49109_, BlockPos p_49110_, CollisionContext p_49111_) {
      return AABB;
   }
}
