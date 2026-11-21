package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class NetherPortalBlock extends Block {
   public static final EnumProperty<Direction.Axis> AXIS;
   protected static final int AABB_OFFSET = 2;
   protected static final VoxelShape X_AXIS_AABB;
   protected static final VoxelShape Z_AXIS_AABB;

   public NetherPortalBlock(BlockBehaviour.Properties p_54909_) {
      super(p_54909_);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AXIS, Direction.Axis.X));
   }

   public VoxelShape getShape(BlockState p_54942_, BlockGetter p_54943_, BlockPos p_54944_, CollisionContext p_54945_) {
      switch ((Direction.Axis)p_54942_.getValue(AXIS)) {
         case Z:
            return Z_AXIS_AABB;
         case X:
         default:
            return X_AXIS_AABB;
      }
   }

   public void randomTick(BlockState p_221799_, ServerLevel p_221800_, BlockPos p_221801_, RandomSource p_221802_) {
      if (p_221800_.dimensionType().natural() && p_221800_.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && p_221802_.nextInt(2000) < p_221800_.getDifficulty().getId()) {
         while(p_221800_.getBlockState(p_221801_).is(this)) {
            p_221801_ = p_221801_.below();
         }

         if (p_221800_.getBlockState(p_221801_).isValidSpawn(p_221800_, p_221801_, EntityType.ZOMBIFIED_PIGLIN)) {
            Entity $$4 = EntityType.ZOMBIFIED_PIGLIN.spawn(p_221800_, p_221801_.above(), MobSpawnType.STRUCTURE);
            if ($$4 != null) {
               $$4.setPortalCooldown();
            }
         }
      }

   }

   public BlockState updateShape(BlockState p_54928_, Direction p_54929_, BlockState p_54930_, LevelAccessor p_54931_, BlockPos p_54932_, BlockPos p_54933_) {
      Direction.Axis $$6 = p_54929_.getAxis();
      Direction.Axis $$7 = (Direction.Axis)p_54928_.getValue(AXIS);
      boolean $$8 = $$7 != $$6 && $$6.isHorizontal();
      return !$$8 && !p_54930_.is(this) && !(new PortalShape(p_54931_, p_54932_, $$7)).isComplete() ? Blocks.AIR.defaultBlockState() : super.updateShape(p_54928_, p_54929_, p_54930_, p_54931_, p_54932_, p_54933_);
   }

   public void entityInside(BlockState p_54915_, Level p_54916_, BlockPos p_54917_, Entity p_54918_) {
      if (p_54918_.canChangeDimensions()) {
         p_54918_.handleInsidePortal(p_54917_);
      }

   }

   public void animateTick(BlockState p_221794_, Level p_221795_, BlockPos p_221796_, RandomSource p_221797_) {
      if (p_221797_.nextInt(100) == 0) {
         p_221795_.playLocalSound((double)p_221796_.getX() + (double)0.5F, (double)p_221796_.getY() + (double)0.5F, (double)p_221796_.getZ() + (double)0.5F, SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 0.5F, p_221797_.nextFloat() * 0.4F + 0.8F, false);
      }

      for(int $$4 = 0; $$4 < 4; ++$$4) {
         double $$5 = (double)p_221796_.getX() + p_221797_.nextDouble();
         double $$6 = (double)p_221796_.getY() + p_221797_.nextDouble();
         double $$7 = (double)p_221796_.getZ() + p_221797_.nextDouble();
         double $$8 = ((double)p_221797_.nextFloat() - (double)0.5F) * (double)0.5F;
         double $$9 = ((double)p_221797_.nextFloat() - (double)0.5F) * (double)0.5F;
         double $$10 = ((double)p_221797_.nextFloat() - (double)0.5F) * (double)0.5F;
         int $$11 = p_221797_.nextInt(2) * 2 - 1;
         if (!p_221795_.getBlockState(p_221796_.west()).is(this) && !p_221795_.getBlockState(p_221796_.east()).is(this)) {
            $$5 = (double)p_221796_.getX() + (double)0.5F + (double)0.25F * (double)$$11;
            $$8 = (double)(p_221797_.nextFloat() * 2.0F * (float)$$11);
         } else {
            $$7 = (double)p_221796_.getZ() + (double)0.5F + (double)0.25F * (double)$$11;
            $$10 = (double)(p_221797_.nextFloat() * 2.0F * (float)$$11);
         }

         p_221795_.addParticle(ParticleTypes.PORTAL, $$5, $$6, $$7, $$8, $$9, $$10);
      }

   }

   public ItemStack getCloneItemStack(BlockGetter p_54911_, BlockPos p_54912_, BlockState p_54913_) {
      return ItemStack.EMPTY;
   }

   public BlockState rotate(BlockState p_54925_, Rotation p_54926_) {
      switch (p_54926_) {
         case COUNTERCLOCKWISE_90:
         case CLOCKWISE_90:
            switch ((Direction.Axis)p_54925_.getValue(AXIS)) {
               case Z -> {
                  return (BlockState)p_54925_.setValue(AXIS, Direction.Axis.X);
               }
               case X -> {
                  return (BlockState)p_54925_.setValue(AXIS, Direction.Axis.Z);
               }
               default -> {
                  return p_54925_;
               }
            }
         default:
            return p_54925_;
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_54935_) {
      p_54935_.add(AXIS);
   }

   static {
      AXIS = BlockStateProperties.HORIZONTAL_AXIS;
      X_AXIS_AABB = Block.box((double)0.0F, (double)0.0F, (double)6.0F, (double)16.0F, (double)16.0F, (double)10.0F);
      Z_AXIS_AABB = Block.box((double)6.0F, (double)0.0F, (double)0.0F, (double)10.0F, (double)16.0F, (double)16.0F);
   }
}
