package net.minecraft.world.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

public class Giant extends Monster {
   public Giant(EntityType<? extends Giant> p_32788_, Level p_32789_) {
      super(p_32788_, p_32789_);
   }

   protected float getStandingEyeHeight(Pose p_32794_, EntityDimensions p_32795_) {
      return 10.440001F;
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, (double)100.0F).add(Attributes.MOVEMENT_SPEED, (double)0.5F).add(Attributes.ATTACK_DAMAGE, (double)50.0F);
   }

   public float getWalkTargetValue(BlockPos p_32791_, LevelReader p_32792_) {
      return p_32792_.getPathfindingCostFromLightLevels(p_32791_);
   }
}
