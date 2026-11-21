package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraftforge.common.Tags;

public class ChorusPlantBlock extends PipeBlock {
   public ChorusPlantBlock(BlockBehaviour.Properties p_51707_) {
      super(0.3125F, p_51707_);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(NORTH, false)).setValue(EAST, false)).setValue(SOUTH, false)).setValue(WEST, false)).setValue(UP, false)).setValue(DOWN, false));
   }

   public BlockState getStateForPlacement(BlockPlaceContext p_51709_) {
      return this.getStateForPlacement(p_51709_.getLevel(), p_51709_.getClickedPos());
   }

   public BlockState getStateForPlacement(BlockGetter p_51711_, BlockPos p_51712_) {
      BlockState blockstate = p_51711_.getBlockState(p_51712_.below());
      BlockState blockstate1 = p_51711_.getBlockState(p_51712_.above());
      BlockState blockstate2 = p_51711_.getBlockState(p_51712_.north());
      BlockState blockstate3 = p_51711_.getBlockState(p_51712_.east());
      BlockState blockstate4 = p_51711_.getBlockState(p_51712_.south());
      BlockState blockstate5 = p_51711_.getBlockState(p_51712_.west());
      return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(DOWN, blockstate.is(this) || blockstate.is(Blocks.CHORUS_FLOWER) || blockstate.is(Blocks.END_STONE) || blockstate.is(Tags.Blocks.CHORUS_ADDITIONALLY_GROWS_ON))).setValue(UP, blockstate1.is(this) || blockstate1.is(Blocks.CHORUS_FLOWER))).setValue(NORTH, blockstate2.is(this) || blockstate2.is(Blocks.CHORUS_FLOWER))).setValue(EAST, blockstate3.is(this) || blockstate3.is(Blocks.CHORUS_FLOWER))).setValue(SOUTH, blockstate4.is(this) || blockstate4.is(Blocks.CHORUS_FLOWER))).setValue(WEST, blockstate5.is(this) || blockstate5.is(Blocks.CHORUS_FLOWER));
   }

   public BlockState updateShape(BlockState p_51728_, Direction p_51729_, BlockState p_51730_, LevelAccessor p_51731_, BlockPos p_51732_, BlockPos p_51733_) {
      if (!p_51728_.canSurvive(p_51731_, p_51732_)) {
         p_51731_.scheduleTick(p_51732_, (Block)this, 1);
         return super.updateShape(p_51728_, p_51729_, p_51730_, p_51731_, p_51732_, p_51733_);
      } else {
         boolean flag = p_51730_.is(this) || p_51730_.is(Blocks.CHORUS_FLOWER) || p_51729_ == Direction.DOWN && (p_51730_.is(Blocks.END_STONE) || p_51730_.is(Tags.Blocks.CHORUS_ADDITIONALLY_GROWS_ON));
         return (BlockState)p_51728_.setValue((Property)PROPERTY_BY_DIRECTION.get(p_51729_), flag);
      }
   }

   public void tick(BlockState p_220985_, ServerLevel p_220986_, BlockPos p_220987_, RandomSource p_220988_) {
      if (!p_220985_.canSurvive(p_220986_, p_220987_)) {
         p_220986_.destroyBlock(p_220987_, true);
      }

   }

   public boolean canSurvive(BlockState p_51724_, LevelReader p_51725_, BlockPos p_51726_) {
      BlockState blockstate = p_51725_.getBlockState(p_51726_.below());
      boolean flag = !p_51725_.getBlockState(p_51726_.above()).isAir() && !blockstate.isAir();

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         BlockPos blockpos = p_51726_.relative(direction);
         BlockState blockstate1 = p_51725_.getBlockState(blockpos);
         if (blockstate1.is(this)) {
            if (flag) {
               return false;
            }

            BlockState blockstate2 = p_51725_.getBlockState(blockpos.below());
            if (blockstate2.is(this) || blockstate2.is(Blocks.END_STONE) || blockstate2.is(Tags.Blocks.CHORUS_ADDITIONALLY_GROWS_ON)) {
               return true;
            }
         }
      }

      return blockstate.is(this) || blockstate.is(Blocks.END_STONE) || blockstate.is(Tags.Blocks.CHORUS_ADDITIONALLY_GROWS_ON);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_51735_) {
      p_51735_.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
   }

   public boolean isPathfindable(BlockState p_51719_, BlockGetter p_51720_, BlockPos p_51721_, PathComputationType p_51722_) {
      return false;
   }
}
