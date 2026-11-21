package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WallSkullBlock extends AbstractSkullBlock {
   public static final DirectionProperty FACING;
   private static final Map<Direction, VoxelShape> AABBS;

   public WallSkullBlock(SkullBlock.Type p_58101_, BlockBehaviour.Properties p_58102_) {
      super(p_58101_, p_58102_);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
   }

   public String getDescriptionId() {
      return this.asItem().getDescriptionId();
   }

   public VoxelShape getShape(BlockState p_58114_, BlockGetter p_58115_, BlockPos p_58116_, CollisionContext p_58117_) {
      return (VoxelShape)AABBS.get(p_58114_.getValue(FACING));
   }

   public BlockState getStateForPlacement(BlockPlaceContext p_58104_) {
      BlockState $$1 = this.defaultBlockState();
      BlockGetter $$2 = p_58104_.getLevel();
      BlockPos $$3 = p_58104_.getClickedPos();
      Direction[] $$4 = p_58104_.getNearestLookingDirections();

      for(Direction $$5 : $$4) {
         if ($$5.getAxis().isHorizontal()) {
            Direction $$6 = $$5.getOpposite();
            $$1 = (BlockState)$$1.setValue(FACING, $$6);
            if (!$$2.getBlockState($$3.relative($$5)).canBeReplaced(p_58104_)) {
               return $$1;
            }
         }
      }

      return null;
   }

   public BlockState rotate(BlockState p_58109_, Rotation p_58110_) {
      return (BlockState)p_58109_.setValue(FACING, p_58110_.rotate((Direction)p_58109_.getValue(FACING)));
   }

   public BlockState mirror(BlockState p_58106_, Mirror p_58107_) {
      return p_58106_.rotate(p_58107_.getRotation((Direction)p_58106_.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_58112_) {
      p_58112_.add(FACING);
   }

   static {
      FACING = HorizontalDirectionalBlock.FACING;
      AABBS = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.box((double)4.0F, (double)4.0F, (double)8.0F, (double)12.0F, (double)12.0F, (double)16.0F), Direction.SOUTH, Block.box((double)4.0F, (double)4.0F, (double)0.0F, (double)12.0F, (double)12.0F, (double)8.0F), Direction.EAST, Block.box((double)0.0F, (double)4.0F, (double)4.0F, (double)8.0F, (double)12.0F, (double)12.0F), Direction.WEST, Block.box((double)8.0F, (double)4.0F, (double)4.0F, (double)16.0F, (double)12.0F, (double)12.0F)));
   }
}
